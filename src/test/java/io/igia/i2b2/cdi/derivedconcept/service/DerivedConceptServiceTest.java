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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.igia.i2b2.cdi.common.dto.WarningDto;
import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.common.integration.AppIntegrationProperties;
import io.igia.i2b2.cdi.common.integration.SftpOutboundMessageHandler;
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

@RunWith(MockitoJUnitRunner.class)
public class DerivedConceptServiceTest {

    private static final String CYCLIC_DEPENDENCY_ERROR = "There exists a cycle in the graph !!";

    @Mock
    private DerivedConceptDao derivedConceptDao;

    @Mock
    private OntologyConceptService ontologyConceptService;

    @Mock
    private ConceptService conceptService;

    private DerivedConceptService derivedConceptService;

    @Mock
    private SftpOutboundMessageHandler sftpOutboundMessageHandler;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private DerivedConceptJobDetailsDao derivedConceptJobDetailsDao;

    @Mock
    private DerivedConceptDependencyDao derivedConceptDependencyDao;

    @Mock
    private DerivedConceptTopologicalSortWrapper derivedConceptTopologicalSortWrapper;

    @Mock
    private AppIntegrationProperties appIntegrationProperties;

    @Mock
    private DerivedConceptQueryMasterDao derivedConceptQueryMasterDao;

    @Mock
    private QueryMasterConfig queryMasterConfig;

    private ConceptUtil conceptUtil;

    @Before
    public void setUp() {
	conceptUtil = new ConceptUtil();
	derivedConceptService = new DerivedConceptServiceImpl(derivedConceptDao, ontologyConceptService, conceptService,
		derivedConceptJobDetailsDao, derivedConceptDependencyDao, derivedConceptTopologicalSortWrapper,
		conceptUtil, derivedConceptQueryMasterDao, queryMasterConfig);
    }

    @Test
    public void testGetDerivedConceptById() {
	DerivedConceptDto expectedDerivedConceptDto = createDerivedConcept(1, "\\Derived\\Test\\", null, null);
	given(derivedConceptDao.findDerivedConceptById(any())).willReturn(expectedDerivedConceptDto);

	List<OntologyConceptDto> ontologyDto = java.util.Arrays.asList(createOntologyConcept("\\Derived\\Concept-1\\"),
		createOntologyConcept("\\Derived\\Concept-2\\"));

	given(ontologyConceptService.getOntologyConceptsWithDataType(any(OntologyConceptSearchDto.class)))
		.willReturn(ontologyDto);

	List<DerivedConceptDependencyDto> derivedConceptDependencies = new ArrayList<>(
		Arrays.asList(createDerivedConceptDependency(1, 2, "\\labtest\\2\\", "\\Derived\\test6\\"),
			createDerivedConceptDependency(2, 2, "\\labtest\\3\\", "\\Derived\\test6\\")));
	when(derivedConceptDependencyDao
		.getDerivedConceptDependency(ArgumentMatchers.any(DerivedConceptDependencySearchDto.class)))
			.thenReturn(derivedConceptDependencies);

	DerivedConceptDto actualDerivedConceptDto = derivedConceptService.getDerivedConceptById(1);

	expectedDerivedConceptDto.setDependencies(createDerivedConceptDependencies(3, "\\Derived\\Test"));

	assertThat(actualDerivedConceptDto).isNotNull();
	assertThat(actualDerivedConceptDto).isEqualToComparingFieldByFieldRecursively(expectedDerivedConceptDto);
    }

    @Test
    public void testGetDerivedConceptById_nullObject() {
	given(derivedConceptDao.findDerivedConceptById(any())).willReturn(null);
	DerivedConceptDto actualDerivedConceptDto = derivedConceptService.getDerivedConceptById(1);
	assertThat(actualDerivedConceptDto).isNull();
    }

