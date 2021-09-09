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


package io.igia.i2b2.cdi.provider.dao;

import io.igia.i2b2.cdi.provider.dto.ProviderDto;
import io.igia.i2b2.cdi.provider.dto.ProviderSearchDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan("io.igia.i2b2.cdi.provider.dao")
@Sql({"/test-schema.sql", "/test-provider-data.sql"})
@DirtiesContext
public class ProviderDaoTest {

    @Autowired
    private ProviderDao providerDao;

    @Test
    public void findProviders_noRecords() {
        ProviderSearchDto searchDto = new ProviderSearchDto();
        searchDto.setSource("notpresent");

        List<ProviderDto> providers = providerDao.findProviders(searchDto);
        assertThat(providers).isNotNull().isEmpty();
    }

    @Test
    public void findProviders_noFilterCriteria() {
        List<ProviderDto> providers = providerDao.findProviders(new ProviderSearchDto());
        assertThat(providers).isNotNull().isNotEmpty().size().isEqualTo(3);

        ProviderDto expectedProvider = new ProviderDto();
        expectedProvider.setSource("demo");
        expectedProvider.setProviderPath("/test/1");
        expectedProvider.setName("test1");
        expectedProvider.setId("1");
        assertThat(providers.get(0)).isEqualToComparingFieldByField(expectedProvider);
    }

    @Test
    public void findProviders_filterBySourceSystem() {
        ProviderSearchDto searchDto = new ProviderSearchDto();
        searchDto.setSource("test");
        List<ProviderDto> providers = providerDao.findProviders(searchDto);
        assertThat(providers).isNotNull().isNotEmpty().size().isEqualTo(1);

        ProviderDto expectedProvider = new ProviderDto();
        expectedProvider.setSource("test");
        expectedProvider.setProviderPath("/test/2");
        expectedProvider.setName("test2");
        expectedProvider.setId("2");
        assertThat(providers.get(0)).isEqualToComparingFieldByField(expectedProvider);
    }

    @Test
    public void findProviders_filterBySourceSystem_caseInsensitive() {
        ProviderSearchDto searchDto = new ProviderSearchDto();
        searchDto.setSource("TeST");
        List<ProviderDto> providers = providerDao.findProviders(searchDto);
        assertThat(providers).isNotNull().isNotEmpty().size().isEqualTo(1);

        ProviderDto expectedProvider = new ProviderDto();
        expectedProvider.setSource("test");
        expectedProvider.setProviderPath("/test/2");
        expectedProvider.setName("test2");
        expectedProvider.setId("2");
        assertThat(providers.get(0)).isEqualToComparingFieldByField(expectedProvider);
    }
}
