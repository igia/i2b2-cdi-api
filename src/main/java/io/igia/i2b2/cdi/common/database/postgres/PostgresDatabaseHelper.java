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

package io.igia.i2b2.cdi.common.database.postgres;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.igia.i2b2.cdi.common.database.DatabaseHelper;
import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationQueryParamName;
import io.igia.i2b2.cdi.common.dto.QueryParamDto;

@Component
@Conditional(PostgresDatabaseCondition.class)
public class PostgresDatabaseHelper implements DatabaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostgresDatabaseHelper.class);

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PostgresDatabaseHelper(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void createSequenceIfNotExists(String sequenceName, int startsWith, int incrementBy) {
        try {
            LOGGER.debug("check if sequence already exists: {}", sequenceName);
            namedParameterJdbcTemplate.getJdbcTemplate().execute(
                "select 1 from pg_class where relkind = 'S' and oid = ('" + sequenceName + "')::regclass");
            LOGGER.debug("Sequence found. {}", sequenceName);
        }  catch (Exception e) {
            LOGGER.info("Creating sequence: {}", sequenceName);
            String encounterNumSequence = String.join(" ",
                "CREATE SEQUENCE " + sequenceName,
                "INCREMENT " + incrementBy,
                "START " + startsWith,
                "CACHE 1");
            namedParameterJdbcTemplate.getJdbcTemplate().execute(encounterNumSequence);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Integer getNextSequenceValue(String sequenceName) {
        return this.namedParameterJdbcTemplate.queryForObject(
            "select nextval('" + sequenceName + "')",
            new MapSqlParameterSource(), Integer.class);
    }

    @Override
    public void addPaginationCriteria(QueryParamDto queryParamDto, PageableDto pageableDto) {

        queryParamDto.appendQuery(
                " ORDER BY " + pageableDto.getSortBy() + " " + pageableDto.getSortOrder() + 
                " LIMIT :" + PaginationQueryParamName.LIMIT + 
                " OFFSET :" + PaginationQueryParamName.OFFSET);
    }
}
