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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencyDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencySearchDto;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan({ "io.igia.i2b2.cdi.derivedconcept.dao", "io.igia.i2b2.cdi.common.database" })
@DirtiesContext
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:testdb" })
@Sql({ "/test-schema.sql", "/test-derivedconcept-schema.sql", "/test-ontology-schema.sql",
	"/test-derivedconcept-data.sql" })
public class DerivedConceptDependencyDaoTest {

    @Autowired
    DerivedConceptDependencyDao derivedConceptDependencyDao;

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
    public void testAddDerivedConceptDependency() {
	List<DerivedConceptDependencyDto> derivedConceptDependencies = Arrays.asList(
		createDerivedConceptDependency(null, 1, "/DerivedTest-2/", null),
		createDerivedConceptDependency(null, 1, "/DerivedTest-3/", null));
	int[] actualRowsAffected = derivedConceptDependencyDao.addDerivedConceptDependency(derivedConceptDependencies);
	assertThat(actualRowsAffected.length).isNotNull().isEqualTo(2);
    }

    @Test
    public void testGetAllDerivedConceptDependencies() {
	DerivedConceptDependencyDto expectedDerivedConceptDependency = createDerivedConceptDependency(1, 1,
		"\\Derived\\Test2\\", "\\Derived\\Test1\\");
	List<DerivedConceptDependencyDto> actualDerivedConceptDependencies = derivedConceptDependencyDao
		.getAllDerivedConceptDependencies();

	assertThat(actualDerivedConceptDependencies.size()).isNotNull().isEqualTo(4);
	assertThat(actualDerivedConceptDependencies.get(0)).isNotNull()
		.isEqualToComparingFieldByField(expectedDerivedConceptDependency);
    }

    @Test
    public void testGetDerivedConceptDependency() {
	DerivedConceptDependencyDto derivedConceptDependencies = createDerivedConceptDependency(1, 1,
		"\\Derived\\Test2\\", "\\Derived\\Test1\\");
	DerivedConceptDependencySearchDto derivedConceptDependencySearchDto = new DerivedConceptDependencySearchDto();
	derivedConceptDependencySearchDto.setDerivedConceptId(1);
	List<DerivedConceptDependencyDto> actualDerivedConceptDependencies = derivedConceptDependencyDao
		.getDerivedConceptDependency(derivedConceptDependencySearchDto);
	assertThat(actualDerivedConceptDependencies.size()).isNotNull().isEqualTo(3);
	assertThat(actualDerivedConceptDependencies.get(0)).isNotNull()
		.isEqualToComparingFieldByField(derivedConceptDependencies);
    }

    @Test
    public void testGetDerivedConceptDependency_byConceptPaths() {
	DerivedConceptDependencyDto derivedConceptDependencies = createDerivedConceptDependency(1, 1,
		"\\Derived\\Test2\\", "\\Derived\\Test1\\");
	DerivedConceptDependencySearchDto derivedConceptDependencySearchDto = new DerivedConceptDependencySearchDto();
	Set<String> conceptPaths = new HashSet<>();
	conceptPaths.add("\\Derived\\Test2\\");
	conceptPaths.add("\\Derived\\Test4\\");
	conceptPaths.add("\\Derived\\Test3\\");
	derivedConceptDependencySearchDto.setConceptPaths(conceptPaths);
	List<DerivedConceptDependencyDto> actualDerivedConceptDependencies = derivedConceptDependencyDao
		.getDerivedConceptDependency(derivedConceptDependencySearchDto);
	assertThat(actualDerivedConceptDependencies.size()).isNotNull().isEqualTo(4);
	assertThat(actualDerivedConceptDependencies.get(0)).isNotNull()
		.isEqualToComparingFieldByField(derivedConceptDependencies);
    }

    @Test
    public void testDeleteDerivedConceptDependencies() {
	List<String> parentConceptPaths = Arrays.asList("\\Derived\\Test2\\", "\\Derived\\Test3\\");
	DerivedConceptDependencySearchDto derivedConceptDependencySearchDto = new DerivedConceptDependencySearchDto();
	derivedConceptDependencySearchDto.setDerivedConceptId(1);
	derivedConceptDependencySearchDto.setParentConceptPaths(parentConceptPaths);
	int rowsAffected = derivedConceptDependencyDao
		.deleteDerivedConceptDependencies(derivedConceptDependencySearchDto);
	assertThat(rowsAffected).isNotNull().isEqualTo(2);
    }
}