    @Test
    public void testGetDerivedConceptById_noConceptMatch() {
	DerivedConceptDto derivedConceptDto = createDerivedConcept(1, "\\Derived\\Test\\", null, null);
	given(derivedConceptDao.findDerivedConceptById(any())).willReturn(derivedConceptDto);

	List<OntologyConceptDto> ontologyDto = java.util.Arrays.asList(createOntologyConcept("/no-match-concept/"),
		createOntologyConcept("/no-match-concept-2/"));

	when(ontologyConceptService.getOntologyConceptsWithDataType(any(OntologyConceptSearchDto.class)))
		.thenReturn(ontologyDto);

	DerivedConceptDto actualDerivedConceptDto = derivedConceptService.getDerivedConceptById(1);

	assertThat(actualDerivedConceptDto).isNotNull();
	assertThat(actualDerivedConceptDto).isEqualToComparingFieldByField(derivedConceptDto);
    }

    @Test
    public void testGetDerivedConceptById_conceptTypeNumeric() {
	DerivedConceptDto derivedConceptDto = createDerivedConcept(1, "\\Derived\\Test\\", null, null);
	given(derivedConceptDao.findDerivedConceptById(any())).willReturn(derivedConceptDto);

	List<OntologyConceptDto> ontologyDto = java.util.Arrays.asList(
		createOntologyConcept("/Derived/Concept-1/", "metadata-xml-1"),
		createOntologyConcept("/Derived/Concept-2/", "metadata-xml-2"));

	when(ontologyConceptService.getOntologyConceptsWithDataType(any(OntologyConceptSearchDto.class)))
		.thenReturn(ontologyDto);

	DerivedConceptDto actualDerivedConceptDto = derivedConceptService.getDerivedConceptById(1);

	assertThat(actualDerivedConceptDto).isNotNull();
	assertThat(actualDerivedConceptDto).isEqualToComparingFieldByField(derivedConceptDto);
    }

    @Test
    public void testGetDerivedConcepts() {
	List<DerivedConceptDto> concepts = Arrays.asList(
		createDerivedConcept(1, "\\Derived\\Test\\",
			createDerivedConceptDependencies(2, "\\Derived\\dependenttest"), null),
		createDerivedConcept(2, "\\Derived\\Test\\",
			createDerivedConceptDependencies(2, "\\Derived\\dependenttest"), null));
	given(derivedConceptDao.findDerivedConcepts()).willReturn(concepts);
	assertThat(concepts).isNotNull().isNotEmpty();

	List<OntologyConceptDto> ontologyConceptsMap = Arrays.asList(getOntologyList(1), getOntologyList(2));
	given(ontologyConceptService.getOntologyConceptsWithDataType(any())).willReturn(ontologyConceptsMap);
	List<DerivedConceptDependencyDto> derivedConceptDependencies = new ArrayList<>(Arrays.asList(
		createDerivedConceptDependency(1, 1, "\\Derived\\dependenttest1\\", "\\Derived\\Test\\1\\"),
		createDerivedConceptDependency(2, 1, "\\Derived\\dependenttest2\\", "\\Derived\\Test\\1\\")));
	when(derivedConceptDependencyDao
		.getDerivedConceptDependency(ArgumentMatchers.any(DerivedConceptDependencySearchDto.class)))
			.thenReturn(derivedConceptDependencies);

	List<DerivedConceptDto> actualConcepts = derivedConceptService.getDerivedConcepts();

	assertThat(actualConcepts).isNotNull().isNotEmpty().size().isEqualTo(2);
	assertThat(actualConcepts.get(0)).isEqualToComparingFieldByFieldRecursively(concepts.get(0));
	assertThat(actualConcepts.get(1)).isEqualToComparingFieldByFieldRecursively(concepts.get(1));
    }

    private OntologyConceptDto createOntologyConcept(String fullName) {
	return createOntologyConcept(fullName, null);
    }

    private OntologyConceptDto createOntologyConcept(String fullName, String metadataXml) {
	OntologyConceptDto ontologyConceptDto = new OntologyConceptDto();
	ontologyConceptDto.setFullName(fullName);
	ontologyConceptDto.setMetadataXml(metadataXml);
	return ontologyConceptDto;
    }

