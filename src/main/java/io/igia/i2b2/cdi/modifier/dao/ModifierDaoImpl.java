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



package io.igia.i2b2.cdi.modifier.dao;

import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.modifier.dto.ModifierDto;
import io.igia.i2b2.cdi.modifier.dto.ModifierSearchDto;
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
public class ModifierDaoImpl implements ModifierDao {

    protected static final String MODIFIER_CODE = "modifierCode";
    protected static final String MODIFIER_NAME = "modifierName";
    protected static final String MODIFIER_PATH = "modifierPath";
    protected static final String MODIFIER_SOURCE = "modifierSource";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ModifierMapper modifierMapper;

    public ModifierDaoImpl(DataSource dataSource, ModifierMapper modifierMapper) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));
        this.modifierMapper = modifierMapper;
    }

    @Override
    public List<ModifierDto> findModifiers(final ModifierSearchDto modifierSearchDto) {

        QueryParamDto queryParamDto = new QueryParamDto()
            .withQuery(String.join(" ",
                "select modifier.modifier_cd as " + MODIFIER_CODE + ",",
                "modifier.modifier_path as " + MODIFIER_PATH + ",",
                "modifier.name_char as " + MODIFIER_NAME + ",",
                "modifier.sourcesystem_cd as " + MODIFIER_SOURCE,
                "from modifier_dimension modifier"
            ));

        addSourceCriteria(modifierSearchDto, queryParamDto);
        addModifierPathCriteria(modifierSearchDto, queryParamDto);
        addModifierCodeCriteria(modifierSearchDto, queryParamDto);

        return this.namedParameterJdbcTemplate.query(
            queryParamDto.getQuery(), queryParamDto.getParameterSource(), modifierMapper);
    }

    private void addSourceCriteria(ModifierSearchDto modifierSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(modifierSearchDto.getSource())) {
            final String sourceQueryParamName = "sourceSystem";
            queryParamDto
                .addQueryCriteria("UPPER(modifier.sourcesystem_cd) = :" + sourceQueryParamName)
                .addQueryParameter(sourceQueryParamName, modifierSearchDto.getSource().toUpperCase(Locale.ENGLISH));
        }
    }

    private void addModifierPathCriteria(ModifierSearchDto modifierSearchDto, QueryParamDto queryParamDto) {
        if (!modifierSearchDto.getModifierPaths().isEmpty()) {
            final String modifierPathsQueryParamName = "modifierPaths";
            queryParamDto
                .addQueryCriteria("modifier.modifier_path in (:" + modifierPathsQueryParamName + ")")
                .addQueryParameter(modifierPathsQueryParamName, modifierSearchDto.getModifierPaths());
        }
    }

    private void addModifierCodeCriteria(ModifierSearchDto modifierSearchDto, QueryParamDto queryParamDto) {
        if (!modifierSearchDto.getModifierCodes().isEmpty()) {
            final String modifierCodeQueryParamName = "modifierCodes";
            queryParamDto
                .addQueryCriteria("modifier.modifier_cd in (:" + modifierCodeQueryParamName + ")")
                .addQueryParameter(modifierCodeQueryParamName, modifierSearchDto.getModifierCodes());
        }
    }
}
