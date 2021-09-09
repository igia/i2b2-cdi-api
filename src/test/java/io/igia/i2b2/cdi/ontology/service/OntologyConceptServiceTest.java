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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptType;
import io.igia.i2b2.cdi.derivedconcept.util.ConceptUtil;
import io.igia.i2b2.cdi.ontology.dao.OntologyConceptDao;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyDto;

@RunWith(MockitoJUnitRunner.class)
public class OntologyConceptServiceTest {
    private static final String METADATA_XML = "<?xml version=\"1.0\"?><ValueMetadata><Version>3.02</Version><CreationDateTime>08/14/2008 01:22:59</CreationDateTime><TestID></TestID><TestName></TestName><DataType>PosFloat</DataType><CodeType></CodeType><Loinc></Loinc><Flagstouse></Flagstouse><Oktousevalues>Y</Oktousevalues><MaxStringLength></MaxStringLength><LowofLowValue>0</LowofLowValue><HighofLowValue>0</HighofLowValue><LowofHighValue>100</LowofHighValue>100<HighofHighValue>100</HighofHighValue><LowofToxicValue></LowofToxicValue><HighofToxicValue></HighofToxicValue><EnumValues></EnumValues><CommentsDeterminingExclusion><Com></Com></CommentsDeterminingExclusion><UnitValues><NormalUnits>ratio</NormalUnits><EqualUnits></EqualUnits><ExcludingUnits></ExcludingUnits><ConvertingUnits><Units></Units><MultiplyingFactor></MultiplyingFactor></ConvertingUnits></UnitValues><Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>";

    @Mock
    private OntologyConceptDao ontologyConceptDao;

    @Mock
    private ConceptUtil conceptUtil;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private OntologyConceptService ontologyConceptService;

    @Before
    public void setUp() throws ParserConfigurationException {
	ontologyConceptService = new OntologyConceptServiceImpl(ontologyConceptDao, conceptUtil);
    }

