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

package io.igia.i2b2.cdi.modifier;

import io.igia.i2b2.cdi.modifier.dto.ModifierDto;
import io.igia.i2b2.cdi.modifier.service.ModifierService;
import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@WebMvcTest(ModifierResource.class)
@AutoConfigureJsonTesters
public class ModifierResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ModifierService modifierService;

    @Autowired
    private JacksonTester<List<ModifierDto>> jacksonTester;

    @Test
    @WithMockUser()
    public void getAllModifiers() throws Exception {

        List<ModifierDto> modifiers = Arrays.asList(
            createModifier(1), createModifier(2), createModifier(3));

        given(modifierService.getModifiers(any())).willReturn(modifiers);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/modifiers")
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(modifiers).getJson());
    }

    @Test
    @WithMockUser()
    public void getAllModifiers_filterBySource() throws Exception {

        List<ModifierDto> modifiers = Arrays.asList(
            createModifier(1, "demo"), createModifier(3, "demo"));
        given(modifierService.getModifiers(argThat(searchDto -> searchDto.getSource().equals("demo")))).willReturn(modifiers);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/modifiers?source=demo")
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(modifiers).getJson());

        verify(modifierService, times(1)).getModifiers(argThat(searchDto -> searchDto.getSource().equals("demo")));
    }

    @Test
    @WithMockUser()
    public void getAllModifiers_filterByConceptCode() throws Exception {

        List<ModifierDto> modifiers = Arrays.asList(
            createModifier(1, "demo"), createModifier(3, "demo"));
        given(modifierService.getModifiers(argThat(searchDto -> searchDto.getConceptCode().equals("ICD9:160")))).willReturn(modifiers);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/modifiers?conceptCode=ICD9:160")
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(modifiers).getJson());

        verify(modifierService, times(1)).getModifiers(argThat(searchDto -> searchDto.getConceptCode().equals("ICD9:160")));
    }

    @Test
    @WithMockUser()
    public void getAllModifiers_filterByConceptCode_and_filterBySource() throws Exception {

        List<ModifierDto> modifiers = Arrays.asList(
            createModifier(1, "demo"), createModifier(3, "demo"));
        given(modifierService.getModifiers(argThat(searchDto ->
            searchDto.getConceptCode().equals("ICD9:160") && searchDto.getSource().equals("demo")))).willReturn(modifiers);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/modifiers?conceptCode=ICD9:160&source=demo")
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(modifiers).getJson());

        verify(modifierService, times(1)).getModifiers(argThat(searchDto ->
            searchDto.getConceptCode().equals("ICD9:160") && searchDto.getSource().equals("demo")));
    }

    private ModifierDto createModifier(int sequence) {
        return createModifier(sequence, "test");
    }

    private ModifierDto createModifier(int sequence, String source) {
        ModifierDto modifierDto = new ModifierDto();
        modifierDto.setCode("MOD:" + sequence);
        modifierDto.setName("MOD-name" + sequence);
        modifierDto.setModifierPath("/MOD/MOD-name/" + sequence);
        modifierDto.setSource(source != null ? source : "test");
        return modifierDto;
    }
}
