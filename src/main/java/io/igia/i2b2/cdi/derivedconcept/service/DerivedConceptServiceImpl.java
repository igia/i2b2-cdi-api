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


package io.igia.i2b2.cdi.derivedconcept.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.common.dto.WarningDto;
import io.igia.i2b2.cdi.common.exception.I2B2DataNotFoundException;
import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.service.ConceptService;
import io.igia.i2b2.cdi.derivedconcept.config.QueryMasterConfig;
import io.igia.i2b2.cdi.derivedconcept.dao.DerivedConceptDao;
import io.igia.i2b2.cdi.derivedconcept.dao.DerivedConceptDependencyDao;
import io.igia.i2b2.cdi.derivedconcept.dao.DerivedConceptJobDetailsDao;
import io.igia.i2b2.cdi.derivedconcept.dao.DerivedConceptQueryMasterDao;
import io.igia.i2b2.cdi.derivedconcept.dto.DependencyDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencyDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencySearchDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsFetchType;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsSearchDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptQueryMasterDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptTopologicalSortDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptType;
import io.igia.i2b2.cdi.derivedconcept.dto.Status;
import io.igia.i2b2.cdi.derivedconcept.util.ConceptUtil;
import io.igia.i2b2.cdi.derivedconcept.util.DerivedConceptTopologicalSortWrapper;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyDto;
import io.igia.i2b2.cdi.ontology.service.OntologyConceptService;

@Service
@Transactional(readOnly = true)
public class DerivedConceptServiceImpl implements DerivedConceptService {
    public static final Logger logger = LoggerFactory.getLogger(DerivedConceptServiceImpl.class);
    private final DerivedConceptDao derivedConceptDao;
    private final OntologyConceptService ontologyConceptService;
    private final ConceptService conceptService;
    private final DerivedConceptJobDetailsDao derivedConceptJobDetailsDao;
    private final DerivedConceptDependencyDao derivedConceptDependencyDao;
    private final DerivedConceptTopologicalSortWrapper derivedConceptTopologicalSortWrapper;
    private final ConceptUtil conceptUtil;
    private final DerivedConceptQueryMasterDao derivedConceptQueryMasterDao;
    private final QueryMasterConfig queryMasterConfig;

    public static final Integer STATUS_CODE = 400;
    public static final String CYCLIC_DEPENDENCY_WARNING = "Cyclic dependency detected between concept paths.";
    public static final String CHANGED_CONCEPT_PATH = "Concept path has changed, could not be updated";
    public static final String CYCLIC_DEPENDENCY_ERROR = "Execution of derived concept is 'FAILED' due to cyclic dependencies";
    public static final String CONCEPT_NOT_FOUND_ERROR = "No derived concept definition matched for id ";
    public static final String DELETE_PREVIOUS_DATA_QUERY = "DELETE FROM global_temp_table; DELETE FROM dx;";
    public static final String OBSERVATION_FACT_WRAPPER_QUERY = "SELECT DISTINCT dx.patient_num,'1970-01-01 00:00:00' AS start_date, '1970-01-01 00:00:00' AS end_date, -1 AS encounter_num, 0 AS provider_id, 'T' AS valtype_cd, '' AS tval_char, 0 AS nval_num FROM dx";
    public static final String QUERY_SEPARATOR_1 = "<\\*>";
    public static final String QUERY_SEPARATOR_2 = "\\\\#";
    public static final String HASH = "#";
    public static final String SINGLE_WHITE_SPACE = " ";
    public static final String LINE_SEPARATOR = "\n";
    public static final String SEMICOLON = ";";
    public static final String EMPTY_STRING = "";
    public static final int DEFAULT_NUMBER_OF_RECORDS = 50;

    public DerivedConceptServiceImpl(DerivedConceptDao derivedConceptDao, OntologyConceptService ontologyConceptService,
	    ConceptService conceptService, DerivedConceptJobDetailsDao derivedConceptJobDetailsDao,
	    DerivedConceptDependencyDao derivedConceptDependencyDao,
	    DerivedConceptTopologicalSortWrapper derivedConceptTopologicalSortWrapper, ConceptUtil conceptUtil,
	    DerivedConceptQueryMasterDao derivedConceptQueryMasterDao, QueryMasterConfig queryMasterConfig) {
	this.derivedConceptDao = derivedConceptDao;
	this.ontologyConceptService = ontologyConceptService;
	this.conceptService = conceptService;
	this.derivedConceptJobDetailsDao = derivedConceptJobDetailsDao;
	this.derivedConceptDependencyDao = derivedConceptDependencyDao;
	this.derivedConceptTopologicalSortWrapper = derivedConceptTopologicalSortWrapper;
	this.conceptUtil = conceptUtil;
	this.derivedConceptQueryMasterDao = derivedConceptQueryMasterDao;
	this.queryMasterConfig = queryMasterConfig;
    }

