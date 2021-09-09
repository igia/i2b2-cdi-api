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

import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.provider.dto.ProviderDto;
import io.igia.i2b2.cdi.provider.dto.ProviderSearchDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.util.List;
import java.util.Locale;

@Repository
@Transactional(readOnly = true)
public class ProviderDaoImpl implements ProviderDao {

    protected static final String PROVIDER_ID = "providerId";
    protected static final String PROVIDER_NAME = "providerName";
    protected static final String PROVIDER_PATH = "providerPath";
    protected static final String PROVIDER_SOURCE = "providerSource";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ProviderMapper providerMapper;

    public ProviderDaoImpl(DataSource dataSource, ProviderMapper providerMapper) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));
        this.providerMapper = providerMapper;
    }

    @Override
    public List<ProviderDto> findProviders(final ProviderSearchDto providerSearchDto) {

        QueryParamDto queryParamDto = new QueryParamDto()
            .withQuery(String.join(" ",
                "select provider.provider_id as " + PROVIDER_ID + ",",
                "provider.provider_path as " + PROVIDER_PATH + ",",
                "provider.name_char as " + PROVIDER_NAME + ",",
                "provider.sourcesystem_cd as " + PROVIDER_SOURCE,
                "from provider_dimension provider"
            ));

        addSourceCriteria(providerSearchDto, queryParamDto);
        addProviderIdCriteria(providerSearchDto, queryParamDto);

        return this.namedParameterJdbcTemplate.query(
            queryParamDto.getQuery(), queryParamDto.getParameterSource(), providerMapper);
    }

    private void addProviderIdCriteria(ProviderSearchDto providerSearchDto, QueryParamDto queryParamDto) {

        if (!StringUtils.isEmpty(providerSearchDto.getProviderId())) {
            final String providerIdQueryParamName = "providerIdParam";
            queryParamDto
                .addQueryCriteria("UPPER(provider.provider_id) = :" + providerIdQueryParamName)
                .addQueryParameter(providerIdQueryParamName, providerSearchDto.getProviderId().toUpperCase(Locale.ENGLISH));
        }
    }

    private void addSourceCriteria(ProviderSearchDto providerSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(providerSearchDto.getSource())) {
            final String sourceQueryParamName = "sourceSystem";
            queryParamDto
                .addQueryCriteria("UPPER(provider.sourcesystem_cd) = :" + sourceQueryParamName)
                .addQueryParameter(sourceQueryParamName, providerSearchDto.getSource().toUpperCase(Locale.ENGLISH));
        }
    }
}
