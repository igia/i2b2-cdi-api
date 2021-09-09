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



package io.igia.i2b2.cdi.derivedconcept.dao;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptQueryMasterDto;

@Repository
@Transactional(readOnly = true)
public class DerivedConceptQueryMasterDaoImpl implements DerivedConceptQueryMasterDao {
    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String GENERATED_SQL = "generatedSql";
    protected static final String CREATED_DATE = "createdDate";

    private final DerivedConceptQueryMasterMapper derivedConceptQueryMasterMapper;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DerivedConceptQueryMasterDaoImpl(DataSource dataSource,
	    DerivedConceptQueryMasterMapper derivedConceptQueryMasterMapper) {
	this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	this.derivedConceptQueryMasterMapper = derivedConceptQueryMasterMapper;
    }

    @Override
    public List<DerivedConceptQueryMasterDto> getQueryMaster(int fetchSize) {
	QueryParamDto queryParamDto = new QueryParamDto()
		.withQuery(String.join(" ", "SELECT TOP " + fetchSize + " qmaster.query_master_id AS " + ID,
			",qmaster.name AS " + NAME, ",qmaster.create_date AS " + CREATED_DATE,
			",qmaster.generated_sql AS " + GENERATED_SQL, "FROM qt_query_master qmaster "));
	orderByCreatedDate(queryParamDto);

	return this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(), derivedConceptQueryMasterMapper);
    }

    private void orderByCreatedDate(QueryParamDto queryParamDto) {
	queryParamDto.appendQuery("ORDER BY qmaster.create_date DESC");
    }

}
