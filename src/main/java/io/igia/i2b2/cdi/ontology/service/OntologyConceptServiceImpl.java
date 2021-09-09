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


package io.igia.i2b2.cdi.ontology.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;
import io.igia.i2b2.cdi.derivedconcept.dto.MetadataProperties;
import io.igia.i2b2.cdi.derivedconcept.util.ConceptUtil;
import io.igia.i2b2.cdi.ontology.dao.OntologyConceptDao;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyDto;

@Service
@Transactional(readOnly = true, transactionManager = "i2b2OntologyTransactionManager")
public class OntologyConceptServiceImpl implements OntologyConceptService {

    private static final Logger logger = LoggerFactory.getLogger(OntologyConceptServiceImpl.class);
    private static final String NULL_ONTOLOGY_MESSAGE = "OntologyDto is null.";

    private final OntologyConceptDao ontologyConceptDao;
    private final DocumentBuilderFactory factory;
    private final ConceptUtil conceptUtil;

    public OntologyConceptServiceImpl(OntologyConceptDao ontologyConceptDao, ConceptUtil conceptUtil)
	    throws ParserConfigurationException {
	this.ontologyConceptDao = ontologyConceptDao;
	factory = DocumentBuilderFactory.newInstance();
	factory.setExpandEntityReferences(false);
	factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE);
	factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
	this.conceptUtil = conceptUtil;
    }

    @Override
    public List<OntologyConceptDto> getOntologyConcepts(OntologyConceptSearchDto inOntologyConceptSearchDto) {

	OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto(inOntologyConceptSearchDto);

	if (ontologyConceptSearchDto.isModifierConcept() && !ontologyConceptSearchDto.getConceptPaths().isEmpty()) {
	    ontologyConceptSearchDto.setModifierAppliedPaths(
		    getModifierAppliedPathsHierarchy(ontologyConceptSearchDto.getConceptPaths()));
	}
	return this.ontologyConceptDao.findOntologyConcepts(ontologyConceptSearchDto);
    }

    @Override
    public List<OntologyConceptDto> findOntologyConcepts(OntologyConceptSearchDto ontologyConceptSearchDto) {
	return this.ontologyConceptDao.findOntologyConcepts(ontologyConceptSearchDto);
    }

    @Override
    public List<OntologyConceptDto> getOntologyConceptsWithDataType(
	    OntologyConceptSearchDto inOntologyConceptSearchDto) {
	OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto(inOntologyConceptSearchDto);
	List<OntologyConceptDto> ontologyConcepts = getOntologyConcepts(ontologyConceptSearchDto);
	ontologyConcepts.stream().forEach(concept -> {
	    if (StringUtils.isEmpty(concept.getMetadataXml())) {
		concept.setDataType(ConceptDataType.STRING);
	    } else {
		concept.setDataType(extractDataTypeFromMetadata(concept.getMetadataXml()));
	    }
	});

	return ontologyConcepts;
    }

    private ConceptDataType extractDataTypeFromMetadata(String metadata) {
	try {
	    final DocumentBuilder builder = factory.newDocumentBuilder();
	    Document dDoc = builder.parse(new InputSource(new StringReader(metadata)));

	    XPath xPath = XPathFactory.newInstance().newXPath();
	    Node node = (Node) xPath.evaluate("/ValueMetadata/DataType", dDoc, XPathConstants.NODE);
	    if (!StringUtils.isEmpty(node.getTextContent().trim())) {
		return ConceptDataType.fromCode(node.getTextContent());
	    }
	} catch (Exception e) {
	    logger.warn("Concept metadata xml parsing failed.", e);
	}
	return ConceptDataType.STRING;
    }

    private Set<String> getModifierAppliedPathsHierarchy(List<String> conceptPaths) {
	return conceptPaths.stream().flatMap(concept -> getModifierAppliedPathsHierarchy(concept).stream())
		.collect(Collectors.toSet());
    }

    private Set<String> getModifierAppliedPathsHierarchy(String concept) {
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
		.map(term -> term.endsWith("\\") ? term + "%" : term + "\\%").collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = false)
    public OntologyDto addOntology(OntologyDto ontologyDto) {
	int i2b2Count = 0;
	int tableAccessCount = 0;
	OntologyDto ontologyRecord = prepareOntology(ontologyDto);

	i2b2Count = ontologyConceptDao.addOntologyToI2b2(ontologyRecord);

	if (i2b2Count <= 0)
	    throw new I2b2Exception("Ontology could not added in i2b2.");

	logger.info("Ontology added in i2b2");

	if (ontologyRecord.getChLevel() == 0) {
	    tableAccessCount = ontologyConceptDao.addOntologyToTableAccess(ontologyRecord);
	    if (tableAccessCount <= 0)
		throw new I2b2Exception("Ontology could not added in table_access.");

	    logger.info("Ontology added in table_access");
	}

	return ontologyRecord;
    }

    private OntologyDto prepareOntology(OntologyDto ontologyDto) {
	if (ontologyDto == null)
	    throw new I2b2Exception(NULL_ONTOLOGY_MESSAGE);

	OntologyDto ontologyRecord = new OntologyDto();

	String path = ontologyDto.getPath();
	String conceptCode = ontologyDto.getCode();
	conceptUtil.validateConceptPath(path);
	path = conceptUtil.removeSeparatorAtFirstAndLast(path);
	String metaDataXml = ontologyDto.getMetadata();
	String columnDataType = ontologyDto.getcColumnDatatype();
	String tableName = conceptUtil.getTableName(ontologyDto.getcTableName());

	ontologyRecord.setChLevel(conceptUtil.getLevel(path));
	ontologyRecord.setPath(conceptUtil.getFullPath(path));
	ontologyRecord.setcName(conceptUtil.getConceptName(path));
	ontologyRecord.setCode(conceptCode);
	ontologyRecord.setcSynonymCd(MetadataProperties.SYNONYM_CD);
	ontologyRecord.setcVisualAttributes(conceptUtil.getVisualAttributes(path));
	ontologyRecord.setMetadata(
		!StringUtils.isEmpty(metaDataXml) ? metaDataXml : conceptUtil.getMetadataXml(columnDataType));
	ontologyRecord.setcFactTableColumn(conceptUtil.getFactTableColumnName(ontologyDto.getcFactTableColumn()));
	ontologyRecord.setcTableName(tableName);
	ontologyRecord.setcColumnName(conceptUtil.getColumnName(ontologyDto.getcColumnName()));
	ontologyRecord.setcColumnDatatype(conceptUtil.getColumnDataType(columnDataType, tableName));
	ontologyRecord.setcOperator(conceptUtil.getOperator(ontologyDto.getcOperator()));
	ontologyRecord.setcDimcode(conceptUtil.getDimCode(ontologyDto.getcDimcode(), path));
	ontologyRecord.setcTooltip(conceptUtil.getToolTip(path));
	ontologyRecord.setmAppliedPath(MetadataProperties.APPLIED_PATH);
	ontologyRecord.setcTableCd(conceptUtil.getConceptName(path));
	ontologyRecord.setAccessTableName(MetadataProperties.TABLE_ACCESS_NAME);
	ontologyRecord.setcProtectedAccess(MetadataProperties.PROTECTED_ACCESS);
	return ontologyRecord;
    }

    @Override
    @Transactional(readOnly = false)
    public OntologyDto updateOntology(OntologyDto ontologyDto, String existingConceptFullName) {
	int i2b2Count = 0;
	int tableAccessCount = 0;
	OntologyDto ontologyRecord = prepareOntology(ontologyDto);

	OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();
	ontologyConceptSearchDto.setOntologyDto(ontologyRecord);
	ontologyConceptSearchDto.setExistingConceptFullName(existingConceptFullName);

	i2b2Count = ontologyConceptDao.updateOntologyToI2b2(ontologyConceptSearchDto);

	if (i2b2Count <= 0)
	    throw new I2b2Exception("Ontology could not updated in i2b2.");

	logger.info("Ontology updated in i2b2");

	if (ontologyRecord.getChLevel() == 0) {
	    tableAccessCount = ontologyConceptDao.updateOntologyToTableAccess(ontologyConceptSearchDto);
	    if (tableAccessCount <= 0)
		throw new I2b2Exception("Ontology could not updated in table_access.");

	    logger.info("Ontology updated in table_access");
	}

	return ontologyRecord;
    }

    @Override
    @Transactional(readOnly = false)
    public OntologyDto deleteOntology(OntologyDto ontologyDto) {
	if (ontologyDto == null)
	    throw new I2b2Exception(NULL_ONTOLOGY_MESSAGE);

	final String existingPath = ontologyDto.getPath();

	if (StringUtils.isEmpty(existingPath))
	    throw new I2b2Exception("ConceptFullName is null or empty.");

	OntologyDto ontologyRecord = new OntologyDto();
	ontologyRecord.setPath(existingPath);
	ontologyRecord.setChLevel(conceptUtil.getLevel(existingPath));

	ontologyConceptDao.deleteOntologyFromI2b2(ontologyRecord);
	ontologyConceptDao.deleteOntologyFromTableAccess(ontologyRecord);

	return ontologyRecord;
    }
    
    @Override
    public List<OntologyConceptDto> getOntologyConceptsByLevel(OntologyConceptSearchDto inOntologyConceptSearchDto) {
        return this.ontologyConceptDao.findOntologyConceptsByLevel(inOntologyConceptSearchDto);
    }
}
