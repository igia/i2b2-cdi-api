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


package io.igia.i2b2.cdi.ontology.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;

import io.igia.i2b2.cdi.config.SecurityConfig;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptType;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyDto;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan(basePackages = { "io.igia.i2b2.cdi.ontology.dao",
	"io.igia.i2b2.cdi.config" }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = {
		SecurityConfig.class }))
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
	"spring.datasource.driver=org.h2.Driver", "spring.datasource.username=sa",
	"ontology.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
	"ontology.datasource.driver=org.h2.Driver", "ontology.datasource.username=sa" })
@DirtiesContext
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(value = { "/test-ontology-schema.sql", "/test-ontology-data.sql" })
@SqlConfig(dataSource = "ontology", transactionManager = "i2b2OntologyTransactionManager")
public class OntologyConceptDaoTest {
    private static final String METADATA_XML = "<?xml version=\"1.0\"?><ValueMetadata><Version>3.02</Version><CreationDateTime>08/14/2008 01:22:59</CreationDateTime><TestID></TestID><TestName></TestName><DataType>PosFloat</DataType><CodeType></CodeType><Loinc></Loinc><Flagstouse></Flagstouse><Oktousevalues>Y</Oktousevalues><MaxStringLength></MaxStringLength><LowofLowValue>0</LowofLowValue><HighofLowValue>0</HighofLowValue><LowofHighValue>100</LowofHighValue>100<HighofHighValue>100</HighofHighValue><LowofToxicValue></LowofToxicValue><HighofToxicValue></HighofToxicValue><EnumValues></EnumValues><CommentsDeterminingExclusion><Com></Com></CommentsDeterminingExclusion><UnitValues><NormalUnits>ratio</NormalUnits><EqualUnits></EqualUnits><ExcludingUnits></ExcludingUnits><ConvertingUnits><Units></Units><MultiplyingFactor></MultiplyingFactor></ConvertingUnits></UnitValues><Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>";

    @Autowired
    private OntologyConceptDao ontologyConceptDao;

    @Test
    public void findOntologyConcepts_noRecords() {
	OntologyConceptSearchDto searchDto = new OntologyConceptSearchDto();
	searchDto.setModifierAppliedPaths(Arrays.asList("\\nonexistent\\%"));

	List<OntologyConceptDto> ontologyConcepts = ontologyConceptDao.findOntologyConcepts(searchDto);
	assertThat(ontologyConcepts).isNotNull().isEmpty();
    }

    @Test
    public void findOntologyConcepts_byConceptPath() {
	OntologyConceptSearchDto searchDto = new OntologyConceptSearchDto();
	searchDto.setConceptPath("\\TNM\\Tumor\\");

	List<OntologyConceptDto> ontologyConcepts = ontologyConceptDao.findOntologyConcepts(searchDto);
	assertThat(ontologyConcepts).isNotNull();
	assertThat(ontologyConcepts.size()).isEqualTo(1);
    }
    
    @Test
    public void findOntologyConceptsFilterByLevel() {
        OntologyConceptSearchDto searchDto = new OntologyConceptSearchDto();
        searchDto.setConceptPath("\\\\TNM\\\\");
        searchDto.setConceptLevels(Arrays.asList(2));

        List<OntologyConceptDto> ontologyConcepts = ontologyConceptDao.findOntologyConceptsByLevel(searchDto);
        assertThat(ontologyConcepts).isNotNull();
        assertThat(ontologyConcepts.size()).isEqualTo(1);
    }

    @Test
    public void findOntologyConcepts_filterByModifierAppliedPath() {
	OntologyConceptSearchDto searchDto = new OntologyConceptSearchDto();
	searchDto.setModifierConcept(true);
	searchDto.setModifierAppliedPaths(Arrays.asList("\\i2b2\\%", "\\i2b2\\Diagnoses\\%",
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\%",
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\%",
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\Respiratory and intrathorasic organs (160-165)\\%",
		"\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\Respiratory and intrathorasic organs (160-165)\\(160) Malignant neoplasm of nasal~\\%"));

	List<OntologyConceptDto> ontologyConcepts = ontologyConceptDao.findOntologyConcepts(searchDto);
	assertThat(ontologyConcepts).isNotNull().isNotEmpty().size().isEqualTo(3);

	OntologyConceptDto expectedModifier = createOntologyConcept("\\Lung\\TNM\\Stage\\I\\"); // "\\Secondary
												// Diagnosis\\");
	assertThat(ontologyConcepts.get(0)).isEqualToComparingFieldByField(expectedModifier);

	OntologyConceptDto expectedModifier2 = createOntologyConcept("\\Lung\\TNM\\Stage\\Occult\\");
	assertThat(ontologyConcepts.get(1)).isEqualToComparingFieldByField(expectedModifier2);
    }

