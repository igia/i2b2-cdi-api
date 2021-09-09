/**
* This Source Code Form is subject to the terms of the Mozilla Public License, v.
* 2.0 with a Healthcare Disclaimer.
* A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
* be found under the top level directory, named LICENSE.
* If a copy of the MPL was not distributed with this file, You can obtain one at
* http://mozilla.org/MPL/2.0/.
* If a copy of the Healthcare Disclaimer was not distributed with this file, You
* can obtain one at the project website https://github.com/igia.
*
* Copyright (C) 2021-2022 Persistent Systems, Inc.
*/

package io.igia.i2b2.cdi.concept.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.common.dto.Operator;
import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.common.exception.I2b2DataValidationException;
import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.concept.dao.ConceptDao;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.dto.ConceptSearchDto;
import io.igia.i2b2.cdi.concept.dto.PathFilterDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptType;
import io.igia.i2b2.cdi.derivedconcept.util.ConceptUtil;
import io.igia.i2b2.cdi.observation.domain.ValueTypeCode;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyDto;
import io.igia.i2b2.cdi.ontology.service.OntologyConceptService;

@Service
@Transactional(readOnly = true)
public class ConceptServiceImpl implements ConceptService {

    public static final String INVALID_CONCEPT_CODE = "Invalid concept code.";
    private static final String SOURCE = "DEMO";
    private static final String NULL_CONCEPT = "Concept is null.";
    private static final String BACK_SLASH = "\\";
    protected static final String CONCEPT_PATH = "concept_path";
    protected static final String ID = "id";
    private static final Logger logger = LoggerFactory.getLogger(ConceptServiceImpl.class);
    private final ConceptDao conceptDao;
    private final OntologyConceptService ontologyConceptService;
    private final ConceptUtil conceptUtil;

    public ConceptServiceImpl(ConceptDao conceptDao, OntologyConceptService ontologyConceptService,
	    ConceptUtil conceptUtil) {
	this.conceptDao = conceptDao;
	this.ontologyConceptService = ontologyConceptService;
	this.conceptUtil = conceptUtil;
    }

    @Override
    public PaginationResult<ConceptDto> getConcepts(ConceptSearchDto inConceptSearchDto) {
        // In this case, If sortKey is not provided then it is default to
        // concept_path
        PageableDto pageableDto = inConceptSearchDto.getPageableDto();
        if (pageableDto != null) {
            String sortBy = pageableDto.getSortBy();
            if (StringUtils.isEmpty(sortBy) || (!StringUtils.isEmpty(sortBy) && sortBy.equals(ID))) {
                pageableDto.setSortBy(CONCEPT_PATH);
            }
            inConceptSearchDto.setPageableDto(pageableDto);
        }

        // Filter concepts by 'STARTSWITH'
        List<String> conceptPaths = new ArrayList<>();
        PathFilterDto pathFilterDto = inConceptSearchDto.getPathFilterDto();
        if (pathFilterDto != null
                && (pathFilterDto.getOpertaor() != null && pathFilterDto.getOpertaor().equals(Operator.STARTSWITH))) {
            String conceptPath = pathFilterDto.getPath();
            int count = StringUtils.countOccurrencesOf(conceptPath, BACK_SLASH);
            List<Integer> levels = new ArrayList<>();
            if (count == 1) {
                levels = Arrays.asList(0, 1);
            }
            if (count > 1) {
                levels = Arrays.asList(count);
            }
            OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();
            ontologyConceptSearchDto.setConceptPath(conceptPath);
            ontologyConceptSearchDto.setConceptLevels(levels);
            List<OntologyConceptDto> ontologyConcepts = ontologyConceptService
                    .getOntologyConceptsByLevel(ontologyConceptSearchDto);

            ontologyConcepts.stream().forEach(ontologyConcept -> {
                conceptPaths.add(ontologyConcept.getFullName());
            });
        }
        
        ConceptSearchDto conceptSearchDto = new ConceptSearchDto(inConceptSearchDto);
        conceptSearchDto.setConceptPaths(conceptPaths);
        PaginationResult<ConceptDto> concepts = conceptDao.findConcepts(conceptSearchDto);
        concepts.setTotalCount(conceptDao.getTotalCount(conceptSearchDto));
        concepts.setRecords(getConceptDataType(concepts.getRecords()));
        return concepts;
    }

