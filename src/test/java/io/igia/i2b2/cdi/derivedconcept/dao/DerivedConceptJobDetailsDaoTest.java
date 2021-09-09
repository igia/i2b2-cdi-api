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
import java.util.Collections;
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

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsFetchType;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsSearchDto;
import io.igia.i2b2.cdi.derivedconcept.dto.Status;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan({ "io.igia.i2b2.cdi.derivedconcept.dao", "io.igia.i2b2.cdi.common.database" })
@DirtiesContext
@TestPropertySource(properties = { "spring.datasource.url=jdbc:h2:mem:testdb" })
@Sql({ "/test-derivedconceptjobdetails-schema.sql", "/test-derivedconceptjobdetails-data.sql" })
public class DerivedConceptJobDetailsDaoTest {

    @Autowired
    private DerivedConceptJobDetailsDao derivedConceptJobDetailsDao;

    @Test
    public void testFindDerivedConceptJobDetails() {
	DerivedConceptJobDetailsSearchDto searchDto = new DerivedConceptJobDetailsSearchDto();
	searchDto.setDerivedConceptIds(Arrays.asList(1, 2, 3));
	DerivedConceptJobDetailsDto expectedDerivedConceptJobDetail = createDerivedConceptJobDetails(1, "",
		"Select * from table1", Status.COMPLETED, 1);
	List<DerivedConceptJobDetailsDto> actualDerivedConceptJobDetails = derivedConceptJobDetailsDao
		.findDerivedConceptJobDetails(searchDto);
	assertThat(actualDerivedConceptJobDetails.size()).isNotNull().isEqualTo(6);
	assertThat(actualDerivedConceptJobDetails.get(0)).isNotNull()
		.isEqualToIgnoringGivenFields(expectedDerivedConceptJobDetail, "completedOn", "startedOn");
    }

    @Test
    public void testFindDerivedConceptJobDetails_fetchTypeLatest() {
	DerivedConceptJobDetailsSearchDto searchDto = new DerivedConceptJobDetailsSearchDto();
	searchDto.setDerivedConceptIds(Arrays.asList(1, 2, 3));
	searchDto.setDerivedConceptJobDetailsFetchType(DerivedConceptJobDetailsFetchType.LATEST);
	DerivedConceptJobDetailsDto derivedConceptJobDetail = createDerivedConceptJobDetails(4, "",
		"Select * from table1", Status.COMPLETED, 1);
	List<DerivedConceptJobDetailsDto> actualDerivedConceptJobDetails = derivedConceptJobDetailsDao
		.findDerivedConceptJobDetails(searchDto);
	assertThat(actualDerivedConceptJobDetails.size()).isNotNull().isEqualTo(3);
	assertThat(actualDerivedConceptJobDetails.get(0)).isNotNull()
		.isEqualToIgnoringGivenFields(derivedConceptJobDetail, "completedOn", "startedOn");
    }

    @Test
    public void testFindDerivedConceptJobDetails_fetchTypeAll() {
	DerivedConceptJobDetailsSearchDto searchDto = new DerivedConceptJobDetailsSearchDto();
	searchDto.setDerivedConceptIds(Arrays.asList(1, 2, 3));
	searchDto.setDerivedConceptJobDetailsFetchType(DerivedConceptJobDetailsFetchType.ALL);
	DerivedConceptJobDetailsDto expectedDerivedConceptJobDetail = createDerivedConceptJobDetails(1, "",
		"Select * from table1", Status.COMPLETED, 1);
	List<DerivedConceptJobDetailsDto> actualDerivedConceptJobDetails = derivedConceptJobDetailsDao
		.findDerivedConceptJobDetails(searchDto);
	assertThat(actualDerivedConceptJobDetails.size()).isNotNull().isEqualTo(6);
	assertThat(actualDerivedConceptJobDetails.get(0)).isNotNull().isEqualToIgnoringGivenFields(expectedDerivedConceptJobDetail,
		"completedOn", "startedOn");
    }

    @Test
    public void testFindDerivedConceptJobDetails_emptyDerivedConceptIdList_fetchTypeLatest() {
	DerivedConceptJobDetailsSearchDto searchDto = new DerivedConceptJobDetailsSearchDto();
	searchDto.setDerivedConceptJobDetailsFetchType(DerivedConceptJobDetailsFetchType.LATEST);
	DerivedConceptJobDetailsDto expectedDerivedConceptJobDetail = createDerivedConceptJobDetails(4, "",
		"Select * from table1", Status.COMPLETED, 1);
	List<DerivedConceptJobDetailsDto> actualDerivedConceptJobDetails = derivedConceptJobDetailsDao
		.findDerivedConceptJobDetails(searchDto);
	assertThat(actualDerivedConceptJobDetails.size()).isNotNull().isEqualTo(3);
	assertThat(actualDerivedConceptJobDetails.get(0)).isNotNull().isEqualToIgnoringGivenFields(expectedDerivedConceptJobDetail,
		"completedOn", "startedOn");
    }

