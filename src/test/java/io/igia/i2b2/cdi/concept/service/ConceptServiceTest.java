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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyDto;
import io.igia.i2b2.cdi.ontology.service.OntologyConceptService;

@RunWith(MockitoJUnitRunner.class)
public class ConceptServiceTest {

    @Mock
    private ConceptDao conceptDao;
    @Mock
    private OntologyConceptService ontologyConceptService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Captor
    private ArgumentCaptor<OntologyDto> ontologyArgumentCaptor;

    private ConceptService conceptService;

    private ConceptUtil conceptUtil;

    @Before
    public void setUp() {
	conceptUtil = new ConceptUtil();
	conceptService = new ConceptServiceImpl(conceptDao, ontologyConceptService, conceptUtil);
    }

    @Test
    public void testGetConcepts() {
	List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"), createConcept(2, "test"));
	PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);
	given(conceptDao.findConcepts(any())).willReturn(result);

	PaginationResult<ConceptDto> actualConcepts = conceptService.getConcepts(createConceptSearchDto("", "", ""));

	assertThat(actualConcepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(2);
	assertThat(actualConcepts.getRecords().get(0)).isEqualToComparingFieldByField(concepts.get(0));
	assertThat(actualConcepts.getRecords().get(1)).isEqualToComparingFieldByField(concepts.get(1));
    }

    @Test
    public void testGetConceptsFilterByConceptPathContains() {
	List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"), createConcept(1, "test"));
	PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);

	given(conceptDao.findConcepts(any())).willReturn(result);
	PaginationResult<ConceptDto> actualConcepts = conceptService
		.getConcepts(createConceptSearchDto("", "conc", ""));
	assertThat(actualConcepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(2);
	assertThat(actualConcepts.getRecords().get(0)).isEqualToComparingFieldByField(concepts.get(0));
    }
    
    /**
     * Test get concepts, filter by startsWith slash i.e. level 0 and 1
     */
    @Test
    public void testGetConceptsFilterByConceptPathStartsWithOnlySlash() {
        List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"), createConcept(1, "test"));
        PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 2);

        ConceptSearchDto conceptSearchDto = createConceptSearchDto("", "\\", "");
        conceptSearchDto.getPathFilterDto().setOpertaor(Operator.STARTSWITH);
        
        List<OntologyConceptDto> ontologyConceptDtos = new ArrayList<>();
        OntologyConceptDto ontologyConceptDto1 = new OntologyConceptDto();
        ontologyConceptDto1.setFullName("\\conc\\conc-name\\");
        ontologyConceptDtos.add(ontologyConceptDto1);
        OntologyConceptDto ontologyConceptDto2 = new OntologyConceptDto();
        ontologyConceptDto2.setFullName("\\conc\\conc-name\\");
        ontologyConceptDtos.add(ontologyConceptDto2);
        
        given(ontologyConceptService.getOntologyConceptsByLevel(any())).willReturn(ontologyConceptDtos);
        given(conceptDao.findConcepts(any())).willReturn(result);
        PaginationResult<ConceptDto> actualConcepts = conceptService
                .getConcepts(conceptSearchDto);
        assertThat(actualConcepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(2);
        assertThat(actualConcepts.getRecords().get(0)).isEqualToComparingFieldByField(concepts.get(0));
    }
    
    /**
     * Test get concepts, filter by startsWith concept path \conc\ i.e. level(3) = slash count + 1
     */
    @Test
    public void testGetConceptsFilterByConceptPathStartsWith() {
        List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"), createConcept(1, "test"));
        PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 2);

        ConceptSearchDto conceptSearchDto = createConceptSearchDto("", "\\conc\\", "");
        conceptSearchDto.getPathFilterDto().setOpertaor(Operator.STARTSWITH);
        
        List<OntologyConceptDto> ontologyConceptDtos = new ArrayList<>();
        OntologyConceptDto ontologyConceptDto1 = new OntologyConceptDto();
        ontologyConceptDto1.setFullName("\\conc\\conc-name\\1\\");
        ontologyConceptDtos.add(ontologyConceptDto1);
        OntologyConceptDto ontologyConceptDto2 = new OntologyConceptDto();
        ontologyConceptDto2.setFullName("\\conc\\conc-name\\2\\");
        ontologyConceptDtos.add(ontologyConceptDto2);
        
