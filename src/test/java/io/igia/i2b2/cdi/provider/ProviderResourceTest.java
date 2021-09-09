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



package io.igia.i2b2.cdi.provider;

import io.igia.i2b2.cdi.provider.dto.ProviderDto;
import io.igia.i2b2.cdi.provider.service.ProviderService;
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

@RunWith(SpringRunner.class)
@WebMvcTest(ProviderResource.class)
@AutoConfigureJsonTesters
public class ProviderResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProviderService providerService;

    @Autowired
    private JacksonTester<List<ProviderDto>> jacksonTester;

    @Test
    @WithMockUser()
    public void getAllProviders() throws Exception {

        List<ProviderDto> providers = Arrays.asList(
            createProvider(1), createProvider(2), createProvider(3));

        given(providerService.getProviders(any())).willReturn(providers);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/providers")
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(providers).getJson());
    }

    @Test
    @WithMockUser()
    public void getAllProviders_filterBySource() throws Exception {

        List<ProviderDto> providers = Arrays.asList(
            createProvider(1, "demo"), createProvider(3, "demo"));
        given(providerService.getProviders(argThat(searchDto -> searchDto.getSource().equals("demo")))).willReturn(providers);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
            .get("/api/providers?source=demo")
            .accept(MediaType.APPLICATION_JSON_UTF8)
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo(jacksonTester.write(providers).getJson());
    }

//    @Test
//    @WithMockUser()
//    public void getAllProviders_notFound() throws Exception {
//
//        List<ProviderDto> providers = Arrays.asList(
//            createProvider(1, "demo"), createProvider(3, "demo"));
//        given(providerService.getProviders(any())).willThrow(new RuntimeException());
//
//        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders
//            .get("/api/providers?source=test")
//            .accept(MediaType.APPLICATION_JSON_UTF8)
//        ).andReturn().getResponse();
//
//        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        assertThat(response.getContentAsString()).isEqualTo("");
//    }

    private ProviderDto createProvider(int sequence) {
        return createProvider(sequence, "test");
    }

    private ProviderDto createProvider(int sequence, String source) {
        ProviderDto providerDto = new ProviderDto();
        providerDto.setId("prov:" + sequence);
        providerDto.setName("prov-name" + sequence);
        providerDto.setProviderPath("/prov/prov-name/" + sequence);
        providerDto.setSource(source != null ? source : "test");
        return providerDto;
    }
}
