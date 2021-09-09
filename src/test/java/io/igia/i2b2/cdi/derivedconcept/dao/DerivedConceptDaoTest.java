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

package io.igia.i2b2.cdi.derivedconcept.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan({ "io.igia.i2b2.cdi.derivedconcept.dao", "io.igia.i2b2.cdi.common.database" })
@DirtiesContext
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:testdb" })
@Sql({ "/test-schema.sql", "/test-derivedconcept-schema.sql", "/test-ontology-schema.sql",
	"/test-derivedconcept-data.sql" })
public class DerivedConceptDaoTest {
    @Autowired
    private DerivedConceptDao derivedConceptDao;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testFindDerivedConceptById() {
	DerivedConceptDto expectedDerivedConcept = createDerivedConcept(1, "\\Derived\\Test1\\", "derived:test1");
	DerivedConceptDto actualDerivedConcept = derivedConceptDao.findDerivedConceptById(1);
	assertThat(actualDerivedConcept).isNotNull();
	assertThat(actualDerivedConcept.getId()).isNotNull().isEqualTo(1);
	assertThat(actualDerivedConcept).isEqualToComparingOnlyGivenFields(expectedDerivedConcept, "id", "code");
    }

    @Test
    public void testFindDerivedConceptByIdDerivedConceptNotFound() {
        DerivedConceptDto expectedDerivedConcept = derivedConceptDao.findDerivedConceptById(5);
        assertThat(expectedDerivedConcept).isNull();
    }

    @Test
    public void testFindDerivedConcepts() {
	List<DerivedConceptDto> concepts = derivedConceptDao.findDerivedConcepts();
	assertThat(concepts).isNotNull().isNotEmpty().size().isEqualTo(3);

	DerivedConceptDto expectedConcept = createDerivedConcept(1);
	assertThat(concepts.get(0)).isEqualToIgnoringGivenFields(expectedConcept, "updatedOn");
    }

    private DerivedConceptDto createDerivedConcept(int sequence) {
	DerivedConceptDto derivedConceptDto = new DerivedConceptDto();
	derivedConceptDto.setId(sequence);
	derivedConceptDto.setPath("\\Derived\\Test" + sequence + "\\");
	derivedConceptDto.setCode("derived:test" + sequence);
	derivedConceptDto.setDescription("Test description " + sequence);
	derivedConceptDto.setFactQuery("Select * from table" + sequence);
	derivedConceptDto.setUnit("test-unit");
	derivedConceptDto.setUpdatedOn(Instant.now());
	return derivedConceptDto;
    }

    private DerivedConceptDto createDerivedConcept(Integer id, String path, String code) {
	DerivedConceptDto derivedConcept = new DerivedConceptDto();
	derivedConcept.setId(id);
	derivedConcept.setPath(path);
	derivedConcept.setCode(code);
	return derivedConcept;
    }

    @Test
    public void testAddDerivedConcept() {
	DerivedConceptDto derivedFact = createDerivedConcept(7);
	int actualResult = derivedConceptDao.addDerivedConcept(derivedFact);
	assertThat(actualResult).isGreaterThan(0);

    }

    @Test
    public void testUpdateDerivedFact() {
	DerivedConceptDto derivedFact = createDerivedConcept(1);
	int actualResult = derivedConceptDao.updateDerivedConcept(derivedFact);
	assertThat(actualResult).isEqualTo(1);
    }

    @Test
    public void testDeleteDerivedConcept() {
	DerivedConceptDto derivedFact = createDerivedConcept(1);
	int actualResult = derivedConceptDao.deleteDerivedConcept(derivedFact);
	assertThat(actualResult).isEqualTo(1);
    }
}