    private OntologyConceptDto getOntologyList(int i) {
	OntologyConceptDto ontology = new OntologyConceptDto();
	ontology.setFullName("\\Derived\\Test" + i + "\\");
	ontology.setMetadataXml("MetaDataXml" + i);
	ontology.setDataType(ConceptDataType.INTEGER);
	return ontology;
    }

    private DerivedConceptDto createDerivedConcept(Integer sequence, String path, List<DependencyDto> dependencies,
	    List<WarningDto> warnings) {
	DerivedConceptDto derivedConceptDto = new DerivedConceptDto();
	derivedConceptDto.setId(sequence);
	derivedConceptDto.setPath(path + sequence + "\\");
	derivedConceptDto.setCode("derived:test" + sequence);
	derivedConceptDto.setDescription("Test description " + sequence);
	derivedConceptDto.setFactQuery("Select * from table" + sequence);
	derivedConceptDto.setUnit("test-unit");
	derivedConceptDto.setUpdatedOn(Instant.now());
	derivedConceptDto.setType(DerivedConceptType.NUMERIC);
	derivedConceptDto.setDependencies(dependencies);
	derivedConceptDto.setWarnings(warnings);
	return derivedConceptDto;
    }

    private List<DependencyDto> createDerivedConceptDependencies(int size, String path) {
	List<DependencyDto> dependencies = new ArrayList<>();
	DependencyDto derivedConceptDependency = null;
	for (int sequence = 1; sequence <= size; sequence++) {
	    derivedConceptDependency = new DependencyDto();
	    derivedConceptDependency.setPath(path + sequence + "\\");
	    dependencies.add(derivedConceptDependency);
	}
	return dependencies;
    }

    private List<WarningDto> createWarnings() {
	WarningDto warning = new WarningDto();
	warning.setTitle(DerivedConceptServiceImpl.CYCLIC_DEPENDENCY_WARNING);
	warning.setDetail(CYCLIC_DEPENDENCY_ERROR);
	warning.setStatus(DerivedConceptServiceImpl.STATUS_CODE);

	List<WarningDto> warnings = new ArrayList<>();
	warnings.add(warning);
	return warnings;
    }

    @Test
    public void testAddDerivedConcept() {
	final int SEQUENCE = 6;
	DerivedConceptDto derivedConceptDto = createDerivedConcept(SEQUENCE, "\\Derived\\test",
		createDerivedConceptDependencies(2, "\\Derived\\dependenttest"), null);

	when(derivedConceptDao.addDerivedConcept(ArgumentMatchers.any())).thenReturn(1);
	ConceptDto conceptDto = new ConceptDto();
	when(conceptService.addConcept(ArgumentMatchers.any())).thenReturn(conceptDto);
	OntologyDto ontologyDto = new OntologyDto();
	when(ontologyConceptService.addOntology(ArgumentMatchers.any())).thenReturn(ontologyDto);
	when(derivedConceptTopologicalSortWrapper.detectDerivedConceptCyclicDependency(ArgumentMatchers.any()))
		.thenReturn(new DerivedConceptTopologicalSortDto());
	when(derivedConceptDependencyDao.addDerivedConceptDependency(ArgumentMatchers.any()))
		.thenReturn(new int[] { 1, 2 });
	DerivedConceptDto actualDerivedConcept = derivedConceptService.addDerivedConcept(derivedConceptDto);
	assertThat(actualDerivedConcept).isEqualToComparingFieldByField(derivedConceptDto);
    }

    private DerivedConceptDependencyDto createDerivedConceptDependency(Integer id, Integer derivedConceptId,
	    String parentConceptPath, String derivedConceptPath) {
	DerivedConceptDependencyDto derivedConceptDependencyDto = new DerivedConceptDependencyDto();
	derivedConceptDependencyDto.setId(id);
	derivedConceptDependencyDto.setDerivedConceptId(derivedConceptId);
	derivedConceptDependencyDto.setParentConceptPath(parentConceptPath);
	derivedConceptDependencyDto.setDerivedConceptPath(derivedConceptPath);
	return derivedConceptDependencyDto;
    }