    private List<ConceptDto> getConceptDataType(List<ConceptDto> concepts) {
	OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();
	ontologyConceptSearchDto
		.setConceptPaths(concepts.stream().map(ConceptDto::getConceptPath).collect(Collectors.toList()));

	Map<String, List<OntologyConceptDto>> ontologyConceptsMap = ontologyConceptService
		.getOntologyConceptsWithDataType(ontologyConceptSearchDto).stream()
		.collect(Collectors.groupingBy(OntologyConceptDto::getFullName));

	concepts.forEach(conceptDto -> {
	    List<OntologyConceptDto> ontologyConcepts = ontologyConceptsMap.getOrDefault(conceptDto.getConceptPath(),
		    Collections.emptyList());
	    if (ontologyConcepts.isEmpty()) {
		logger.debug("No ontology concept matched against concept path : {}", conceptDto.getConceptPath());
		// defaults to String
		conceptDto.setValueType(ValueTypeCode.TEXT);
	    } else {
		logger.debug("{} number of ontology concepts got mapped against concept path.",
			ontologyConcepts.size());

		// Considering first ontology concept to get data type associated with concept
		// in the list
		conceptDto.setValueType(mapValueTypeFromConceptDataType(ontologyConcepts.get(0).getDataType()));
	    }
	});
	return concepts;
    }

    private ValueTypeCode mapValueTypeFromConceptDataType(ConceptDataType dataType) {
	switch (dataType) {
	case STRING:
	case ENUM:
	    return ValueTypeCode.TEXT;
	case FLOAT:
	case INTEGER:
	case POS_FLOAT:
	case POS_INTEGER:
	    return ValueTypeCode.NUMERIC;
	case LARGE_STRING:
	    return ValueTypeCode.RAW_TEXT;
	default:
	    return ValueTypeCode.NONE;
	}
    }

    @Override
    public void validate(ConceptSearchDto conceptSearchDto) {
	if (getConcepts(conceptSearchDto).getRecords().isEmpty()) {
	    throw new I2b2DataValidationException(INVALID_CONCEPT_CODE);
	}
    }

    public List<ConceptDto> getConceptsWithDataType(ConceptSearchDto conceptSearchDto) {
	List<ConceptDto> concepts = getConcepts(conceptSearchDto).getRecords();

	logger.debug("{} number of concepts matched.", concepts.size());

	OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();
	ontologyConceptSearchDto
		.setConceptPaths(concepts.stream().map(ConceptDto::getConceptPath).collect(Collectors.toList()));

	Map<String, List<OntologyConceptDto>> ontologyConceptsMap = ontologyConceptService
		.getOntologyConceptsWithDataType(ontologyConceptSearchDto).stream()
		.collect(Collectors.groupingBy(OntologyConceptDto::getFullName));

	concepts.forEach(conceptDto -> {
	    List<OntologyConceptDto> ontologyConcepts = ontologyConceptsMap.getOrDefault(conceptDto.getConceptPath(),
		    Collections.emptyList());
	    if (ontologyConcepts.isEmpty()) {
		logger.debug("No ontology concept matched against concept path : {}", conceptDto.getConceptPath());
		// defaults to String
		conceptDto.setDataType(ConceptDataType.STRING);
	    } else {
		logger.debug("{} number of ontology concepts got mapped against concept path.",
			ontologyConcepts.size());

		// Considering first ontology concept to get data type associated with concept
		// in the list
		conceptDto.setDataType(ontologyConcepts.get(0).getDataType());
	    }
	});
	return concepts;
    }