    @Test
    public void testFindDerivedConceptJobDetails_emptyDerivedConceptIdList_fetchTypeALL() {
	DerivedConceptJobDetailsSearchDto searchDto = new DerivedConceptJobDetailsSearchDto();
	searchDto.setDerivedConceptIds(Collections.emptyList());
	searchDto.setDerivedConceptJobDetailsFetchType(DerivedConceptJobDetailsFetchType.ALL);
	DerivedConceptJobDetailsDto expectedDerivedConceptJobDetail = createDerivedConceptJobDetails(1, "",
		"Select * from table1", Status.COMPLETED, 1);
	List<DerivedConceptJobDetailsDto> actualDerivedConceptJobDetails = derivedConceptJobDetailsDao
		.findDerivedConceptJobDetails(searchDto);
	assertThat(actualDerivedConceptJobDetails.size()).isNotNull().isEqualTo(6);
	assertThat(actualDerivedConceptJobDetails.get(0)).isNotNull().isEqualToIgnoringGivenFields(expectedDerivedConceptJobDetail,
		"completedOn", "startedOn");
    }

    @Test
    public void testFindDerivedConceptJobDetails_emptyDerivedConceptIdList_fetchTypeNull() {
	DerivedConceptJobDetailsSearchDto searchDto = new DerivedConceptJobDetailsSearchDto();
	searchDto.setDerivedConceptIds(Collections.emptyList());
	searchDto.setDerivedConceptJobDetailsFetchType(null);
	DerivedConceptJobDetailsDto expectedDerivedConceptJobDetail = createDerivedConceptJobDetails(1, "",
		"Select * from table1", Status.COMPLETED, 1);
	List<DerivedConceptJobDetailsDto> actualDerivedConceptJobDetails = derivedConceptJobDetailsDao
		.findDerivedConceptJobDetails(searchDto);
	assertThat(actualDerivedConceptJobDetails.size()).isNotNull().isEqualTo(6);
	assertThat(actualDerivedConceptJobDetails.get(0)).isNotNull().isEqualToIgnoringGivenFields(expectedDerivedConceptJobDetail,
		"completedOn", "startedOn");
    }

    @Test
    public void testFindDerivedConceptJobDetails_nullDerivedConceptIdList_fetchTypeNull() {
	DerivedConceptJobDetailsSearchDto searchDto = new DerivedConceptJobDetailsSearchDto();
	searchDto.setDerivedConceptIds(null);
	searchDto.setDerivedConceptJobDetailsFetchType(null);
	DerivedConceptJobDetailsDto expectedDerivedConceptJobDetail = createDerivedConceptJobDetails(1, "",
		"Select * from table1", Status.COMPLETED, 1);
	List<DerivedConceptJobDetailsDto> actualDerivedConceptJobDetails = derivedConceptJobDetailsDao
		.findDerivedConceptJobDetails(searchDto);
	assertThat(actualDerivedConceptJobDetails.size()).isNotNull().isEqualTo(6);
	assertThat(actualDerivedConceptJobDetails.get(0)).isNotNull().isEqualToIgnoringGivenFields(expectedDerivedConceptJobDetail,
		"completedOn", "startedOn");
    }
    
    /**
     * Below test inserts list of job details into derived_concept_job_details table
     */
    @Test
    public void testCreateDerivedConceptJobDetails() {

        List<DerivedConceptJobDetailsDto> jobDetails = Arrays.asList(
                createDerivedConceptJobDetails(0, "", "", Status.PENDING, 5),
                createDerivedConceptJobDetails(1, "", "", Status.PENDING, 6));
        int[] rowsAffected = derivedConceptJobDetailsDao.createDerivedConceptJobDetails(jobDetails);
        assertThat(rowsAffected).isNotNull().isEqualTo(new int[] {1,1});
    }

    private DerivedConceptJobDetailsDto createDerivedConceptJobDetails(Integer id, String errorStack,
	    String derivedConceptSql, Status status, Integer derivedConceptId) {
	DerivedConceptJobDetailsDto derivedConceptJobDetailsDto = new DerivedConceptJobDetailsDto();
	derivedConceptJobDetailsDto.setDerivedConceptId(derivedConceptId);
	derivedConceptJobDetailsDto.setId(id);
	derivedConceptJobDetailsDto.setErrorStack(errorStack);
	derivedConceptJobDetailsDto.setDerivedConceptSql(derivedConceptSql);
	derivedConceptJobDetailsDto.setStatus(status);
	return derivedConceptJobDetailsDto;
    }
}