    @Test
    public void testAddDerivedConcept_setWarnings() {
	final int SEQUENCE = 6;
	DerivedConceptDto derivedConceptDto = createDerivedConcept(SEQUENCE, "\\Derived\\test",
		createDerivedConceptDependencies(2, "\\Derived\\dependenttest"), null);

	when(derivedConceptDao.addDerivedConcept(ArgumentMatchers.any())).thenReturn(1);
	ConceptDto conceptDto = new ConceptDto();
	when(conceptService.addConcept(ArgumentMatchers.any())).thenReturn(conceptDto);
	OntologyDto ontologyDto = new OntologyDto();
	when(ontologyConceptService.addOntology(ArgumentMatchers.any())).thenReturn(ontologyDto);
	DerivedConceptTopologicalSortDto derivedConceptTopologicalSortDto = new DerivedConceptTopologicalSortDto();
	derivedConceptTopologicalSortDto.setMessage(CYCLIC_DEPENDENCY_ERROR);
	when(derivedConceptTopologicalSortWrapper.detectDerivedConceptCyclicDependency(ArgumentMatchers.any()))
		.thenReturn(derivedConceptTopologicalSortDto);
	when(derivedConceptDependencyDao.addDerivedConceptDependency(ArgumentMatchers.any()))
		.thenReturn(new int[] { 1, 2 });
	DerivedConceptDto actualDerivedConcept = derivedConceptService.addDerivedConcept(derivedConceptDto);
	derivedConceptDto.setWarnings(createWarnings());
	assertThat(actualDerivedConcept).isEqualToComparingFieldByField(derivedConceptDto);
    }

    @Test
    public void testAddDerivedConcept_failedAddDerivedConcept() {
	final int SEQUENCE = 6;
	DerivedConceptDto derivedConceptDto = createDerivedConcept(SEQUENCE, "\\Derived\\Test\\", null, null);
	when(derivedConceptDao.addDerivedConcept(ArgumentMatchers.any())).thenReturn(0);
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage("Could not add derived concept record.");
	derivedConceptService.addDerivedConcept(derivedConceptDto);
    }

