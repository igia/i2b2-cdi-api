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

import java.sql.Timestamp;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptQueryMasterDto;


@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan({ "io.igia.i2b2.cdi.derivedconcept.dao", "io.igia.i2b2.cdi.common.database" })
@DirtiesContext
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:testdb" })
@Sql({ "/test-derivedconceptquerymaster-schema.sql", "/test-derivedconceptquerymaster-data.sql" })
public class DerivedConceptQueryMasterDaoTest {
    @Autowired
    DerivedConceptQueryMasterDao derivedConceptQueryMasterDao;

    @Test
    public void testGetQueryMaster() {
	DerivedConceptQueryMasterDto expectedQuery = createDerivedConceptQueryMasterDto(5, "derived:lastbpc",
		"2019-10-10 00:00:00");
	List<DerivedConceptQueryMasterDto> actualQueries = derivedConceptQueryMasterDao.getQueryMaster(50);
	assertThat(actualQueries.size()).isNotNull().isLessThanOrEqualTo(50);
	assertThat(actualQueries.size()).isNotNull().isEqualTo(5);
	assertThat(actualQueries.get(0)).isNotNull().isEqualToComparingFieldByField(expectedQuery);
    }

    private DerivedConceptQueryMasterDto createDerivedConceptQueryMasterDto(int id, String name, String createdDate) {
	DerivedConceptQueryMasterDto derivedConceptQueryMasterDto = new DerivedConceptQueryMasterDto();
	derivedConceptQueryMasterDto.setId(id);
	derivedConceptQueryMasterDto.setName(name);
	derivedConceptQueryMasterDto.setCreatedDate(Timestamp.valueOf(createdDate).toInstant());
	derivedConceptQueryMasterDto.setGeneratedSql("select * from dx");
	return derivedConceptQueryMasterDto;
    }

}
