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

package io.igia.i2b2.cdi.common.database.mssql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.igia.i2b2.cdi.common.database.DatabaseHelper;
import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationQueryParamName;
import io.igia.i2b2.cdi.common.dto.QueryParamDto;

@Component
@Conditional(MssqlDatabaseCondition.class)
public class MssqlDatabaseHelper implements DatabaseHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(MssqlDatabaseHelper.class);

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public MssqlDatabaseHelper(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void createSequenceIfNotExists(String sequenceName, int startsWith, int incrementBy) {
        try {
            QueryParamDto queryParamDto = new QueryParamDto().withQuery("select count(name) from sys.sequences where object_id = object_id(':sequenceName')")
                .addQueryParameter("sequenceName", sequenceName);

            LOGGER.debug("check if sequence already exists: {}", sequenceName);
            Integer sequenceCount = namedParameterJdbcTemplate.queryForObject(
                queryParamDto.getQuery(),
                queryParamDto.getParameterSource(),
                Integer.class);
            if(sequenceCount == 0) {
                createSequence(namedParameterJdbcTemplate.getJdbcTemplate(), sequenceName, startsWith, incrementBy);
            } else {
                LOGGER.debug("Sequence already exists. {}", sequenceName);
            }
        } catch (Exception e) {
            LOGGER.debug("Sequence already exists. {}", sequenceName);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Integer getNextSequenceValue(String sequenceName) {
        return this.namedParameterJdbcTemplate.queryForObject(
            "SELECT NEXT VALUE FOR " + sequenceName,
            new MapSqlParameterSource(), Integer.class);
    }

    private void createSequence(JdbcTemplate jdbcTemplate, String sequenceName, int startsWith, int incrementBy) {
        LOGGER.info("Creating sequence: {}", sequenceName);
        String encounterNumSequence = String.join(" ",
            "CREATE SEQUENCE " + sequenceName,
            "INCREMENT BY " + incrementBy,
            "START WITH " + startsWith,
            "CACHE 1");
        jdbcTemplate.execute(encounterNumSequence);
    }

    @Override
    public void addPaginationCriteria(QueryParamDto queryParamDto, PageableDto pageableDto) {

        queryParamDto.appendQuery(" ORDER BY " + pageableDto.getSortBy() + " " + pageableDto.getSortOrder() 
                + " OFFSET :" + PaginationQueryParamName.OFFSET 
                + " ROWS FETCH NEXT :" + PaginationQueryParamName.LIMIT + " ROWS ONLY");
    }
}
