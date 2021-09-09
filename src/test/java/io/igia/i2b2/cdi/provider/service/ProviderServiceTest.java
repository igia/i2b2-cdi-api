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


package io.igia.i2b2.cdi.provider.service;

import io.igia.i2b2.cdi.common.exception.I2b2DataValidationException;
import io.igia.i2b2.cdi.provider.dao.ProviderDao;
import io.igia.i2b2.cdi.provider.dto.ProviderDto;
import io.igia.i2b2.cdi.provider.dto.ProviderSearchDto;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProviderServiceTest {

    @Mock
    private ProviderDao providerDao;
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ProviderService providerService;

    @Before
    public void setUp() {
        providerService = new ProviderServiceImpl(providerDao);
    }

    @Test
    public void testGetProviders() {
        List<ProviderDto> providers = Arrays.asList(
            createProvider(1, "demo"), createProvider(2, "test"));
        given(providerDao.findProviders(any())).willReturn(providers);

        List<ProviderDto> actualProviders = providerService.getProviders(new ProviderSearchDto());

        assertThat(actualProviders).isNotNull().isNotEmpty().size().isEqualTo(2);
        assertThat(actualProviders.get(0)).isEqualToComparingFieldByField(providers.get(0));
        assertThat(actualProviders.get(1)).isEqualToComparingFieldByField(providers.get(1));
    }

    @Test
    public void testValidateProvider() {
        List<ProviderDto> providers = Arrays.asList(
            createProvider(1, "demo"));
        given(providerDao.findProviders(any())).willReturn(providers);

        providerService.validate(new ProviderSearchDto());
        verify(providerDao, times(1)).findProviders(any());
    }

    @Test
    public void testValidateProvider_invalidProviderId() {
        given(providerDao.findProviders(any())).willReturn(Collections.emptyList());

        thrown.expect(I2b2DataValidationException.class);
        thrown.expectMessage("Invalid provider identifier");
        providerService.validate(new ProviderSearchDto().setProviderId("ABC"));
        verify(providerDao, times(1)).findProviders(any());
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