    @Test
    public void testUpdateDerivedConcept() {
	final int SEQUENCE = 6;
	DerivedConceptDto derivedConceptDto = createDerivedConcept(SEQUENCE, "\\Derived\\test\\",
		createDerivedConceptDependencies(4, "\\labtest"), null);
	when(derivedConceptDao.findDerivedConceptById(ArgumentMatchers.any())).thenReturn(derivedConceptDto);
	when(derivedConceptDao.updateDerivedConcept(ArgumentMatchers.any())).thenReturn(1);
	when(conceptService.updateConcept(ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new ConceptDto());
	when(ontologyConceptService.updateOntology(ArgumentMatchers.any(), ArgumentMatchers.any()))
		.thenReturn(new OntologyDto());
	DerivedConceptTopologicalSortDto derivedConceptTopologicalSortDto = new DerivedConceptTopologicalSortDto();
	derivedConceptTopologicalSortDto.setMessage(null);
	when(derivedConceptTopologicalSortWrapper.detectDerivedConceptCyclicDependency(ArgumentMatchers.any()))
		.thenReturn(derivedConceptTopologicalSortDto);

	List<DerivedConceptDependencyDto> derivedConceptDependencies = new ArrayList<>(
		Arrays.asList(createDerivedConceptDependency(1, 2, "\\labtest\\2\\", "\\Derived\\test6\\"),
			createDerivedConceptDependency(2, 2, "\\labtest\\3\\", "\\Derived\\test6\\")));
	when(derivedConceptDependencyDao
		.getDerivedConceptDependency(ArgumentMatchers.any(DerivedConceptDependencySearchDto.class)))
			.thenReturn(derivedConceptDependencies);

	DerivedConceptDto actualDerivedConcept = derivedConceptService.updateDerivedConcept(derivedConceptDto);

	assertThat(actualDerivedConcept.getId()).isNotNull().isEqualTo(derivedConceptDto.getId());
	assertThat(actualDerivedConcept).isNotNull().isEqualToComparingFieldByField(derivedConceptDto);
    }

    @Test
    public void testUpdateDerivedConcept_changedConceptCode() {
	DerivedConceptDto derivedConceptDto = createDerivedConcept(6, "\\Derived\\test\\", null, null);
	DerivedConceptDto existingDerivedConcept = createDerivedConcept(7, "\\Derived\\test\\", null, null);
	when(derivedConceptDao.findDerivedConceptById(ArgumentMatchers.any())).thenReturn(existingDerivedConcept);
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage("Concept code has changed, could not be updated");
	derivedConceptService.updateDerivedConcept(derivedConceptDto);
    }

    @Test
    public void testUpdateDerivedConcept_changedConceptPath() {
	DerivedConceptDto derivedConceptDto = createDerivedConcept(6, "\\Derived\\tester\\", null, null);
	DerivedConceptDto existingDerivedConcept = createDerivedConcept(6, "\\Derived\\test\\", null, null);
	when(derivedConceptDao.findDerivedConceptById(ArgumentMatchers.any())).thenReturn(existingDerivedConcept);
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage(DerivedConceptServiceImpl.CHANGED_CONCEPT_PATH);
	derivedConceptService.updateDerivedConcept(derivedConceptDto);
    }

    @Test
    public void testDeleteDerivedConcept() {
	DerivedConceptDto derivedConceptDto = createDerivedConcept(6, "\\Derived\\test\\", null, null);
	when(derivedConceptDao.findDerivedConceptById(ArgumentMatchers.any())).thenReturn(derivedConceptDto);
	when(derivedConceptDao.deleteDerivedConcept(ArgumentMatchers.any())).thenReturn(1);
	when(conceptService.deleteConcept(ArgumentMatchers.any())).thenReturn(new ConceptDto());
	when(ontologyConceptService.deleteOntology(ArgumentMatchers.any())).thenReturn(new OntologyDto());

	DerivedConceptDto actualConcept = derivedConceptService.deleteDerivedConcept(derivedConceptDto);

	assertThat(actualConcept.getId()).isNotNull().isEqualTo(derivedConceptDto.getId());
    }

    @Test
    public void testDeleteDerivedConcept_nullId() {
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage("Id is null. Could not delete derived concept");
	DerivedConceptDto derivedConceptDto = createDerivedConcept(null, "\\Derived\\test\\", null, null);
	derivedConceptService.deleteDerivedConcept(derivedConceptDto);
    }

    private DerivedConceptJobDetailsDto createDerivedConceptJobDetails(Integer id, String errorStack,
	    String derivedConceptSql, Status status) {
	DerivedConceptJobDetailsDto jobDetails = new DerivedConceptJobDetailsDto();
	jobDetails.setDerivedConceptId(id);
	jobDetails.setId(id);
	jobDetails.setErrorStack(errorStack);
	jobDetails.setDerivedConceptSql(derivedConceptSql);
	jobDetails.setStatus(status);
	return jobDetails;
    }

    @Test
    public void testgetDerivedConceptJobDetails() {
	final List<Integer> derivedConceptIds = Arrays.asList(1, 2, 3);
	DerivedConceptJobDetailsSearchDto derivedConceptJobDetailsSearchDto = new DerivedConceptJobDetailsSearchDto();
	derivedConceptJobDetailsSearchDto.setDerivedConceptIds(derivedConceptIds);
	derivedConceptJobDetailsSearchDto
		.setDerivedConceptJobDetailsFetchType(DerivedConceptJobDetailsFetchType.LATEST);
	List<DerivedConceptJobDetailsDto> jobDetails = new ArrayList<>();
	jobDetails.add(createDerivedConceptJobDetails(1, "derived fact error", "select * from testdb", Status.PENDING));
	jobDetails
		.add(createDerivedConceptJobDetails(2, "derived fact error", "select * from testdb", Status.COMPLETED));

	when(derivedConceptJobDetailsDao
		.findDerivedConceptJobDetails(ArgumentMatchers.any(DerivedConceptJobDetailsSearchDto.class)))
			.thenReturn(jobDetails);

	List<DerivedConceptJobDetailsDto> actualJobDetails = derivedConceptService
		.getDerivedConceptJobDetails(derivedConceptJobDetailsSearchDto);

	assertThat(actualJobDetails.get(0)).isNotNull().isEqualToComparingFieldByField(jobDetails.get(0));
    }

    /**
     * Below test creates list of PENDING status records for global calculation.
     */
    @Test
    public void testCalculateAllDerivedConcept() {

	List<DerivedConceptDependencyDto> allDerivedConceptDependencies = new ArrayList<>(Arrays.asList(
		createDerivedConceptDependencyDto(1, 1, "\\Derived\\LDL Change\\", "\\Derived\\Last LDL\\"),
		createDerivedConceptDependencyDto(2, 1, "\\Derived\\LDL Change\\", "\\Derived\\Start LDL\\")));

	when(derivedConceptDependencyDao.getAllDerivedConceptDependencies()).thenReturn(allDerivedConceptDependencies);

	when(derivedConceptDependencyDao
		.getDerivedConceptDependency(ArgumentMatchers.any(DerivedConceptDependencySearchDto.class)))
			.thenReturn(allDerivedConceptDependencies);

	List<DependencyDto> dependencies = new ArrayList<>(Arrays.asList(createDependencyDto("\\Derived\\Last LDL\\"),
		createDependencyDto("\\Derived\\Last LDL\\")));

	List<DerivedConceptDto> derivedConceptDependencies = new ArrayList<>(
		Arrays.asList(createDerivedConcept(1, "\\Derived\\LDL Change\\", dependencies, new ArrayList<>()),
			createDerivedConcept(2, "\\Derived\\Last LDL\\", new ArrayList<>(), new ArrayList<>()),
			createDerivedConcept(3, "\\Derived\\Start LDL\\", new ArrayList<>(), new ArrayList<>())));

	when(derivedConceptDao.findDerivedConceptsByPaths(any())).thenReturn(derivedConceptDependencies);
	when(derivedConceptJobDetailsDao.findDerivedConceptJobDetails(any())).thenReturn(new ArrayList<>());
	when(derivedConceptJobDetailsDao.createDerivedConceptJobDetails(any())).thenReturn(new int[3]);

	int[] rowsAffected = derivedConceptService.calculateDerivedConcept(null);
	assertThat(rowsAffected).isNotNull().isEqualTo(new int[3]);
    }

    private DependencyDto createDependencyDto(String path) {
	DependencyDto dependencyDto = new DependencyDto();
	dependencyDto.setPath(path);
	return dependencyDto;
    }

    private DerivedConceptDependencyDto createDerivedConceptDependencyDto(int id, int derivedConceptId,
	    String derivedConceptPath, String parentConceptPath) {
	DerivedConceptDependencyDto dependencyDto = new DerivedConceptDependencyDto();
	dependencyDto.setId(id);
	dependencyDto.setDerivedConceptId(derivedConceptId);
	dependencyDto.setDerivedConceptPath(derivedConceptPath);
	dependencyDto.setParentConceptPath(parentConceptPath);
	return dependencyDto;
    }

    /**
     * Below test creates list of PENDING status records for derived concept and
     * dependent on that derived concept.
     */
    @Test
    public void testCalculateDerivedConceptIdFound() {

	DerivedConceptDto derivedConceptDto = createDerivedConcept(2, "\\Derived\\Last LDL\\", new ArrayList<>(),
		new ArrayList<>());
	when(derivedConceptDao.findDerivedConceptById(any())).thenReturn(derivedConceptDto);

	List<DependencyDto> dependencies = new ArrayList<>(Arrays.asList(createDependencyDto("\\Derived\\Last LDL\\"),
		createDependencyDto("\\Derived\\Last LDL\\")));
	List<DerivedConceptDto> derivedConceptDependencies = new ArrayList<>(
		Arrays.asList(createDerivedConcept(1, "\\Derived\\LDL Change\\", dependencies, new ArrayList<>()),
			createDerivedConcept(2, "\\Derived\\Last LDL\\", new ArrayList<>(), new ArrayList<>()),
			createDerivedConcept(3, "\\Derived\\Start LDL\\", new ArrayList<>(), new ArrayList<>())));

	when(derivedConceptDao.findDerivedConceptsByPaths(any())).thenReturn(derivedConceptDependencies);
	when(derivedConceptJobDetailsDao.createDerivedConceptJobDetails(any())).thenReturn(new int[3]);
	int[] rowsAffected = derivedConceptService.calculateDerivedConcept(2);
	assertThat(rowsAffected).isNotNull().isEqualTo(new int[3]);
    }

    /**
     * Below test tries to create list of PENDING status records for derived concept
     * and dependent on that derived concept, but throws id not matched exception.
     */
    @Test
    public void testCalculateDerivedConceptIdNotFound() {
	when(derivedConceptDao.findDerivedConceptById(any())).thenReturn(null);
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage(DerivedConceptServiceImpl.CONCEPT_NOT_FOUND_ERROR + "2");
	derivedConceptService.calculateDerivedConcept(2);
    }

    @Test
    public void testGetAllDerivedConceptDependencyHierarchy() {
	List<DerivedConceptDependencyDto> derivedConceptDependencies = new ArrayList<>(
		Arrays.asList(createDerivedConceptDependency(1, 1, "\\labtest\\2\\", "\\Derived\\test1\\"),
			createDerivedConceptDependency(2, 1, "\\labtest\\3\\", "\\Derived\\test1\\"),
			createDerivedConceptDependency(3, 2, "\\labtest\\3\\", "\\Derived\\test2\\"),
			createDerivedConceptDependency(4, 3, "\\labtest\\4\\", "\\Derived\\test3\\")));

	DerivedConceptDependencyDto expectedDerivedConceptDependency = createDerivedConceptDependency(3, 2,
		"\\labtest\\3\\", "\\Derived\\test2\\");

	when(derivedConceptDependencyDao.getAllDerivedConceptDependencies()).thenReturn(derivedConceptDependencies);
	when(derivedConceptDependencyDao
		.getDerivedConceptDependency(ArgumentMatchers.any(DerivedConceptDependencySearchDto.class)))
			.thenReturn(derivedConceptDependencies);

	List<Set<DerivedConceptDependencyDto>> actualDerivedConceptDependencyHierarchies = derivedConceptService
		.getAllDerivedConceptDependencyHierarchy();
	assertThat(actualDerivedConceptDependencyHierarchies.get(0)).isNotNull();
	DerivedConceptDependencyDto actualDerivedConceptDependency = actualDerivedConceptDependencyHierarchies.get(0)
		.stream().findFirst().get();
	assertThat(actualDerivedConceptDependency).isNotNull()
		.isEqualToComparingFieldByField(expectedDerivedConceptDependency);
    }

    private DerivedConceptQueryMasterDto createDerivedConceptQueryMasterDto(int id, String query) {
	DerivedConceptQueryMasterDto derivedConceptQueryMasterDto = new DerivedConceptQueryMasterDto();
	derivedConceptQueryMasterDto.setId(id);
	derivedConceptQueryMasterDto.setName("derive:test-" + id);
	derivedConceptQueryMasterDto.setCreatedDate(Timestamp.valueOf("2019-10-10 00:00:00").toInstant());
	derivedConceptQueryMasterDto.setGeneratedSql(query);
	return derivedConceptQueryMasterDto;
    }

    @Test
    public void testGetQueryMasterRecords() {
	List<DerivedConceptQueryMasterDto> masterQueries = Arrays.asList(createDerivedConceptQueryMasterDto(1,
		"with t as (   select  p.patient_num   from i2b2demodata.dbo.patient_dimension p  where   p.patient_num IN (select patient_num from  i2b2demodata.dbo.patient_dimension   where income_cd = 'High')    group by  p.patient_num   )  insert into #global_temp_table (patient_num, panel_count) select  t.patient_num, 0 as panel_count  from t  <*> update #global_temp_table set panel_count =1 where #global_temp_table.panel_count =  0 and exists ( select 1 from ( select  f.patient_num   from i2b2demodata.dbo.observation_fact f  where   f.concept_cd IN (select concept_cd from  i2b2demodata.dbo.concept_dimension   where concept_path LIKE '\\i2b2\\Demographics\\Marital Status\\Single\\%')    group by  f.patient_num ) t where #global_temp_table.patient_num = t.patient_num    )  <*>  insert into #dx (  patient_num   ) select * from ( select distinct  patient_num  from #global_temp_table where panel_count = 1 ) q"),
		createDerivedConceptQueryMasterDto(2,
			"with t as ( select  p.patient_num   from i2b2demodata.dbo.patient_dimension p  where   p.patient_num IN (select patient_num from  i2b2demodata.dbo.patient_dimension   where birth_date > getdate() - (365.25*10) +1)    group by  p.patient_num   )  insert into #global_temp_table (patient_num, panel_count) select  t.patient_num, 0 as panel_count  from t  <*> with t as (   select  p.patient_num   from i2b2demodata.dbo.patient_dimension p  where   p.patient_num IN (select patient_num from  i2b2demodata.dbo.patient_dimension   where birth_date BETWEEN getdate() - (365.25*45) +1  AND getdate() - (365.25*35) + 1)    group by  p.patient_num   )  insert into #global_temp_table (patient_num, panel_count) select  t.patient_num, 0 as panel_count  from t  <*>  insert into #dx (  patient_num   ) select * from ( select distinct  patient_num  from #global_temp_table where panel_count = 0 ) q"));

	when(derivedConceptQueryMasterDao.getQueryMaster(ArgumentMatchers.anyInt())).thenReturn(masterQueries);
	List<DerivedConceptQueryMasterDto> actualMasterQueries = derivedConceptService.getQueryMasterRecords(50);

	DerivedConceptQueryMasterDto expectedMasterQuery = createDerivedConceptQueryMasterDto(1,
		" with t as ( select p.patient_num from i2b2demodata.dbo.patient_dimension p where p.patient_num IN (select patient_num from i2b2demodata.dbo.patient_dimension where income_cd = 'High') group by p.patient_num ) insert into #global_temp_table (patient_num, panel_count) select t.patient_num, 0 as panel_count from t ; update #global_temp_table set panel_count =1 where #global_temp_table.panel_count = 0 and exists ( select 1 from ( select f.patient_num from i2b2demodata.dbo.observation_fact f where f.concept_cd IN (select concept_cd from i2b2demodata.dbo.concept_dimension where concept_path LIKE '\\i2b2\\Demographics\\Marital Status\\Single\\%') group by f.patient_num ) t where #global_temp_table.patient_num = t.patient_num ) ; insert into #dx ( patient_num ) select * from ( select distinct patient_num from #global_temp_table where panel_count = 1 ) q ");

	assertThat(actualMasterQueries.size()).isNotNull().isLessThanOrEqualTo(50);
	assertThat(actualMasterQueries.size()).isNotNull().isEqualTo(2);
	assertThat(actualMasterQueries.get(0)).isNotNull().isEqualToComparingFieldByField(expectedMasterQuery);
    }
}