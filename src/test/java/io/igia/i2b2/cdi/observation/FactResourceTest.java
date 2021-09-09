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

package io.igia.i2b2.cdi.observation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.observation.dto.FactDto;
import io.igia.i2b2.cdi.observation.dto.FactModifierDto;
import io.igia.i2b2.cdi.observation.service.ObservationService;

@RunWith(SpringRunner.class)
@WebMvcTest(FactResource.class)
@AutoConfigureJsonTesters
public class FactResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ObservationService observationService;

    @Autowired
    private JacksonTester<FactDto> jacksonTester;
    
    @Autowired
    private JacksonTester<List<FactDto>> jacksonTester1;

    @Test
    @WithMockUser()
    public void addObservation() throws Exception {

        FactDto observation = new FactDto();
        observation.setStartDate(LocalDateTime.now());
        observation.setConceptCode("CC");
        observation.setPatientId("P");

        given(observationService.addObservation(any())).willReturn(observation);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/facts")
            .content(jacksonTester.write(observation).getJson())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(observation).getJson());
    }

    @Test
    @WithMockUser()
    public void addObservation_missingConceptCode() throws Exception {

        FactDto observation = new FactDto();
        observation.setStartDate(LocalDateTime.now());
        observation.setPatientId("P");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/facts")
            .content(jacksonTester.write(observation).getJson())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn()
            .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("\"field\":\"conceptCode\",\"message\":\"must not be blank\"");
    }

    @Test
    @WithMockUser()
    public void addObservation_missingPatientId() throws Exception {

        FactDto observation = new FactDto();
        observation.setStartDate(LocalDateTime.now());
        observation.setConceptCode("CC");

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/facts")
            .content(jacksonTester.write(observation).getJson())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn()
            .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("\"field\":\"patientId\",\"message\":\"must not be blank\"");
    }

    @Test
    @WithMockUser()
    public void addObservation_invalidStartDate() throws Exception {

        FactDto observation = new FactDto();
        observation.setPatientId("P");
        observation.setConceptCode("CC");
        observation.setStartDate(LocalDateTime.now());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/facts")
            .content(jacksonTester.write(observation).getJson().replaceAll(
                "\"startDate\":\\s*\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\"",
                "\"startDate\": \"2018-03-31T24:01:00\""))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn()
            .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Date '2018-03-31T24:01:00' should be in format 'yyyy-MM-ddTHH:mm:ss'");
    }

    @Test
    @WithMockUser()
    public void addObservation_invalidEndDate() throws Exception {

        FactDto observation = new FactDto();
        observation.setPatientId("P");
        observation.setConceptCode("CC");
        observation.setStartDate(LocalDateTime.now());
        observation.setEndDate(LocalDateTime.now());

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/facts")
            .content(jacksonTester.write(observation).getJson().replaceAll(
                "\"endDate\":\\s*\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\"",
                "\"endDate\": \"2018-03-31T24:01:00\""))
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn()
            .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("Date '2018-03-31T24:01:00' should be in format 'yyyy-MM-ddTHH:mm:ss'");
    }

    @Test
    @WithMockUser()
    public void addObservation_duplicateIdentifier() throws Exception {

        FactDto observation = new FactDto();
        observation.setPatientId("P");
        observation.setConceptCode("CC");
        observation.setStartDate(LocalDateTime.now());

        given(observationService.addObservation(any())).willThrow(new DuplicateKeyException("exception"));

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .post("/api/facts")
            .content(jacksonTester.write(observation).getJson())
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn()
            .getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.REQUEST_TIMEOUT.value());
        assertThat(response.getContentAsString()).contains("Your request could not be processed at this time. Please try again.");
    }
    
    @Test
    @WithMockUser()
    public void getObservations() throws Exception {
        List<FactDto> facts = Arrays.asList(createFact(1, false), createFact(2, false), createFact(3, false));

        PaginationResult<FactDto> result = new PaginationResult<>(facts, 3);
        given(observationService.getObservations(any())).willReturn(result);

        MockHttpServletResponse response = mockMvc
            .perform(MockMvcRequestBuilders.get("/api/facts").accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester1.write(result.getRecords()).getJson());
    }
    
    private FactDto createFact(int i, boolean modifierFlag) {
        FactDto factDto = new FactDto();
        factDto.setPatientId("P" + i);
        factDto.setEncounterId("E" + i);
        factDto.setConceptCode("C" + 1);
        if (modifierFlag) {
            List<FactModifierDto> modifiers = new ArrayList<>();
            FactModifierDto factModifierDto = new FactModifierDto();
            factModifierDto.setModifierCode("M" + 1);
            modifiers.add(factModifierDto);
            factDto.setModifiers(modifiers);
        }
        return factDto;
    }

    @Test
    @WithMockUser()
    public void getObservationsModifiersFlagTrue() throws Exception {
        List<FactDto> facts = Arrays.asList(createFact(1, true), createFact(2, true), createFact(3, true));

        PaginationResult<FactDto> result = new PaginationResult<>(facts, 3);
        given(observationService.getObservations(any())).willReturn(result);

        MockHttpServletResponse response = mockMvc
            .perform(MockMvcRequestBuilders.get("/api/facts").accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester1.write(result.getRecords()).getJson());
    }
    
    @Test
    @WithMockUser()
    public void getObservationsByPatientNum() throws Exception {
        List<FactDto> facts = Arrays.asList(createFact(1, false));

        PaginationResult<FactDto> result = new PaginationResult<>(facts, 1);
        given(observationService.getObservations(any())).willReturn(result);

        MockHttpServletResponse response = mockMvc
            .perform(MockMvcRequestBuilders.get("/api/facts?patientNum=1").accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester1.write(result.getRecords()).getJson());
    }
    
    @Test
    @WithMockUser()
    public void getObservationsByConceptCode() throws Exception {
        List<FactDto> facts = Arrays.asList(createFact(1, false));

        PaginationResult<FactDto> result = new PaginationResult<>(facts, 1);
        given(observationService.getObservations(any())).willReturn(result);

        MockHttpServletResponse response = mockMvc
            .perform(MockMvcRequestBuilders.get("/api/facts?conceptCode=C1").accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester1.write(result.getRecords()).getJson());
    }
    
    @Test
    @WithMockUser()
    public void getObservationsByConceptCodeAndPagination() throws Exception {
        List<FactDto> facts = Arrays.asList(createFact(1, false), createFact(2, false));

        PaginationResult<FactDto> result = new PaginationResult<>(facts, 2);
        given(observationService.getObservations(any())).willReturn(result);

        MockHttpServletResponse response = mockMvc
            .perform(MockMvcRequestBuilders.get("/api/facts?conceptCode=C1&order=ASC&page=1&size=2").accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester1.write(result.getRecords()).getJson());
    }
}
