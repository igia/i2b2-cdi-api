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

package io.igia.i2b2.cdi.concept;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.io.IOException;
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

import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.service.ConceptService;

@RunWith(SpringRunner.class)
@WebMvcTest(ConceptResource.class)
@AutoConfigureJsonTesters
public class ConceptResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ConceptService conceptService;

    @Autowired
    private JacksonTester<List<ConceptDto>> jacksonTester;

    @Autowired
    private JacksonTester<ConceptDto> jacksonTesterConcept;

    @Captor
    private ArgumentCaptor<ConceptDto> conceptArgumentCaptor;

    @Test
    @WithMockUser()
    public void getAllConcepts() throws Exception {

	List<ConceptDto> concepts = Arrays.asList(createConcept(1), createConcept(2), createConcept(3));

	PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);
	given(conceptService.getConcepts(any())).willReturn(result);

	MockHttpServletResponse response = mockMvc
		.perform(MockMvcRequestBuilders.get("/api/concepts").accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(result.getRecords()).getJson());
    }

    @Test
    @WithMockUser()
    public void getAllConcepts_filterBySource() throws Exception {

	List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"), createConcept(3, "demo"));
	PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);
	given(conceptService.getConcepts(argThat(searchDto -> searchDto.getSource().equals("demo"))))
		.willReturn(result);

	MockHttpServletResponse response = mockMvc
		.perform(
			MockMvcRequestBuilders.get("/api/concepts?source=demo").accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(result.getRecords()).getJson());
    }

    @Test
    @WithMockUser()
    public void getAllConcepts_filterByConceptPathContains() throws Exception {

	List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"), createConcept(3, "demo"));

	PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);
	given(conceptService.getConcepts(argThat(searchDto -> searchDto.getSource().equals("demo"))))
		.willReturn(result);

	MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
		.get("/api/concepts?source=demo&conceptPath[contains]=conc").accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(result.getRecords()).getJson());
    }
    
    @Test
    @WithMockUser()
    public void getAllConceptsFilterByConceptPathStartsWith() throws Exception {

        List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"), createConcept(3, "demo"));

        PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);
        given(conceptService.getConcepts(argThat(searchDto -> searchDto.getSource().equals("demo"))))
                .willReturn(result);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/concepts?source=demo&conceptPath[startsWith]=/conc").accept(MediaType.APPLICATION_JSON_UTF8))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(result.getRecords()).getJson());
    }

    @Test
    @WithMockUser()
    public void getAllConcepts_filterByConceptPathContains_paginationCriteria() throws Exception {

	List<ConceptDto> concepts = Arrays.asList(createConcept(1, "demo"), createConcept(3, "demo"));
	PaginationResult<ConceptDto> result = new PaginationResult<>(concepts, 0);
	given(conceptService.getConcepts(argThat(searchDto -> searchDto.getSource().equals("demo"))))
		.willReturn(result);

	MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
		.get("/api/concepts?source=demo&conceptPath[contains]=conc&order=ASC&page=1&size=2")
		.accept(MediaType.APPLICATION_JSON_UTF8)).andReturn().getResponse();

	assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
	assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(result.getRecords()).getJson());
    }

    private ConceptDto createConcept(int sequence) {
	return createConcept(sequence, "test");
    }

    private ConceptDto createConcept(int sequence, String source) {
	ConceptDto conceptDto = new ConceptDto();
	conceptDto.setCode("conc:" + sequence);
	conceptDto.setName("conc-name" + sequence);
	conceptDto.setConceptPath("/conc/conc-name/" + sequence);
	conceptDto.setSource(source != null ? source : "test");
	return conceptDto;
    }

    @Test
    @WithMockUser
    public void testCreateConcept() throws IOException, Exception {
	ConceptDto concept = createConcept(1);

	doNothing().when(conceptService).createConcept(ArgumentMatchers.any(ConceptDto.class));

	MockHttpServletResponse actualResponse = mockMvc
		.perform(MockMvcRequestBuilders.post("/api/concepts")
			.content(jacksonTesterConcept.write(concept).getJson())
			.contentType(MediaType.APPLICATION_JSON_UTF8).accept(MediaType.APPLICATION_JSON_UTF8))
		.andReturn().getResponse();

	assertThat(actualResponse.getStatus()).isNotNull().isEqualTo(HttpStatus.CREATED.value());

	verify(conceptService).createConcept(conceptArgumentCaptor.capture());
	assertThat(conceptArgumentCaptor.getValue()).isNotNull().isEqualToComparingFieldByField(concept);
    }
}