    private OntologyConceptDto createOntologyConcept(String fullName) {
	OntologyConceptDto ontologyConceptDto = new OntologyConceptDto();
	ontologyConceptDto.setFullName(fullName);
	return ontologyConceptDto;
    }

    @Test
    public void testAddOntologyToI2b2() {
	OntologyDto ontology = createOntologyDto("\\Derived\\test\\", 1);
	int status = ontologyConceptDao.addOntologyToI2b2(ontology);
	assertThat(status).isNotNull().isEqualTo(1);
    }

    @Test
    public void testAddOntologyToTableAccess() {
	OntologyDto ontology = createOntologyDto("\\Derived\\test\\", 0);
	int status = ontologyConceptDao.addOntologyToTableAccess(ontology);
	assertThat(status).isNotNull().isEqualTo(1);
    }

    private OntologyDto createOntologyDto(String path, int chLevel) {
	OntologyDto ontology = new OntologyDto();
	ontology.setPath(path);
	ontology.setCode("derived:test");
	ontology.setcColumnDatatype(DerivedConceptType.NUMERIC.getType());
	ontology.setMetadata(METADATA_XML);
	ontology.setUnit("mg/dL");
	ontology.setDescription("test derived concept");
	ontology.setChLevel(chLevel);
	ontology.setcName("derivedtest");
	ontology.setcSynonymCd("dr");
	ontology.setcVisualAttributes("FA");
	ontology.setcFactTableColumn("concept_cd");
	ontology.setcTableName("concept_dimension");
	ontology.setcColumnName("concept_path");
	ontology.setcOperator("LIKE");
	ontology.setcDimcode("derived:test");
	ontology.setcTooltip("derived / test");
	ontology.setmAppliedPath("@");
	ontology.setSourceSystemCd("demo");
	ontology.setcTableCd("derivedtest");
	ontology.setAccessTableName("tableaccess");
	ontology.setcProtectedAccess("N");
	return ontology;
    }

    @Test
    public void testUpdateOntologyToI2b2() {
	OntologyDto ontology = createOntologyDto("\\Derived\\test\\", 1);
	OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();
	ontologyConceptSearchDto.setExistingConceptFullName("\\TNM\\Tumor\\");
	ontologyConceptSearchDto.setOntologyDto(ontology);
	int status = ontologyConceptDao.updateOntologyToI2b2(ontologyConceptSearchDto);
	assertThat(status).isNotNull().isEqualTo(1);
    }

    @Test
    public void testUpdateOntologyToTableAccess() {
	OntologyDto ontology = createOntologyDto("\\Derived\\test\\", 1);
	OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();
	ontologyConceptSearchDto.setExistingConceptFullName("\\TNM\\Tumor\\");
	ontologyConceptSearchDto.setOntologyDto(ontology);
	int status = ontologyConceptDao.updateOntologyToTableAccess(ontologyConceptSearchDto);
	assertThat(status).isNotNull().isEqualTo(1);
    }

    @Test
    public void testDeleteOntologyFromI2b2() {
	OntologyDto ontology = createOntologyDto("\\TNM\\Tumor\\", 1);
	int status = ontologyConceptDao.deleteOntologyFromI2b2(ontology);
	assertThat(status).isNotNull().isEqualTo(1);
    }

    @Test
    public void testDeleteOntologyFromTableAccess() {
	OntologyDto ontology = createOntologyDto("\\TNM\\Tumor\\", 1);
	int status = ontologyConceptDao.deleteOntologyFromTableAccess(ontology);
	assertThat(status).isNotNull().isEqualTo(1);
    }
}