        given(ontologyConceptService.getOntologyConceptsByLevel(any())).willReturn(ontologyConceptDtos);
        given(conceptDao.findConcepts(any())).willReturn(result);
        PaginationResult<ConceptDto> actualConcepts = conceptService
                .getConcepts(conceptSearchDto);
        assertThat(actualConcepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(2);
        assertThat(actualConcepts.getRecords().get(0)).isEqualToComparingFieldByField(concepts.get(0));
    }

    /**
     * This method test the get Concepts service for the for the filter source, path
     * and pagination.
     */
    @Test
    public void testGetConceptsFilterBySourcePathAndPagination() {
	List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"));
	PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);
	given(conceptDao.findConcepts(any())).willReturn(result);

	PaginationResult<ConceptDto> actualConcepts = conceptService
		.getConcepts(createConceptSearchDto("demo", "conc", ""));
	assertThat(actualConcepts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(1);
	assertThat(actualConcepts.getRecords().get(0)).isEqualToComparingFieldByField(concepts.get(0));
    }

    @Test
    public void testValidateConcept() {
	List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"), createConcept(2, "test"));
	PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);
	given(conceptDao.findConcepts(any())).willReturn(result);

	conceptService.validate(createConceptSearchDto("", "", ""));
	verify(conceptDao, times(1)).findConcepts(any());
    }

    @Test
    public void testValidateConcept_invalidConceptCode() {
	PaginationResult<ConceptDto> concepts = new PaginationResult<>();
	concepts.setRecords(new ArrayList<ConceptDto>());
	given(conceptDao.findConcepts(any())).willReturn(concepts);

	thrown.expect(I2b2DataValidationException.class);
	thrown.expectMessage("Invalid concept code.");
	conceptService.validate(new ConceptSearchDto().setCode("ABC"));
	verify(conceptDao, times(1)).findConcepts(any());
    }

    @Test
    public void testGetConceptsWithDataType() {
	List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo", ConceptDataType.STRING),
		createConcept(2, "test", ConceptDataType.POS_FLOAT));
	PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);
	given(conceptDao.findConcepts(any())).willReturn(result);

	OntologyConceptDto ontologyConceptDto = new OntologyConceptDto();
	ontologyConceptDto.setFullName("/conc/conc-name/1");
	ontologyConceptDto.setDataType(ConceptDataType.STRING);

	OntologyConceptDto ontologyConceptDto2 = new OntologyConceptDto();
	ontologyConceptDto2.setFullName("/conc/conc-name/2");
	ontologyConceptDto2.setDataType(ConceptDataType.INTEGER);

	List<String> conceptPaths = concepts.stream().map(ConceptDto::getConceptPath).collect(Collectors.toList());
	given(ontologyConceptService.getOntologyConceptsWithDataType(argThat(
		ontologyConceptSearchDto -> ontologyConceptSearchDto.getConceptPaths().containsAll(conceptPaths))))
			.willReturn(Arrays.asList(ontologyConceptDto, ontologyConceptDto2));

	ConceptSearchDto searchDto = new ConceptSearchDto();
	PageableDto pageableDto = new PageableDto();
	pageableDto.setSortBy("concept_path");
	searchDto.setPageableDto(pageableDto);
	searchDto.setPathFilterDto(new PathFilterDto());
	List<ConceptDto> actualConcepts = conceptService.getConceptsWithDataType(searchDto);

	assertThat(actualConcepts).isNotNull().isNotEmpty().size().isEqualTo(2);
	assertThat(actualConcepts.get(0)).isEqualToComparingFieldByField(concepts.get(0));
	assertThat(actualConcepts.get(1)).isEqualToComparingFieldByField(concepts.get(1));

	verify(ontologyConceptService, times(2)).getOntologyConceptsWithDataType(argThat(
		ontologyConceptSearchDto -> ontologyConceptSearchDto.getConceptPaths().containsAll(conceptPaths)));

	verify(conceptDao, times(1)).findConcepts(any());
    }

    private ConceptDto createConcept(int sequence, String source) {
	return createConcept(sequence, source, ConceptDataType.STRING);
    }

    private ConceptDto createConcept(int sequence, String source, ConceptDataType dataType) {
	ConceptDto conceptDto = new ConceptDto();
	conceptDto.setCode("conc:" + sequence);
	conceptDto.setName("conc-name" + sequence);
	conceptDto.setConceptPath("\\conc\\conc-name\\" + sequence + "\\");
	conceptDto.setSource(source != null ? source : "test");
	conceptDto.setDataType(dataType);
	return conceptDto;
    }

    @Test
    public void testAddConcept() {
	ConceptDto conceptDto = createConcept(7, "test");
	given(conceptDao.addConcept(ArgumentMatchers.any())).willReturn(1);
	ConceptDto actualConcept = conceptService.addConcept(conceptDto);

	assertThat(actualConcept).isNotNull();
	assertThat(actualConcept).isEqualToComparingOnlyGivenFields(conceptDto, "code");
    }

    @Test
    public void testAddConcept_nullConcept() {
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage("Concept is null.");
	conceptService.addConcept(null);
    }

    @Test
    public void testAddConcept_conceptFailedToAdd() {
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage("Could not add concept record.");
	ConceptDto conceptDto = createConcept(7, "test");
	given(conceptDao.addConcept(ArgumentMatchers.any())).willReturn(0);
	conceptService.addConcept(conceptDto);
    }

    @Test
    public void testUpdateConcept() {
	ConceptDto conceptDto = createConcept(1, "test");
	given(conceptDao.updateConcept(ArgumentMatchers.any(), ArgumentMatchers.any())).willReturn(1);
	ConceptDto actualConcept = conceptService.updateConcept(conceptDto, "\\conc\\conc-name\\1\\");
	assertThat(actualConcept).isNotNull().isEqualToComparingOnlyGivenFields(conceptDto, "code");
    }

    @Test
    public void testDeleteConcept() {
	ConceptDto conceptDto = createConcept(1, "test");
	given(conceptDao.deleteConcept(ArgumentMatchers.any())).willReturn(1);
	ConceptDto actualConcept = conceptService.deleteConcept(conceptDto);

	assertThat(actualConcept.getConceptPath()).isNotNull().isEqualTo(conceptDto.getConceptPath());
    }

    @Test
    public void testDeleteConcept_nullConcept() {
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage("Concept is null.");
	conceptService.deleteConcept(null);
    }

    @Test
    public void testDeleteConcept_nullConceptPath() {
	ConceptDto conceptDto = createConcept(1, "test");
	conceptDto.setConceptPath(null);
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage("Concept path is null or empty. Could not delete concept.");
	conceptService.deleteConcept(conceptDto);
    }

    private ConceptSearchDto createConceptSearchDto(String source, String path, String code) {
	ConceptSearchDto conceptSearchDto = new ConceptSearchDto();
	conceptSearchDto.setSource(source);
	conceptSearchDto.setCode(code);

	PathFilterDto pathFilterDto = new PathFilterDto();
	pathFilterDto.setPath(path);
	conceptSearchDto.setPathFilterDto(pathFilterDto);

	PageableDto pageableDto = new PageableDto();
	pageableDto.setSortBy("concept_path");
	conceptSearchDto.setPageableDto(pageableDto);
	return conceptSearchDto;
    }

    @Test
    public void testCreateConcept() {
	ConceptDto conceptDto = createConcept(10, "DEMO");
	given(conceptDao.addConcept(ArgumentMatchers.any())).willReturn(1);
	given(ontologyConceptService.addOntology(ArgumentMatchers.any())).willReturn(new OntologyDto());
	conceptService.createConcept(conceptDto);

	verify(ontologyConceptService).addOntology(ontologyArgumentCaptor.capture());
	OntologyDto ontology = createOntologyDto("\\conc\\conc-name\\10\\", "conc:10");
	assertThat(ontologyArgumentCaptor.getValue()).isNotNull().isEqualToComparingFieldByField(ontology);
    }

    private OntologyDto createOntologyDto(String path, String code) {
	OntologyDto ontology = new OntologyDto();
	ontology.setPath(path);
	ontology.setCode(code);
	ontology.setcColumnDatatype(DerivedConceptType.TEXTUAL.getType());
	return ontology;
    }
}
