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

package io.igia.i2b2.cdi.derivedconcept;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.derivedconcept.dto.DependencyDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsFetchType;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsSearchDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptQueryMasterDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptType;
import io.igia.i2b2.cdi.derivedconcept.dto.Status;
import io.igia.i2b2.cdi.derivedconcept.service.DerivedConceptService;
import io.igia.i2b2.cdi.derivedconcept.service.DerivedConceptServiceImpl;

@RunWith(SpringRunner.class)
@WebMvcTest(DerivedConceptResource.class)
@AutoConfigureJsonTesters
public class DerivedConceptResourceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JacksonTester<DerivedConceptDto> jacksonDerivedConceptDto;

    @MockBean
    private DerivedConceptService derivedConceptService;

    @Autowired
    private JacksonTester<List<DerivedConceptDto>> jacksonTester;

    @Autowired
    private JacksonTester<List<DerivedConceptJobDetailsDto>> jacksonDerivedFactJobDetails;

    @Autowired
    private JacksonTester<List<DerivedConceptQueryMasterDto>> jacksonDerivedConceptQueryMasterDtos;

    @Captor
    ArgumentCaptor<DerivedConceptJobDetailsSearchDto> argumentCaptorDerivedConceptJobDetailsSearchDto;

    @Captor
    ArgumentCaptor<DerivedConceptDto> argumentCaptorDerivedConceptDto;

    @Test
    @WithMockUser
    public void getDerivedConceptById() throws Exception {
	DerivedConceptDto expectedDerivedConcept = createDerivedConcept(1, "\\Derived\\test\\", null);
	when(derivedConceptService.getDerivedConceptById(1)).thenReturn(expectedDerivedConcept);

	MockHttpServletResponse response = mockMvc.perform(
		MockMvcRequestBuilders.get("/api/derived-concepts/1").accept(MediaType.APPLICATION_JSON_UTF8_VALUE))
		.andReturn().getResponse();

	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	assertThat(response.getContentAsString())
		.isEqualTo(jacksonDerivedConceptDto.write(expectedDerivedConcept).getJson());
    }

    @Test
    @WithMockUser()
    public void getAllDerivedConcepts() throws Exception {

	List<DerivedConceptDto> concepts = Arrays.asList(createDerivedConcept(1, "\\Derived\\test\\", null),
		createDerivedConcept(2, "\\Derived\\test\\", null), createDerivedConcept(3, "\\Derived\\test\\", null));

	given(derivedConceptService.getDerivedConcepts()).willReturn(concepts);

	MockHttpServletResponse response = mockMvc
		.perform(MockMvcRequestBuilders.get("/api/derived-concepts").accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(concepts).getJson());
    }

    @Test
    @WithMockUser()
    public void testCreateDerivedConcept() throws IOException, Exception {
	DerivedConceptDto derivedConcept = createDerivedConcept(7, "/Derived/test",
		createDerivedConceptDependencies(2, "/Derived/Dtest"));
	given(derivedConceptService.addDerivedConcept(ArgumentMatchers.any())).willReturn(derivedConcept);
	MockHttpServletResponse response = mockMvc
		.perform(MockMvcRequestBuilders.post("/api/derived-concepts")
			.content(jacksonDerivedConceptDto.write(derivedConcept).getJson())
			.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(response.getStatus()).isNotNull().isEqualTo(HttpStatus.CREATED.value());
	assertThat(response.getContentAsString()).isEqualTo(jacksonDerivedConceptDto.write(derivedConcept).getJson());
    }

    private DerivedConceptDto createDerivedConcept(int sequence, String path, List<DependencyDto> dependencies) {
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
	return derivedConceptDto;
    }

    @Test
    @WithMockUser
    public void testUpdateDerivedConcept() throws IOException, Exception {
	DerivedConceptDto expectedDerivedConcept = createDerivedConcept(1, "\\Derived\\Test\\",
		createDerivedConceptDependencies(2, "\\Derived\\dependtest"));
	given(derivedConceptService.updateDerivedConcept(ArgumentMatchers.any())).willReturn(expectedDerivedConcept);
	MockHttpServletResponse actualResponse = mockMvc
		.perform(MockMvcRequestBuilders.put("/api/derived-concepts/1")
			.content(jacksonDerivedConceptDto.write(expectedDerivedConcept).getJson())
			.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	verify(derivedConceptService).updateDerivedConcept(argumentCaptorDerivedConceptDto.capture());
	assertThat(argumentCaptorDerivedConceptDto.getValue().getId()).isNotNull().isEqualTo(1);

	assertThat(actualResponse.getStatus()).isNotNull().isEqualTo(HttpStatus.OK.value());
	assertThat(actualResponse.getContentAsString()).isNotNull()
		.isEqualTo(jacksonDerivedConceptDto.write(expectedDerivedConcept).getJson());
    }

    @Test
    @WithMockUser
    public void testDeleteDerivedConcept() throws Exception {
	DerivedConceptDto expectedDerivedConcept = createDerivedConcept(1, "\\Derived\\Test\\", null);
	given(derivedConceptService.deleteDerivedConcept(ArgumentMatchers.any())).willReturn(expectedDerivedConcept);
	MockHttpServletResponse actualResponse = mockMvc
		.perform(MockMvcRequestBuilders.delete("/api/derived-concepts/1")
			.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(actualResponse.getStatus()).isNotNull().isEqualTo(HttpStatus.OK.value());
    }

    /**
     * This test, finds derived concepts and its dependent graphs for global fact
     * calculation.
     * 
     */
    @Test
    @WithMockUser
    public void testCalculateAllDerivedConcepts() throws Exception {
	given(derivedConceptService.calculateDerivedConcept(null)).willReturn(new int[2]);
	MockHttpServletResponse actualResponse = mockMvc
		.perform(MockMvcRequestBuilders.post("/api/derived-concepts/calculate-facts")
			.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(actualResponse.getStatus()).isNotNull().isEqualTo(HttpStatus.CREATED.value());
    }

    /**
     * This test, throws derived concept not matched exception.
     * 
     */
    @Test
    @WithMockUser
    public void testCalculateDerivedConceptIdNotFound() throws Exception {
	given(derivedConceptService.calculateDerivedConcept(ArgumentMatchers.any()))
		.willThrow(new I2b2Exception(DerivedConceptServiceImpl.CONCEPT_NOT_FOUND_ERROR + "10"));
	MockHttpServletResponse actualResponse = mockMvc
		.perform(MockMvcRequestBuilders.post("/api/derived-concepts/10/calculate-facts")
			.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(actualResponse.getStatus()).isNotNull().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
	assertThat(actualResponse.getContentAsString())
		.contains(DerivedConceptServiceImpl.CONCEPT_NOT_FOUND_ERROR + "10");
    }

    /**
     * This test, finds derived concept and its dependent graph nodes for fact
     * calculation.
     * 
     */
    @Test
    @WithMockUser
    public void testCalculateDerivedConceptIdFound() throws Exception {
	given(derivedConceptService.calculateDerivedConcept(ArgumentMatchers.any())).willReturn(new int[2]);
	MockHttpServletResponse actualResponse = mockMvc
		.perform(MockMvcRequestBuilders.post("/api/derived-concepts/1/calculate-facts")
			.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(actualResponse.getStatus()).isNotNull().isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    @WithMockUser
    public void testGetDerivedConceptJobDetails() throws Exception {
	List<DerivedConceptJobDetailsDto> derivedConceptJobDetails = Arrays.asList(
		createDerivedConceptJobDetail(1, Status.COMPLETED), createDerivedConceptJobDetail(2, Status.PENDING));

	given(derivedConceptService
		.getDerivedConceptJobDetails(ArgumentMatchers.any(DerivedConceptJobDetailsSearchDto.class)))
			.willReturn(derivedConceptJobDetails);

	MockHttpServletResponse actualResponse = mockMvc.perform(MockMvcRequestBuilders
		.get("/api/derived-concepts/jobs").param("derivedConceptId", "1,2").param("fetchType", "latest")
		.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)).andReturn()
		.getResponse();

	assertThat(actualResponse.getStatus()).isNotNull().isEqualTo(HttpStatus.OK.value());
	assertThat(actualResponse.getContentAsString()).isNotNull()
		.isEqualTo(jacksonDerivedFactJobDetails.write(derivedConceptJobDetails).getJson());

	DerivedConceptJobDetailsSearchDto expectedDerivedConceptJobDetailsSearchDto = new DerivedConceptJobDetailsSearchDto();
	expectedDerivedConceptJobDetailsSearchDto.setDerivedConceptIds(Arrays.asList(1, 2));
	expectedDerivedConceptJobDetailsSearchDto
		.setDerivedConceptJobDetailsFetchType(DerivedConceptJobDetailsFetchType.LATEST);

	verify(derivedConceptService)
		.getDerivedConceptJobDetails(argumentCaptorDerivedConceptJobDetailsSearchDto.capture());
	assertThat(argumentCaptorDerivedConceptJobDetailsSearchDto.getValue()).isNotNull()
		.isEqualToComparingFieldByField(expectedDerivedConceptJobDetailsSearchDto);
    }

    @Test
    @WithMockUser
    public void testGetDerivedConceptJobDetails_invalidDerivedConceptJobDetailsFetchType() throws Exception {
	MockHttpServletResponse actualResponse = mockMvc.perform(MockMvcRequestBuilders
		.get("/api/derived-concepts/jobs").param("derivedConceptId", "1,2").param("fetchType", "xyz")
		.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8)).andReturn()
		.getResponse();

	assertThat(actualResponse.getStatus()).isNotNull().isEqualTo(HttpStatus.BAD_REQUEST.value());
	assertThat(actualResponse.getContentAsString())
		.contains("Invalid fetchType provided. Please provide LATEST or ALL");
    }

    private DerivedConceptJobDetailsDto createDerivedConceptJobDetail(Integer id, Status status) {
	return createDerivedConceptJobDetail(id, "derived-fact error-stack", "Select * from table1", status);
    }

    private DerivedConceptJobDetailsDto createDerivedConceptJobDetail(Integer id, String errorStack,
	    String derivedConceptSql, Status status) {
	DerivedConceptJobDetailsDto jobDetails = new DerivedConceptJobDetailsDto();
	jobDetails.setDerivedConceptId(id);
	jobDetails.setId(id);
	jobDetails.setErrorStack(errorStack);
	jobDetails.setDerivedConceptSql(derivedConceptSql);
	jobDetails.setStatus(status);
	jobDetails.setCompletedOn(Instant.now());
	jobDetails.setStartedOn(Instant.now());
	return jobDetails;
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

    @Test
    @WithMockUser
    public void testGetQueryMasters() throws Exception {
	List<DerivedConceptQueryMasterDto> masterQueries = Arrays.asList(createDerivedConceptQueryMasterDto(1,
		"DELETE FROM global_temp_table; DELETE FROM dx; with t as (   select  p.patient_num   from i2b2demodata.dbo.patient_dimension p  where   p.patient_num IN (select patient_num from  i2b2demodata.dbo.patient_dimension   where income_cd = 'High')    group by  p.patient_num   )  insert into  global_temp_table (patient_num, panel_count) select  t.patient_num, 0 as panel_count  from t    update  global_temp_table set panel_count =1 where  global_temp_table.panel_count =  0 and exists ( select 1 from ( select  f.patient_num   from i2b2demodata.dbo.observation_fact f  where   f.concept_cd IN (select concept_cd from  i2b2demodata.dbo.concept_dimension   where concept_path LIKE '\\i2b2\\Demographics\\Marital Status\\Single\\%')    group by  f.patient_num ) t where  global_temp_table.patient_num = t.patient_num    )     insert into  dx (  patient_num   ) select * from ( select distinct  patient_num  from  global_temp_table where panel_count = 1 ) q SELECT DISTINCT dx.patient_num,'1970-01-01 00:00:00' AS start_date, '1970-01-01 00:00:00' AS end_date, -1 AS encounter_num, 0 AS provider_id, 'T' AS valtype_cd, '' AS tval_char, 0 AS nval_num FROM dx"),
		createDerivedConceptQueryMasterDto(2,
			"TRUNCATE TABLE global_temp_table; TRUNCATE TABLE dx; with t as ( select  p.patient_num   from i2b2demodata.dbo.patient_dimension p  where   p.patient_num IN (select patient_num from  i2b2demodata.dbo.patient_dimension   where birth_date > getdate() - (365.25*10) +1)    group by  p.patient_num   )  insert into  global_temp_table (patient_num, panel_count) select  t.patient_num, 0 as panel_count  from t    with t as (   select  p.patient_num   from i2b2demodata.dbo.patient_dimension p  where   p.patient_num IN (select patient_num from  i2b2demodata.dbo.patient_dimension   where birth_date BETWEEN getdate() - (365.25*45) +1  AND getdate() - (365.25*35) + 1)    group by  p.patient_num   )  insert into  global_temp_table (patient_num, panel_count) select  t.patient_num, 0 as panel_count  from t     insert into  dx (  patient_num   ) select * from ( select distinct  patient_num  from  global_temp_table where panel_count = 0 ) q SELECT DISTINCT dx.patient_num,'1970-01-01 00:00:00' AS start_date, '1970-01-01 00:00:00' AS end_date, -1 AS encounter_num, 0 AS provider_id, 'T' AS valtype_cd, '' AS tval_char, 0 AS nval_num FROM dx"));

	given(derivedConceptService.getQueryMasterRecords(ArgumentMatchers.any())).willReturn(masterQueries);

	MockHttpServletResponse response = mockMvc.perform(
		MockMvcRequestBuilders.get("/api/derived-concepts/querymaster").accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(response.getStatus()).isNotNull().isEqualTo(HttpStatus.OK.value());
	assertThat(response.getContentAsString()).isNotNull()
		.isEqualTo(jacksonDerivedConceptQueryMasterDtos.write(masterQueries).getJson());
    }

    @Test
    @WithMockUser
    public void testGetQueryMasters_withNumberOfRecords() throws Exception {
	List<DerivedConceptQueryMasterDto> masterQueries = Arrays.asList(createDerivedConceptQueryMasterDto(1,
		"DELETE FROM global_temp_table; DELETE FROM dx; with t as (   select  p.patient_num   from i2b2demodata.dbo.patient_dimension p  where   p.patient_num IN (select patient_num from  i2b2demodata.dbo.patient_dimension   where income_cd = 'High')    group by  p.patient_num   )  insert into  global_temp_table (patient_num, panel_count) select  t.patient_num, 0 as panel_count  from t    update  global_temp_table set panel_count =1 where  global_temp_table.panel_count =  0 and exists ( select 1 from ( select  f.patient_num   from i2b2demodata.dbo.observation_fact f  where   f.concept_cd IN (select concept_cd from  i2b2demodata.dbo.concept_dimension   where concept_path LIKE '\\i2b2\\Demographics\\Marital Status\\Single\\%')    group by  f.patient_num ) t where  global_temp_table.patient_num = t.patient_num    )     insert into  dx (  patient_num   ) select * from ( select distinct  patient_num  from  global_temp_table where panel_count = 1 ) q SELECT DISTINCT dx.patient_num,'1970-01-01 00:00:00' AS start_date, '1970-01-01 00:00:00' AS end_date, -1 AS encounter_num, 0 AS provider_id, 'T' AS valtype_cd, '' AS tval_char, 0 AS nval_num FROM dx"),
		createDerivedConceptQueryMasterDto(2,
			"TRUNCATE TABLE global_temp_table; TRUNCATE TABLE dx; with t as ( select  p.patient_num   from i2b2demodata.dbo.patient_dimension p  where   p.patient_num IN (select patient_num from  i2b2demodata.dbo.patient_dimension   where birth_date > getdate() - (365.25*10) +1)    group by  p.patient_num   )  insert into  global_temp_table (patient_num, panel_count) select  t.patient_num, 0 as panel_count  from t    with t as (   select  p.patient_num   from i2b2demodata.dbo.patient_dimension p  where   p.patient_num IN (select patient_num from  i2b2demodata.dbo.patient_dimension   where birth_date BETWEEN getdate() - (365.25*45) +1  AND getdate() - (365.25*35) + 1)    group by  p.patient_num   )  insert into  global_temp_table (patient_num, panel_count) select  t.patient_num, 0 as panel_count  from t     insert into  dx (  patient_num   ) select * from ( select distinct  patient_num  from  global_temp_table where panel_count = 0 ) q SELECT DISTINCT dx.patient_num,'1970-01-01 00:00:00' AS start_date, '1970-01-01 00:00:00' AS end_date, -1 AS encounter_num, 0 AS provider_id, 'T' AS valtype_cd, '' AS tval_char, 0 AS nval_num FROM dx"));

	given(derivedConceptService.getQueryMasterRecords(ArgumentMatchers.anyInt())).willReturn(masterQueries);

	MockHttpServletResponse response = mockMvc
		.perform(MockMvcRequestBuilders.get("/api/derived-concepts/querymaster").param("fetchSize", "30")
			.accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(response.getStatus()).isNotNull().isEqualTo(HttpStatus.OK.value());
	assertThat(response.getContentAsString()).isNotNull()
		.isEqualTo(jacksonDerivedConceptQueryMasterDtos.write(masterQueries).getJson());
    }

    private DerivedConceptQueryMasterDto createDerivedConceptQueryMasterDto(int id, String query) {
	DerivedConceptQueryMasterDto derivedConceptQueryMasterDto = new DerivedConceptQueryMasterDto();
	derivedConceptQueryMasterDto.setId(id);
	derivedConceptQueryMasterDto.setName("derive:test-" + id);
	derivedConceptQueryMasterDto.setCreatedDate(Timestamp.valueOf("2019-10-10 00:00:00").toInstant());
	derivedConceptQueryMasterDto.setGeneratedSql(query);
	return derivedConceptQueryMasterDto;
    }
}