    @Override
    public DerivedConceptDto getDerivedConceptById(Integer id) {
	DerivedConceptDto derivedConcept = derivedConceptDao.findDerivedConceptById(id);

	if (derivedConcept != null) {
	    logger.info("Path From DB: {}", derivedConcept.getPath());
	    OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();

	    ontologyConceptSearchDto.setConceptPaths(new ArrayList<>(Arrays.asList(derivedConcept.getPath())));

	    Map<String, List<OntologyConceptDto>> ontologyConceptsMap = ontologyConceptService
		    .getOntologyConceptsWithDataType(ontologyConceptSearchDto).stream()
		    .collect(Collectors.groupingBy(OntologyConceptDto::getFullName));

	    List<OntologyConceptDto> ontologyConcepts = ontologyConceptsMap.getOrDefault(derivedConcept.getPath(),
		    Collections.emptyList());

	    if (ontologyConcepts.isEmpty()) {
		logger.debug("No ontology concept matched against path : {}", derivedConcept.getPath());
	    } else {
		if (!StringUtils.isEmpty(ontologyConcepts.get(0).getMetadataXml())) {
		    derivedConcept.setType(DerivedConceptType.NUMERIC);
		} else {
		    derivedConcept.setType(DerivedConceptType.TEXTUAL);
		}

		// get metadataxml associated with the concept
		derivedConcept.setMetadata(ontologyConcepts.get(0).getMetadataXml());
	    }

	    derivedConcept.setDependencies(getDerivedConceptDependencies(derivedConcept.getId()));
	    derivedConcept.setWarnings(null); // just to ignore field in json response
	}
	return derivedConcept;
    }

    private List<DependencyDto> getDerivedConceptDependencies(int derivedConceptId) {
	DerivedConceptDependencySearchDto derivedConceptDependencySearchDto = new DerivedConceptDependencySearchDto();
	derivedConceptDependencySearchDto.setDerivedConceptId(derivedConceptId);
	List<DerivedConceptDependencyDto> derivedConceptDependencies = derivedConceptDependencyDao
		.getDerivedConceptDependency(derivedConceptDependencySearchDto);

	List<DependencyDto> dependencies = new ArrayList<>();

	if (!derivedConceptDependencies.isEmpty()) {
	    dependencies = derivedConceptDependencies.stream().map(conceptPath -> {
		DependencyDto dependencyDto = new DependencyDto();
		dependencyDto.setPath(conceptPath.getParentConceptPath());
		return dependencyDto;
	    }).collect(Collectors.toList());
	}
	return dependencies;
    }