    @Override
    @Transactional(readOnly = false)
    public ConceptDto addConcept(ConceptDto conceptDto) {
	if (conceptDto == null)
	    throw new I2b2Exception(NULL_CONCEPT);

	ConceptDto conceptRecord = new ConceptDto();
	conceptUtil.validateConceptPath(conceptDto.getConceptPath());
	String path = processConceptPath(conceptDto.getConceptPath());

	conceptRecord.setCode(conceptDto.getCode());
	conceptRecord.setConceptPath(conceptUtil.getFullPath(path));
	conceptRecord.setName(conceptUtil.getConceptName(path));
	conceptRecord.setSource(StringUtils.isEmpty(conceptDto.getSource()) ? SOURCE : conceptDto.getSource());

	int updateCount = conceptDao.addConcept(conceptRecord);

	if (updateCount == 0)
	    throw new I2b2Exception("Could not add concept record.");

	logger.info("Concept added.");

	return conceptRecord;
    }

    @Override
    public String processConceptPath(String path) {
	path = conceptUtil.removeSeparatorAtFirstAndLast(path);
	return path;
    }

    @Override
    @Transactional(readOnly = false)
    public ConceptDto updateConcept(ConceptDto conceptDto, String existingConceptPath) {
	if (conceptDto == null)
	    throw new I2b2Exception(NULL_CONCEPT);

	if (StringUtils.isEmpty(existingConceptPath))
	    throw new I2b2Exception("Concept path is null or empty. Could not update concept.");

	ConceptDto conceptRecord = new ConceptDto();
	String path = conceptDto.getConceptPath();

	conceptUtil.validateConceptPath(path);

	path = conceptUtil.removeSeparatorAtFirstAndLast(path);

	conceptRecord.setCode(conceptDto.getCode());
	conceptRecord.setConceptPath(conceptUtil.getFullPath(path));
	conceptRecord.setName(conceptUtil.getConceptName(path));
	conceptRecord.setSource(SOURCE);

	int updateCount = conceptDao.updateConcept(conceptRecord, existingConceptPath);

	if (updateCount == 0)
	    throw new I2b2Exception("Could not update concept record.");

	logger.info("Concept updated.");

	return conceptRecord;
    }

    @Override
    @Transactional(readOnly = false)
    public ConceptDto deleteConcept(ConceptDto conceptDto) {
	if (conceptDto == null)
	    throw new I2b2Exception(NULL_CONCEPT);

	if (StringUtils.isEmpty(conceptDto.getConceptPath()))
	    throw new I2b2Exception("Concept path is null or empty. Could not delete concept.");

	ConceptDto conceptRecord = new ConceptDto();
	conceptRecord.setUpdateTime(LocalDateTime.now());
	conceptRecord.setConceptPath(conceptDto.getConceptPath());

	conceptDao.deleteConcept(conceptRecord);

	logger.info("Concept deleted.");

	return conceptRecord;
    }

    @Override
    @Transactional(readOnly = false)
    public void createConcept(ConceptDto conceptDto) {

	/**
	 * Adding concept
	 */
	ConceptDto concept = new ConceptDto();
	concept.setCode(conceptDto.getCode());
	concept.setConceptPath(conceptDto.getConceptPath());
	concept.setSource(conceptDto.getSource());
	addConcept(concept);

	/**
	 * Adding Ontology
	 */
	OntologyDto ontology = new OntologyDto();
	ontology.setPath(conceptDto.getConceptPath());
	ontology.setCode(conceptDto.getCode());
	ontology.setcColumnDatatype(conceptDto.getDataType() == null ? "T"
		: mapDerivedConceptTypeFromConceptDataType(conceptDto.getDataType()).getType());
	ontology.setMetadata(conceptDto.getMetadata());
	ontologyConceptService.addOntology(ontology);
    }

    private DerivedConceptType mapDerivedConceptTypeFromConceptDataType(ConceptDataType type) {
	switch (type) {
	case FLOAT:
	case INTEGER:
	case POS_FLOAT:
	case POS_INTEGER:
	    return DerivedConceptType.NUMERIC;
	default:
	    return DerivedConceptType.TEXTUAL;
	}
    }
}