    @Test
    public void testGetOntologyConcepts() {
	OntologyConceptSearchDto updatedSearchDto = new OntologyConceptSearchDto();
	updatedSearchDto.setConceptPaths(Arrays.asList(
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\",
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\Respiratory and intrathorasic organs (160-165)\\"));
	updatedSearchDto.setModifierAppliedPaths(Arrays.asList("\\i2b2\\%", "\\i2b2\\Diagnoses\\%",
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\%",
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\%",
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\Respiratory and intrathorasic organs (160-165)\\%"));
	List<OntologyConceptDto> ontologyConcepts = Arrays.asList(createOntologyConcept("\\TNM\\Nodes\\"),
		createOntologyConcept("\\TNM\\Metastisis\\MX\\"));
	given(ontologyConceptDao.findOntologyConcepts(argThat(searchDto -> searchDto.getModifierAppliedPaths()
		.containsAll(updatedSearchDto.getModifierAppliedPaths())))).willReturn(ontologyConcepts);

	OntologyConceptSearchDto searchDto = new OntologyConceptSearchDto();
	searchDto.setModifierConcept(true);
	searchDto.setConceptPaths(Arrays.asList(
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\",
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\Respiratory and intrathorasic organs (160-165)\\"));
	List<OntologyConceptDto> actualOntologyConcepts = ontologyConceptService.getOntologyConcepts(searchDto);

	assertThat(actualOntologyConcepts).isNotNull().isNotEmpty().size().isEqualTo(2);
	assertThat(actualOntologyConcepts.get(0)).isEqualToComparingFieldByField(ontologyConcepts.get(0));
	assertThat(actualOntologyConcepts.get(1)).isEqualToComparingFieldByField(ontologyConcepts.get(1));
	verify(ontologyConceptDao, times(1)).findOntologyConcepts(argThat(actualSearchDto -> actualSearchDto
		.getModifierAppliedPaths().containsAll(updatedSearchDto.getModifierAppliedPaths())));
    }

    @Test
    public void testFindOntologyConcepts() {
	List<OntologyConceptDto> ontologyConcepts = Arrays.asList(createOntologyConcept("\\TNM\\Nodes\\"));
	given(ontologyConceptDao.findOntologyConcepts(any(OntologyConceptSearchDto.class)))
		.willReturn(ontologyConcepts);

	OntologyConceptSearchDto searchDto = new OntologyConceptSearchDto();
	searchDto.setConceptPath("\\TNM\\Nodes\\");
	List<OntologyConceptDto> actualOntologyConcepts = ontologyConceptService.findOntologyConcepts(searchDto);

	assertThat(actualOntologyConcepts).isNotNull().isNotEmpty().size().isEqualTo(1);
	assertThat(actualOntologyConcepts.get(0)).isEqualToComparingFieldByField(ontologyConcepts.get(0));
    }
    
    @Test
    public void testGetOntologyConceptsByLevel() {
        List<OntologyConceptDto> ontologyConcepts = Arrays.asList(createOntologyConcept("\\TNM\\Nodes\\"));
        given(ontologyConceptDao.findOntologyConceptsByLevel(any(OntologyConceptSearchDto.class)))
                .willReturn(ontologyConcepts);

        OntologyConceptSearchDto searchDto = new OntologyConceptSearchDto();
        searchDto.setConceptPath("\\TNM\\");
        searchDto.setConceptLevels(Arrays.asList(2));
        List<OntologyConceptDto> actualOntologyConcepts = ontologyConceptService.getOntologyConceptsByLevel(searchDto);

        assertThat(actualOntologyConcepts).isNotNull().isNotEmpty().size().isEqualTo(1);
        assertThat(actualOntologyConcepts.get(0)).isEqualToComparingFieldByField(ontologyConcepts.get(0));
    }

    @Test
    public void testGetOntologyConceptsWithDataType() {
	List<OntologyConceptDto> ontologyConcepts = Arrays.asList(createOntologyConcept("\\TNM\\Nodes\\",
		"<?xml version=\"1.0\"?><ValueMetadata><Version>3.02</Version><CreationDateTime>08/14/2008 01:22:59</CreationDateTime><TestID></TestID><TestName></TestName><DataType>PosFloat</DataType><CodeType></CodeType><Loinc></Loinc><Flagstouse></Flagstouse><Oktousevalues>Y</Oktousevalues><MaxStringLength></MaxStringLength><LowofLowValue>0</LowofLowValue><HighofLowValue>0</HighofLowValue><LowofHighValue>100</LowofHighValue>100<HighofHighValue>100</HighofHighValue><LowofToxicValue></LowofToxicValue><HighofToxicValue></HighofToxicValue><EnumValues></EnumValues><CommentsDeterminingExclusion><Com></Com></CommentsDeterminingExclusion><UnitValues><NormalUnits>ratio</NormalUnits><EqualUnits></EqualUnits><ExcludingUnits></ExcludingUnits><ConvertingUnits><Units></Units><MultiplyingFactor></MultiplyingFactor></ConvertingUnits></UnitValues><Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>"),
		createOntologyConcept("\\TNM\\Metastisis\\MX\\",
			"<?xml version=\"1.0\"?><ValueMetadata><Version>3.02</Version><CreationDateTime>09/13/02 08:45:42</CreationDateTime><TestID>APO-E</TestID><TestName>Apolipoprot E Iso</TestName><DataType>Enum</DataType><CodeType>GRP</CodeType><Loinc>1886-1</Loinc><Flagstouse /><Oktousevalues>Y</Oktousevalues><MaxStringLength>0</MaxStringLength><LowofLowValue></LowofLowValue><HighofLowValue></HighofLowValue><LowofHighValue></LowofHighValue><HighofHighValue></HighofHighValue><LowofToxicValue></LowofToxicValue><HighofToxicValue></HighofToxicValue><EnumValues><Val></Val><ExcludingVal></ExcludingVal><ExcludingVal>PEND</ExcludingVal><ExcludingVal>PENDING</ExcludingVal><ExcludingVal>NOT DONE</ExcludingVal><ExcludingVal>QNS</ExcludingVal><ExcludingVal>NSQ</ExcludingVal><ExcludingVal>UNSATISFACTORY</ExcludingVal><ExcludingVal>CANCELLED</ExcludingVal></EnumValues><CommentsDeterminingExclusion><Com></Com></CommentsDeterminingExclusion><UnitValues><NormalUnits></NormalUnits><EqualUnits></EqualUnits><ExcludingUnits></ExcludingUnits><ConvertingUnits><Units></Units><MultiplyingFactor></MultiplyingFactor></ConvertingUnits></UnitValues><Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>"),
		createOntologyConcept("\\TNM\\Metastisis\\MX\\"));

	given(ontologyConceptDao.findOntologyConcepts(any())).willReturn(ontologyConcepts);

	List<OntologyConceptDto> actualOntologyConcepts = ontologyConceptService
		.getOntologyConceptsWithDataType(new OntologyConceptSearchDto());

	List<OntologyConceptDto> expectedConcepts = new ArrayList<>(ontologyConcepts);
	expectedConcepts.get(0).setDataType(ConceptDataType.POS_FLOAT);
	expectedConcepts.get(1).setDataType(ConceptDataType.STRING);
	expectedConcepts.get(2).setDataType(ConceptDataType.STRING);

	assertThat(actualOntologyConcepts).isNotNull().isNotEmpty().size().isEqualTo(3);
	assertThat(actualOntologyConcepts.get(0)).isEqualToComparingFieldByField(expectedConcepts.get(0));
	assertThat(actualOntologyConcepts.get(1)).isEqualToComparingFieldByField(expectedConcepts.get(1));
	assertThat(actualOntologyConcepts.get(2)).isEqualToComparingFieldByField(expectedConcepts.get(2));

	verify(ontologyConceptDao, times(1)).findOntologyConcepts(any());
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

    private OntologyDto createOntologyDto(String path) {
	OntologyDto ontology = new OntologyDto();
	ontology.setPath(path);
	ontology.setCode("derived:test");
	ontology.setcColumnDatatype(DerivedConceptType.NUMERIC.getType());
	ontology.setMetadata(METADATA_XML);
	ontology.setUnit("mg/dL");
	ontology.setDescription("test derived concept");
	return ontology;
    }

    @Test
    public void testAddOntology() {
	OntologyDto expectedOntology = createOntologyDto("\\derived\\test55\\");

	given(conceptUtil.removeSeparatorAtFirstAndLast(ArgumentMatchers.anyString())).willReturn("derived\\test55");
	given(conceptUtil.getLevel(ArgumentMatchers.anyString())).willReturn(1);
	given(conceptUtil.getFullPath(ArgumentMatchers.anyString())).willReturn("\\derived\\test55\\");
	given(conceptUtil.getConceptName(ArgumentMatchers.anyString())).willReturn("derived");
	given(conceptUtil.getVisualAttributes(ArgumentMatchers.anyString())).willReturn("FA");
	given(conceptUtil.getToolTip(ArgumentMatchers.anyString())).willReturn("derived");

	given(ontologyConceptDao.addOntologyToI2b2(ArgumentMatchers.any())).willReturn(1);
	OntologyDto actualOntology = ontologyConceptService.addOntology(expectedOntology);

	assertThat(actualOntology).isNotNull().isEqualToComparingOnlyGivenFields(expectedOntology, "path", "code");
    }

    @Test
    public void testAddOntology_conceptLevel0() {
	OntologyDto expectedOntology = createOntologyDto("\\derived\\");
	given(conceptUtil.removeSeparatorAtFirstAndLast(ArgumentMatchers.anyString())).willReturn("derived");
	given(conceptUtil.getLevel(ArgumentMatchers.anyString())).willReturn(0);
	given(conceptUtil.getFullPath(ArgumentMatchers.anyString())).willReturn("\\derived\\");
	given(conceptUtil.getConceptName(ArgumentMatchers.anyString())).willReturn("derived");
	given(conceptUtil.getVisualAttributes(ArgumentMatchers.anyString())).willReturn("CA");
	given(conceptUtil.getToolTip(ArgumentMatchers.anyString())).willReturn("derived");

	given(ontologyConceptDao.addOntologyToI2b2(ArgumentMatchers.any())).willReturn(1);
	given(ontologyConceptDao.addOntologyToTableAccess(ArgumentMatchers.any())).willReturn(1);
	OntologyDto actualOntology = ontologyConceptService.addOntology(expectedOntology);

	assertThat(actualOntology).isNotNull().isEqualToComparingOnlyGivenFields(expectedOntology, "path", "code");
    }

    @Test
    public void testUpdateOntology() {
	OntologyDto expectedOntology = createOntologyDto("\\derived\\test55\\");
	given(conceptUtil.removeSeparatorAtFirstAndLast(ArgumentMatchers.anyString())).willReturn("derived\\test55");
	given(conceptUtil.getLevel(ArgumentMatchers.anyString())).willReturn(1);
	given(conceptUtil.getFullPath(ArgumentMatchers.anyString())).willReturn("\\derived\\test55\\");
	given(conceptUtil.getConceptName(ArgumentMatchers.anyString())).willReturn("derived");
	given(conceptUtil.getVisualAttributes(ArgumentMatchers.anyString())).willReturn("CA");

	given(conceptUtil.getToolTip(ArgumentMatchers.anyString())).willReturn("derived");
	given(ontologyConceptDao.updateOntologyToI2b2(ArgumentMatchers.any(OntologyConceptSearchDto.class)))
		.willReturn(1);
	OntologyDto actualOntology = ontologyConceptService.updateOntology(expectedOntology, "\\derived\\test55\\");

	assertThat(actualOntology).isNotNull().isEqualToComparingOnlyGivenFields(expectedOntology, "path", "code");
    }

    @Test
    public void testUpdateOntology_conceptLevel0() {
	OntologyDto expectedOntology = createOntologyDto("\\derived\\");

	given(conceptUtil.removeSeparatorAtFirstAndLast(ArgumentMatchers.anyString())).willReturn("derived");
	given(conceptUtil.getLevel(ArgumentMatchers.anyString())).willReturn(0);
	given(conceptUtil.getFullPath(ArgumentMatchers.anyString())).willReturn("\\derived\\");
	given(conceptUtil.getConceptName(ArgumentMatchers.anyString())).willReturn("derived");
	given(conceptUtil.getVisualAttributes(ArgumentMatchers.anyString())).willReturn("CA");
	given(conceptUtil.getToolTip(ArgumentMatchers.anyString())).willReturn("derived");

	given(ontologyConceptDao.updateOntologyToI2b2(ArgumentMatchers.any(OntologyConceptSearchDto.class)))
		.willReturn(1);
	given(ontologyConceptDao.updateOntologyToTableAccess(ArgumentMatchers.any(OntologyConceptSearchDto.class)))
		.willReturn(1);
	OntologyDto actualOntology = ontologyConceptService.updateOntology(expectedOntology, "\\derived\\test55\\");

	assertThat(actualOntology).isNotNull().isEqualToComparingOnlyGivenFields(expectedOntology, "path", "code");
    }

    @Test
    public void testDeleteOntology() {
	OntologyDto expectedOntology = createOntologyDto("\\derived\\test55\\");
	given(ontologyConceptDao.deleteOntologyFromI2b2(ArgumentMatchers.any())).willReturn(1);
	given(ontologyConceptDao.deleteOntologyFromTableAccess(ArgumentMatchers.any())).willReturn(1);
	OntologyDto actualOntology = ontologyConceptService.deleteOntology(expectedOntology);

	assertThat(actualOntology.getPath()).isNotNull().isEqualTo(expectedOntology.getPath());
    }

    @Test
    public void testDeleteOntology_nullConceptPath() {
	OntologyDto expectedOntology = createOntologyDto("\\derived\\test55\\");
	expectedOntology.setPath(null);
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage("ConceptFullName is null or empty.");
	ontologyConceptService.deleteOntology(expectedOntology);
    }

    @Test
    public void testDeleteOntology_nullOntology() {
	thrown.expect(I2b2Exception.class);
	thrown.expectMessage("OntologyDto is null.");
	ontologyConceptService.deleteOntology(null);
    }
}