    @Override
    public List<DerivedConceptDto> getDerivedConcepts() {
	List<DerivedConceptDto> derivedConcepts = derivedConceptDao.findDerivedConcepts();

	if (!derivedConcepts.isEmpty()) {
	    OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();
	    ontologyConceptSearchDto.setConceptPaths(
		    derivedConcepts.stream().map(DerivedConceptDto::getPath).collect(Collectors.toList()));

	    Map<String, List<OntologyConceptDto>> ontologyConceptsMap = ontologyConceptService
		    .getOntologyConceptsWithDataType(ontologyConceptSearchDto).stream()
		    .collect(Collectors.groupingBy(OntologyConceptDto::getFullName));

	    derivedConcepts.forEach(derivedConceptDto -> {
		List<OntologyConceptDto> ontologyConcepts = ontologyConceptsMap
			.getOrDefault(derivedConceptDto.getPath(), Collections.emptyList());
		if (ontologyConcepts.isEmpty()) {
		    logger.debug("No ontology concept matched against path : {}", derivedConceptDto.getPath());
		} else {
		    final OntologyConceptDto ontologyConcept = ontologyConcepts.get(0);
		    final String metadataXml = ontologyConcept.getMetadataXml();
		    if (!StringUtils.isEmpty(metadataXml)) {
			derivedConceptDto
				.setType(mapDerivedConceptTypeFromConceptDataType(ontologyConcept.getDataType()));
		    } else {
			derivedConceptDto.setType(DerivedConceptType.TEXTUAL);
		    }
		    // get metadataxml associated with the concept
		    derivedConceptDto.setMetadata(metadataXml);
		}
		derivedConceptDto.setDependencies(getDerivedConceptDependencies(derivedConceptDto.getId()));
		derivedConceptDto.setWarnings(null); // just to ignore field in json response
	    });
	}
	return derivedConcepts;
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

    @Override
    @Transactional(readOnly = false)
    public DerivedConceptDto addDerivedConcept(DerivedConceptDto derivedConceptDto) {
	/**
	 * Adding Derived fact definition
	 */
	conceptUtil.validateConceptPath(derivedConceptDto.getPath());
	createParentConcept(derivedConceptDto.getPath());
	derivedConceptDto.setUpdatedOn(Instant.now());
	addConceptCodeToImportedQuery(derivedConceptDto);
	int derivedConceptId = derivedConceptDao.addDerivedConcept(derivedConceptDto);

	if (derivedConceptId == 0)
	    throw new I2b2Exception("Could not add derived concept record.");

	derivedConceptDto.setId(derivedConceptId);
	addConcept(derivedConceptDto);

	/**
	 * Add Derived Concept Dependencies
	 */
	if (derivedConceptDto.getDependencies() != null && !derivedConceptDto.getDependencies().isEmpty()) {
	    List<DerivedConceptDependencyDto> dependencies = prepareDerivedConceptDependencies(derivedConceptDto);
	    validateDerivedConceptDependencies(derivedConceptDto, dependencies);
	    addDerivedConceptDependencies(dependencies);
	}
	return derivedConceptDto;
    }

    private void addConceptCodeToImportedQuery(DerivedConceptDto derivedConceptDto) {
	/**
	 * If query imported from qt_query_master table
	 */
	String conceptCode = checkConceptCode(derivedConceptDto.getCode());
	String factQuery = addConceptCodeToQuery(conceptCode, derivedConceptDto.getFactQuery());
	derivedConceptDto.setCode(conceptCode);
	derivedConceptDto.setFactQuery(factQuery);
    }

    private void createParentConcept(String conceptPath) {
	Set<String> conceptPaths = getConceptPathHierarchy(conceptPath);
	conceptPaths.remove(conceptPath);

	List<String> concepts = conceptPaths.stream().filter(concept -> {
	    OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();
	    ontologyConceptSearchDto.setConceptPath(concept);
	    return ontologyConceptService.findOntologyConcepts(ontologyConceptSearchDto).isEmpty();
	}).collect(Collectors.toList());

	concepts.stream().forEach(concept -> {
	    DerivedConceptDto derivedConceptDto = new DerivedConceptDto();
	    derivedConceptDto.setPath(concept);
	    addConcept(derivedConceptDto);
	});
    }

    private void addConcept(DerivedConceptDto derivedConceptDto) {
	/**
	 * Adding concept
	 */
	ConceptDto conceptDto = new ConceptDto();
	conceptDto.setCode(derivedConceptDto.getCode());
	conceptDto.setConceptPath(derivedConceptDto.getPath());
	conceptService.addConcept(conceptDto);

	/**
	 * Adding Ontology
	 */
	OntologyDto ontology = new OntologyDto();
	ontology.setPath(derivedConceptDto.getPath());
	ontology.setCode(derivedConceptDto.getCode());
	Optional<DerivedConceptType> derivedConceptType = Optional.ofNullable(derivedConceptDto.getType());
	ontology.setcColumnDatatype(derivedConceptType.isPresent() ? derivedConceptType.get().getType() : null);
	ontology.setMetadata(derivedConceptDto.getMetadata());
	ontology.setUnit(derivedConceptDto.getUnit());
	ontology.setDescription(derivedConceptDto.getDescription());
	ontologyConceptService.addOntology(ontology);
    }

    private Set<String> getConceptPathHierarchy(String concept) {
	List<String> conceptTerms = new ArrayList<>();
	int endIndex = 0;
	StringBuilder conceptTerm = new StringBuilder("");
	do {
	    int lastEndIndex = endIndex;
	    endIndex = concept.indexOf('\\', lastEndIndex) + 1;
	    conceptTerm.append(concept, lastEndIndex, endIndex);
	    conceptTerms.add(conceptTerm.toString());
	} while (endIndex < concept.length());

	return conceptTerms.stream().filter(term -> !"\\".equals(term))
		.map(term -> term.endsWith("\\") ? term : term + "\\").collect(Collectors.toSet());
    }

    private void addDerivedConceptDependencies(List<DerivedConceptDependencyDto> derivedConceptDependencies) {
	if (derivedConceptDependencies != null && !derivedConceptDependencies.isEmpty()) {
	    derivedConceptDependencyDao.addDerivedConceptDependency(derivedConceptDependencies);
	}
    }

    private void validateDerivedConceptDependencies(DerivedConceptDto derivedConceptDto,
	    List<DerivedConceptDependencyDto> dependencies) {
	Set<DerivedConceptDependencyDto> dependencyHierarchy = getDerivedConceptDependencyHierarchy(dependencies);
	List<DerivedConceptDependencyDto> derivedConceptDependencies = new ArrayList<>(dependencyHierarchy);
	detectDerivedConceptCyclicDependency(derivedConceptDto, derivedConceptDependencies);
    }

    private List<DerivedConceptDependencyDto> prepareDerivedConceptDependencies(DerivedConceptDto derivedConceptDto) {
	List<DependencyDto> dependencies = derivedConceptDto.getDependencies();
	List<DerivedConceptDependencyDto> derivedConceptDependencies = null;

	if (!dependencies.isEmpty()) {
	    derivedConceptDependencies = dependencies.stream().map(dependency -> {
		DerivedConceptDependencyDto derivedConceptDependencyDto = new DerivedConceptDependencyDto();
		derivedConceptDependencyDto.setDerivedConceptId(derivedConceptDto.getId());
		conceptUtil.validateConceptPath(dependency.getPath());
		derivedConceptDependencyDto.setParentConceptPath(dependency.getPath());
		derivedConceptDependencyDto.setDerivedConceptPath(derivedConceptDto.getPath());
		return derivedConceptDependencyDto;
	    }).collect(Collectors.toList());
	}
	return derivedConceptDependencies;
    }

    @Override
    public List<Set<DerivedConceptDependencyDto>> getAllDerivedConceptDependencyHierarchy() {
	List<DerivedConceptDependencyDto> derivedConceptDependencies = derivedConceptDependencyDao
		.getAllDerivedConceptDependencies();
	Set<Set<DerivedConceptDependencyDto>> allDependencyHierarchy = new HashSet<>();
	derivedConceptDependencies.parallelStream().forEach(dependency -> {
	    List<DerivedConceptDependencyDto> dependencies = new ArrayList<>();
	    dependencies.add(dependency);
	    allDependencyHierarchy.add(getDerivedConceptDependencyHierarchy(dependencies));
	});
	return new ArrayList<>(allDependencyHierarchy);
    }

    private Set<DerivedConceptDependencyDto> getDerivedConceptDependencyHierarchy(
	    List<DerivedConceptDependencyDto> derivedConceptDependencies) {
	Set<String> conceptPath = new HashSet<>();

	derivedConceptDependencies.stream().forEach(dependency -> {
	    conceptPath.add(dependency.getDerivedConceptPath());
	    conceptPath.add(dependency.getParentConceptPath());
	});

	List<DerivedConceptDependencyDto> allDerivedConceptDependencies = new ArrayList<>();
	allDerivedConceptDependencies.addAll(derivedConceptDependencies);

	Set<DerivedConceptDependencyDto> derivedConceptDependencyHierarchy = new HashSet<>();
	DerivedConceptDependencySearchDto derivedConceptDependencySearchDto;
	boolean[] dependencyFound = { true };
	while (dependencyFound[0]) {
	    dependencyFound[0] = false;

	    derivedConceptDependencySearchDto = new DerivedConceptDependencySearchDto();
	    derivedConceptDependencySearchDto.setConceptPaths(conceptPath);
	    List<DerivedConceptDependencyDto> derivedConceptDependenciesByPath = derivedConceptDependencyDao
		    .getDerivedConceptDependency(derivedConceptDependencySearchDto);

	    allDerivedConceptDependencies.addAll(derivedConceptDependenciesByPath);

	    if (!allDerivedConceptDependencies.isEmpty()) {
		allDerivedConceptDependencies.forEach(dependency -> {
		    if (derivedConceptDependencyHierarchy.add(dependency)) {
			dependencyFound[0] = true;
			conceptPath.add(dependency.getParentConceptPath());
			conceptPath.add(dependency.getDerivedConceptPath());
		    }
		});
	    }
	}
	return derivedConceptDependencyHierarchy;
    }

    /**
     * Detect cyclic dependency between derived concept paths
     */
    private void detectDerivedConceptCyclicDependency(DerivedConceptDto derivedConceptDto,
	    final List<DerivedConceptDependencyDto> derivedConceptDependencies) {
	if (!derivedConceptDependencies.isEmpty()) {
	    DerivedConceptTopologicalSortDto derivedConceptTopologicalSortDto = derivedConceptTopologicalSortWrapper
		    .detectDerivedConceptCyclicDependency(derivedConceptDependencies);
	    if (!StringUtils.isEmpty(derivedConceptTopologicalSortDto.getMessage())) {
		WarningDto warning = new WarningDto();
		warning.setDetail(derivedConceptTopologicalSortDto.getMessage());
		warning.setStatus(STATUS_CODE);
		warning.setTitle(CYCLIC_DEPENDENCY_WARNING);
		derivedConceptDto.addWarning(warning);
	    }
	}
    }

    @Override
    @Transactional(readOnly = false)
    public DerivedConceptDto updateDerivedConcept(DerivedConceptDto derivedConceptDto) {
	/**
	 * Updating derived concept
	 */
	if (derivedConceptDto.getId() == null)
	    throw new I2b2Exception("Id is null. Could not update derived fact");

	/**
	 * Check whether concept_code has changed or not?
	 */
	final DerivedConceptDto existingConcept = getDerivedConceptById(derivedConceptDto.getId());
	if (existingConcept == null)
	    throw new I2B2DataNotFoundException("Derived concept not found");

	final String existingConceptPath = existingConcept.getPath();
	if (!derivedConceptDto.getCode().equalsIgnoreCase(existingConcept.getCode()))
	    throw new I2b2Exception("Concept code has changed, could not be updated");

	if (!derivedConceptDto.getPath().equalsIgnoreCase(existingConcept.getPath()))
	    throw new I2b2Exception(CHANGED_CONCEPT_PATH);

	derivedConceptDto.setUpdatedOn(Instant.now());
	int updateCount = derivedConceptDao.updateDerivedConcept(derivedConceptDto);

	if (updateCount == 0)
	    throw new I2b2Exception("Could not be updated derived concept record.");

	logger.info("Derived concept updated successfully with Id: {}", derivedConceptDto.getId());

	/**
	 * Updating Concept
	 */
	ConceptDto conceptDto = new ConceptDto();
	conceptDto.setCode(derivedConceptDto.getCode());
	conceptDto.setConceptPath(derivedConceptDto.getPath());
	conceptService.updateConcept(conceptDto, existingConceptPath);

	/**
	 * Updating Ontology
	 */
	OntologyDto ontology = new OntologyDto();
	ontology.setPath(derivedConceptDto.getPath());
	ontology.setCode(derivedConceptDto.getCode());
	ontology.setcColumnDatatype(derivedConceptDto.getType().getType());
	ontology.setMetadata(derivedConceptDto.getMetadata());
	ontology.setUnit(derivedConceptDto.getUnit());
	ontology.setDescription(derivedConceptDto.getDescription());
	ontologyConceptService.updateOntology(ontology, existingConceptPath);

	updateDerivedConceptDependencies(derivedConceptDto);
	return derivedConceptDto;
    }

    private void updateDerivedConceptDependencies(DerivedConceptDto derivedConceptDto) {
	List<DerivedConceptDependencyDto> derivedConceptDependencies = prepareDerivedConceptDependencies(
		derivedConceptDto);

	DerivedConceptDependencySearchDto derivedConceptDependencySearchDto = new DerivedConceptDependencySearchDto();
	derivedConceptDependencySearchDto.setDerivedConceptId(derivedConceptDto.getId());
	List<DerivedConceptDependencyDto> existingDerivedConceptDependencies = derivedConceptDependencyDao
		.getDerivedConceptDependency(derivedConceptDependencySearchDto);
	List<DerivedConceptDependencyDto> derivedConceptDependenciesToBeDeleted = existingDerivedConceptDependencies;

	if (derivedConceptDependencies != null && !derivedConceptDependencies.isEmpty()) {

	    Predicate<DerivedConceptDependencyDto> deletePredicate = existingDependency -> derivedConceptDependencies
		    .stream().noneMatch(dependency -> dependency.getParentConceptPath()
			    .equals(existingDependency.getParentConceptPath()));

	    derivedConceptDependenciesToBeDeleted = existingDerivedConceptDependencies.stream().filter(deletePredicate)
		    .collect(Collectors.toList());

	    Predicate<DerivedConceptDependencyDto> addPredicate = dependency -> existingDerivedConceptDependencies
		    .stream().noneMatch(existingDependency -> existingDependency.getParentConceptPath()
			    .equals(dependency.getParentConceptPath()));

	    List<DerivedConceptDependencyDto> derivedConceptDependenciesToBeAdded = derivedConceptDependencies.stream()
		    .filter(addPredicate).collect(Collectors.toList());

	    validateDerivedConceptDependencies(derivedConceptDto, derivedConceptDependencies);
	    addDerivedConceptDependencies(derivedConceptDependenciesToBeAdded);
	}

	List<String> dependenciesToBeDeleted = derivedConceptDependenciesToBeDeleted.stream()
		.map(DerivedConceptDependencyDto::getParentConceptPath).collect(Collectors.toList());
	if (!dependenciesToBeDeleted.isEmpty()) {
	    derivedConceptDependencySearchDto.setParentConceptPaths(dependenciesToBeDeleted);
	    derivedConceptDependencyDao.deleteDerivedConceptDependencies(derivedConceptDependencySearchDto);
	}
    }

    @Override
    @Transactional(readOnly = false)
    public DerivedConceptDto deleteDerivedConcept(DerivedConceptDto derivedConceptDto) {
	/**
	 * Delete derived concept
	 */
	if (derivedConceptDto.getId() == null)
	    throw new I2b2Exception("Id is null. Could not delete derived concept");

	/**
	 * Getting existing concept path so it can be used to delete records from
	 * further tables.
	 */
	final DerivedConceptDto existingConcept = derivedConceptDao.findDerivedConceptById(derivedConceptDto.getId());
	if (existingConcept != null) {
	    final String existingConceptPath = existingConcept.getPath();

	    derivedConceptDto.setUpdatedOn(Instant.now());
	    derivedConceptDao.deleteDerivedConcept(derivedConceptDto);

	    logger.info("Derived concept deleted successfully with Id: {}", derivedConceptDto.getId());

	    /**
	     * Delete concept
	     */
	    ConceptDto conceptDto = new ConceptDto();
	    conceptDto.setConceptPath(existingConceptPath);
	    conceptService.deleteConcept(conceptDto);

	    /**
	     * Delete Ontology
	     */
	    OntologyDto ontology = new OntologyDto();
	    ontology.setPath(existingConceptPath);
	    ontologyConceptService.deleteOntology(ontology);
	} else {
	    logger.error("Derived concept not found for the id: " + derivedConceptDto.getId());
	    throw new I2B2DataNotFoundException("Derived concept not found for the id: " + derivedConceptDto.getId());
	}

	return derivedConceptDto;
    }

    @Override
    public int[] calculateDerivedConcept(Integer derivedConceptId) {
	List<DerivedConceptJobDetailsDto> jobDetails = new ArrayList<>();
	/**
	 * if derivedConceptId is null then, It is global derived concept calculation
	 */
	if (derivedConceptId == null) {
	    final List<DerivedConceptDto> derivedConceptDtos = derivedConceptDao.findDerivedConcepts();
	    List<Set<DerivedConceptDependencyDto>> dependencyHierarchies = getAllDerivedConceptDependencyHierarchy();
	    for (Set<DerivedConceptDependencyDto> dependencyHierarchy : dependencyHierarchies) {
		List<DerivedConceptDto> derivedConcepts = getDerivedConceptsFromDependencyHierarchy(
			dependencyHierarchy);
		derivedConceptDtos.removeAll(derivedConcepts);
		// Get sorted list of derived concepts (unsorted) from dependency hierarchy
		List<DerivedConceptDependencyDto> dependencies = dependencyHierarchy.stream()
			.collect(Collectors.toList());
		List<DerivedConceptDto> sortedDerivedConcepts = derivedConceptTopologicalSortWrapper
			.getDerivedConceptTopologicalSequence(derivedConcepts, dependencies);
		jobDetails.addAll(
			createDerivedConceptJobDetails(derivedConceptId, derivedConcepts, sortedDerivedConcepts));
	    }
	    jobDetails.addAll(createDerivedConceptJobDetails(null, derivedConceptDtos, derivedConceptDtos));
	} else {
	    /**
	     * if derivedConceptId is not null then, It is individual derived concept
	     * calculation
	     */
	    DerivedConceptDto derivedConceptDto = getDerivedConceptById(derivedConceptId);
	    if (derivedConceptDto == null) {
		throw new I2b2Exception(CONCEPT_NOT_FOUND_ERROR + derivedConceptId);
	    }
	    // Get dependency hierarchy
	    List<DerivedConceptDependencyDto> dependencyDtos = new ArrayList<>();
	    DerivedConceptDependencyDto dependencyDto = new DerivedConceptDependencyDto();
	    dependencyDto.setDerivedConceptPath(derivedConceptDto.getPath());
	    dependencyDtos.add(dependencyDto);
	    Set<DerivedConceptDependencyDto> dependencyHierarchy = getDerivedConceptDependencyHierarchy(dependencyDtos);
	    if (!dependencyHierarchy.isEmpty()) {
		/**
		 * Get list of derived concepts (unsorted) from dependency hierarchy
		 */
		List<DerivedConceptDto> derivedConcepts = getDerivedConceptsFromDependencyHierarchy(
			dependencyHierarchy);

		// Get sorted list of derived concepts
		List<DerivedConceptDependencyDto> dependencies = dependencyHierarchy.stream()
			.collect(Collectors.toList());
		List<DerivedConceptDto> sortedDerivedConcepts = derivedConceptTopologicalSortWrapper
			.getDerivedConceptTopologicalSequence(derivedConcepts, dependencies);
		// Create derived concept job details.
		jobDetails = createDerivedConceptJobDetails(derivedConceptId, derivedConcepts, sortedDerivedConcepts);
	    } else {
		/**
		 * If dependencyDtoList is empty, then that derived concept is a leaf node in
		 * dependency hierarchy. So create PENDING status for that node only.
		 */
		jobDetails.add(createJobDetailsObject(derivedConceptId, Status.PENDING, ""));
	    }
	}
	/**
	 * Insert into derived_concept_job_details table with 'PENDING' status
	 */
	return derivedConceptJobDetailsDao.createDerivedConceptJobDetails(jobDetails);
    }

    /**
     * Get sorted list of derived concepts from dependency hierarchy.
     * 
     * @param dependencySet - Set of dependency hierarchy.
     * @return
     */
    public List<DerivedConceptDto> getDerivedConceptsFromDependencyHierarchy(
	    Set<DerivedConceptDependencyDto> dependencies) {
	// Get derived concept definitions associated with dependency hierarchy
	List<String> paths = new ArrayList<>();
	dependencies.forEach(dependency -> {
	    paths.add(dependency.getDerivedConceptPath());
	    paths.add(dependency.getParentConceptPath());
	});
	return getDerivedConceptsByPaths(paths);
    }

    /**
     * Get derived concepts filtered by paths.
     * 
     * @param pathList - List of derived concept paths.
     * @return
     */
    private List<DerivedConceptDto> getDerivedConceptsByPaths(List<String> paths) {
	return derivedConceptDao.findDerivedConceptsByPaths(paths);
    }

    /**
     * Create derived concept job details.
     * 
     * @param derivedConceptId       - Unique identifier of derived concept that
     *                               require a calculation.
     * @param sortedDerivedConcepts  - Sorted list of derived concepts.
     * @param sortedDerivedConcepts2
     * @return
     */
    private List<DerivedConceptJobDetailsDto> createDerivedConceptJobDetails(Integer derivedConceptId,
	    List<DerivedConceptDto> derivedConcepts, List<DerivedConceptDto> sortedDerivedConcepts) {

	/**
	 * If derived concept id is null create job details for all derived concepts. If
	 * not null then create job details for lower order only.
	 */
	List<Integer> derivedConceptIds = new ArrayList<>();
	if (derivedConceptId != null) {
	    // Get latest job details for lower order derived concepts.
	    for (DerivedConceptDto derivedConceptDto : sortedDerivedConcepts) {
		if (derivedConceptDto.getId().equals(derivedConceptId)) {
		    derivedConceptIds.add(derivedConceptDto.getId());
		    break;
		}
		derivedConceptIds.add(derivedConceptDto.getId());
	    }
	} else {
	    sortedDerivedConcepts.forEach(derivedConceptDto -> derivedConceptIds.add(derivedConceptDto.getId()));
	}

	DerivedConceptJobDetailsSearchDto jobDetailsSearchDto = new DerivedConceptJobDetailsSearchDto();
	jobDetailsSearchDto.setDerivedConceptJobDetailsFetchType(DerivedConceptJobDetailsFetchType.LATEST);
	jobDetailsSearchDto.setDerivedConceptIds(derivedConceptIds);
	List<DerivedConceptJobDetailsDto> latestJobDetails = getDerivedConceptJobDetails(jobDetailsSearchDto);

	// Create a map for latest job details
	Map<Integer, Instant> derivedConceptIdDateMap = new HashMap<>();
	latestJobDetails.forEach(
		jobDetail -> derivedConceptIdDateMap.put(jobDetail.getDerivedConceptId(), jobDetail.getCompletedOn()));

	// Create ids map of sorted derived concepts.
	Map<Integer, Boolean> sortedDerivedConceptIdMap = new HashMap<>();
	sortedDerivedConcepts.forEach(derivedConcept -> sortedDerivedConceptIdMap.put(derivedConcept.getId(), true));
	/**
	 * Create PENDING job details by comparing last update timestamp of derived
	 * concept and the last executed Job start timestamp.
	 */
	List<DerivedConceptJobDetailsDto> jobDetails = new ArrayList<>();
	for (DerivedConceptDto derivedConceptDto : derivedConcepts) {
	    if (sortedDerivedConceptIdMap.containsKey(derivedConceptDto.getId())) {
		Instant timestamp = derivedConceptIdDateMap.get(derivedConceptDto.getId());
		if (timestamp != null && (timestamp.compareTo(derivedConceptDto.getUpdatedOn()) > 0)) {
		    continue;
		}
		DerivedConceptJobDetailsDto jobDetail = createJobDetailsObject(derivedConceptDto.getId(),
			Status.PENDING, "");
		jobDetails.add(jobDetail);
	    } else {
		DerivedConceptJobDetailsDto jobDetail = createJobDetailsObject(derivedConceptDto.getId(), Status.ERROR,
			CYCLIC_DEPENDENCY_ERROR);
		jobDetails.add(jobDetail);
	    }
	}
	return jobDetails;
    }

    /**
     * Prepare derived concept job details object.
     * 
     * @param derivedConceptId - Unique identifier of the derived concept.
     * @param status           - Status for job details.
     * @param errorStack       - Error message if any.
     * @return - object of DerivedConceptJobDetailsDto class.
     */
    private DerivedConceptJobDetailsDto createJobDetailsObject(Integer derivedConceptId, Status status,
	    String errorStack) {
	DerivedConceptJobDetailsDto jobDetail = new DerivedConceptJobDetailsDto();
	jobDetail.setDerivedConceptId(derivedConceptId);
	jobDetail.setStatus(status);
	jobDetail.setErrorStack(errorStack);
	return jobDetail;
    }

    @Override
    public List<DerivedConceptJobDetailsDto> getDerivedConceptJobDetails(
	    DerivedConceptJobDetailsSearchDto derivedConceptJobDetailsSearchDto) {
	return derivedConceptJobDetailsDao.findDerivedConceptJobDetails(derivedConceptJobDetailsSearchDto);
    }

    @Override
    public List<DerivedConceptQueryMasterDto> getQueryMasterRecords(Integer fetchSize) {
	int rowSize = (fetchSize != null && fetchSize >= 0) ? fetchSize : DEFAULT_NUMBER_OF_RECORDS;
	List<DerivedConceptQueryMasterDto> masterQueries = derivedConceptQueryMasterDao.getQueryMaster(rowSize);
	return masterQueries.stream().map(queryMaster -> {
	    String generatedQuery = queryMaster.getGeneratedSql();
	    if (!StringUtils.isEmpty(generatedQuery))
		generatedQuery = queryWrapper(generatedQuery);

	    queryMaster.setGeneratedSql(generatedQuery);
	    return queryMaster;
	}).collect(Collectors.toList());
    }

    private String addConceptCodeToQuery(String conceptCode, String generatedQuery) {
	if (conceptCode != null && !StringUtils.isEmpty(generatedQuery)
		&& queryMasterConfig.getConceptCodeTemplate() != null) {
	    return generatedQuery.replaceFirst(queryMasterConfig.getConceptCodeTemplate(), conceptCode);
	}
	return generatedQuery;
    }

    private String checkConceptCode(String conceptCode) {
	return (conceptCode != null && conceptCode.length() > 50) ? conceptCode.substring(0, 50) : conceptCode;
    }

    private String queryHouseKeeping(String query) {
	if (StringUtils.isEmpty(query))
	    return EMPTY_STRING;
	return query.replaceAll(QUERY_SEPARATOR_1, SEMICOLON).replaceAll(QUERY_SEPARATOR_2, HASH)
		.replaceAll(LINE_SEPARATOR, SINGLE_WHITE_SPACE).replaceAll("\\s+", SINGLE_WHITE_SPACE).trim();
    }

    private String queryWrapper(String query) {
	final StringBuilder wrappedQuery = new StringBuilder(queryHouseKeeping(queryMasterConfig.getPrefix()));
	wrappedQuery.append(SINGLE_WHITE_SPACE);
	wrappedQuery.append(queryHouseKeeping(query));
	wrappedQuery.append(SINGLE_WHITE_SPACE);
	wrappedQuery.append(queryHouseKeeping(queryMasterConfig.getPostfix()));
	return wrappedQuery.toString();
    }
}
