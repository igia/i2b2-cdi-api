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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsFetchType;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsSearchDto;

@Repository
@Transactional(readOnly = true)
public class DerivedConceptJobDetailsDaoImpl implements DerivedConceptJobDetailsDao {

    protected static final String ID = "id";
    protected static final String DERIVED_CONCEPT_ID = "derivedConceptId";
    protected static final String ERROR_STACK = "errorStack";
    protected static final String DERIVED_CONCEPT_SQL = "derivedConceptSql";
    protected static final String STATUS = "status";
    protected static final String STARTED_ON = "startedOn";
    protected static final String COMPLETED_ON = "completedOn";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DerivedConceptJobDetailsMapper derivedConceptJobDetailsMapper;

    public DerivedConceptJobDetailsDaoImpl(DataSource dataSource,
	    DerivedConceptJobDetailsMapper derivedConceptJobDetailsMapper) {
	this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));
	this.derivedConceptJobDetailsMapper = derivedConceptJobDetailsMapper;
    }

    @Override
    public List<DerivedConceptJobDetailsDto> findDerivedConceptJobDetails(
	    DerivedConceptJobDetailsSearchDto derivedConceptJobDetailsSearchDto) {
	QueryParamDto queryParamDto = new QueryParamDto().withQuery(String.join(" ",
		"SELECT jobdetails.id as " + ID + ",", "jobdetails.derived_concept_id as " + DERIVED_CONCEPT_ID + ",",
		"jobdetails.error_stack as " + ERROR_STACK + ",",
		"jobdetails.derived_concept_sql as " + DERIVED_CONCEPT_SQL + ",",
		"jobdetails.status as " + STATUS + ",", "jobdetails.started_on as " + STARTED_ON + ",",
		"jobdetails.completed_on as " + COMPLETED_ON, "FROM derived_concept_job_details jobdetails"));

	addDerivedConceptIdCriteria(derivedConceptJobDetailsSearchDto, queryParamDto);
	addDerivedConceptJobDetailsFetchTypeCriteria(derivedConceptJobDetailsSearchDto, queryParamDto);

	return this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(), queryParamDto.getParameterSource(),
		derivedConceptJobDetailsMapper);
    }

    private void addDerivedConceptIdCriteria(DerivedConceptJobDetailsSearchDto derivedConceptJobDetailsSearchDto,
	    QueryParamDto queryParamDto) {
	List<Integer> derivedConceptIdList = derivedConceptJobDetailsSearchDto.getDerivedConceptIds();
	if (derivedConceptIdList != null && !derivedConceptIdList.isEmpty()) {
	    final String derivedConceptIds = "derivedConceptIds";
	    queryParamDto.addQueryCriteria("derived_concept_id in ( :" + derivedConceptIds + ")")
		    .addQueryParameter(derivedConceptIds, derivedConceptJobDetailsSearchDto.getDerivedConceptIds());
	}
    }

    private void addDerivedConceptJobDetailsFetchTypeCriteria(
	    DerivedConceptJobDetailsSearchDto derivedConceptJobDetailsSearchDto, QueryParamDto queryParamDto) {
	if (derivedConceptJobDetailsSearchDto.getDerivedConceptJobDetailsFetchType() != null
		&& derivedConceptJobDetailsSearchDto.getDerivedConceptJobDetailsFetchType()
			.equals(DerivedConceptJobDetailsFetchType.LATEST)) {
	    queryParamDto.addQueryCriteria(
		    "id in ( select max(id) from derived_concept_job_details group by derived_concept_id )");
	}
    }

    @Override
    public int[] createDerivedConceptJobDetails(List<DerivedConceptJobDetailsDto> jobDetails) {

        QueryParamDto queryParamDto = new QueryParamDto().withQuery(String.join(" ",
                "INSERT INTO derived_concept_job_details (derived_concept_id, error_stack, derived_concept_sql, status)",
                "VALUES (?, ?, ?, ?)"));

        return namedParameterJdbcTemplate.getJdbcTemplate().batchUpdate(queryParamDto.getQuery(),
                new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, jobDetails.get(i).getDerivedConceptId());
                        ps.setString(2, jobDetails.get(i).getErrorStack());
                        ps.setString(3, "");
                        ps.setString(4, String.valueOf(jobDetails.get(i).getStatus()));
                    }

                    public int getBatchSize() {
                        return jobDetails.size();
                    }
                });
    }
}
