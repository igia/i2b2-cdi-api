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

import java.sql.Timestamp;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.igia.i2b2.cdi.common.cache.RequestCache;
import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.common.exception.I2b2DatabaseException;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;

@Repository
@Transactional(readOnly = true)
public class DerivedConceptDaoImpl implements DerivedConceptDao {
    private static final Logger log = LoggerFactory.getLogger(DerivedConceptDaoImpl.class);
    protected static final String ID = "id";
    protected static final String CONCEPT_PATH = "conceptPath";
    protected static final String CONCEPT_CODE = "conceptCode";
    protected static final String DERIVED_CONCEPT_DESCRIPTION = "description";
    protected static final String DERIVED_FACT_QUERY = "derivedFactQuery";
    protected static final String UNIT_CD = "unit";
    protected static final String UPDATED_ON = "updatedOn";
    protected static final String PATH_LIST = "pathList";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DerivedConceptMapper derivedConceptMapper;

    public DerivedConceptDaoImpl(DataSource dataSource, DerivedConceptMapper derivedConceptMapper) {
	this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));
	this.derivedConceptMapper = derivedConceptMapper;
    }

    @Override
    public DerivedConceptDto findDerivedConceptById(Integer id) {
        DerivedConceptDto derivedConceptDto = null;
        QueryParamDto queryParamDto = new QueryParamDto().withQuery(String.join(" ",
                "select derivedconcept.id as " + ID + ",", "concept.concept_cd as " + CONCEPT_CODE + ",",
                "derivedconcept.description as " + DERIVED_CONCEPT_DESCRIPTION + ",",
                "derivedconcept.sql_query as " + DERIVED_FACT_QUERY + ",", "derivedconcept.unit_cd as " + UNIT_CD + ",",
                "derivedconcept.update_date as " + UPDATED_ON + ",", "concept.concept_path as " + CONCEPT_PATH + " ",
                "from derived_concept_definition derivedconcept " + "INNER JOIN concept_dimension concept ",
                "ON derivedconcept.concept_path = concept.concept_path"));

        addDerivedConceptIdCriteria(id, queryParamDto);
        List<DerivedConceptDto> derivedConcepts = this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(),
                queryParamDto.getParameterSource(), derivedConceptMapper);

        if (!derivedConcepts.isEmpty()) {
            derivedConceptDto = derivedConcepts.get(0);
        }
        return derivedConceptDto;
    }

    private void addDerivedConceptIdCriteria(Integer id, QueryParamDto queryParamDto) {
	if (id != null) {
	    final String conceptId = "conceptId";
	    queryParamDto.addQueryCriteria("derivedconcept.id = :" + conceptId).addQueryParameter(conceptId, id);
	}
    }

    @Override
    @RequestCache
    public List<DerivedConceptDto> findDerivedConcepts() {

	QueryParamDto queryParamDto = new QueryParamDto().withQuery(String.join(" ",
		"select derivedconcept.id as " + ID + ",", "concept.concept_cd as " + CONCEPT_CODE + ",",
		"derivedconcept.description as " + DERIVED_CONCEPT_DESCRIPTION + ",",
		"derivedconcept.sql_query as " + DERIVED_FACT_QUERY + ",", "derivedconcept.unit_cd as " + UNIT_CD + ",",
		"derivedconcept.update_date as " + UPDATED_ON + ",", "concept.concept_path as " + CONCEPT_PATH + " ",
		"from derived_concept_definition derivedconcept " + "INNER JOIN concept_dimension concept ",
		"ON derivedconcept.concept_path = concept.concept_path"));
	return this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(), queryParamDto.getParameterSource(),
		derivedConceptMapper);
    }

    @Override
    @Transactional(readOnly = false)
    public int addDerivedConcept(DerivedConceptDto derivedConcept) {
	final String query = String.join(" ", "insert into derived_concept_definition (",
		"concept_path, sql_query, unit_cd, description, update_date) ", "values (",
		":" + CONCEPT_PATH + ", :" + DERIVED_FACT_QUERY + ", :" + UNIT_CD + ", :" + DERIVED_CONCEPT_DESCRIPTION
			+ ", :" + UPDATED_ON + ")");

	MapSqlParameterSource param = new MapSqlParameterSource();
	param.addValue(CONCEPT_PATH, derivedConcept.getPath());
	param.addValue(DERIVED_FACT_QUERY, derivedConcept.getFactQuery());
	param.addValue(UNIT_CD, derivedConcept.getUnit());
	param.addValue(DERIVED_CONCEPT_DESCRIPTION, derivedConcept.getDescription());
	param.addValue(UPDATED_ON, Timestamp.from(derivedConcept.getUpdatedOn()));

	KeyHolder keyHolder = new GeneratedKeyHolder();

	int id = 0;
	try {
	    namedParameterJdbcTemplate.update(query, param, keyHolder, new String[] { "id" });
	    if (keyHolder.getKey() != null)
		id = keyHolder.getKey().intValue();
	} catch (Exception e) {
	    log.error(e.getMessage());
	    throw new I2b2DatabaseException("Derived concept could not be added.");
	}

	return id;
    }

    @Override
    @Transactional(readOnly = false)
    public int updateDerivedConcept(DerivedConceptDto derivedConcept) {
	final String updateQuery = String.join(" ", "update derived_concept_definition set",
		" concept_path = :" + CONCEPT_PATH, ", sql_query = :" + DERIVED_FACT_QUERY, ", unit_cd = :" + UNIT_CD,
		", description = :" + DERIVED_CONCEPT_DESCRIPTION, ", update_date = :" + UPDATED_ON,
		"where id = :" + ID);

	MapSqlParameterSource param = new MapSqlParameterSource();
	param.addValue(CONCEPT_PATH, derivedConcept.getPath());
	param.addValue(DERIVED_FACT_QUERY, derivedConcept.getFactQuery());
	param.addValue(UNIT_CD, derivedConcept.getUnit());
	param.addValue(DERIVED_CONCEPT_DESCRIPTION, derivedConcept.getDescription());
	param.addValue(UPDATED_ON, Timestamp.from(derivedConcept.getUpdatedOn()));
	param.addValue(ID, derivedConcept.getId());

	return namedParameterJdbcTemplate.update(updateQuery, param);
    }

    @Override
    @Transactional(readOnly = false)
    public int deleteDerivedConcept(DerivedConceptDto derivedConcept) {
	final String deleteQuery = String.join(" ", "delete from derived_concept_definition ", "where id = :" + ID);

	MapSqlParameterSource param = new MapSqlParameterSource();
	param.addValue(ID, derivedConcept.getId());
	return namedParameterJdbcTemplate.update(deleteQuery, param);
    }

    @Override
    public List<DerivedConceptDto> findDerivedConceptsByPaths(List<String> pathList) {
        QueryParamDto queryParamDto = new QueryParamDto().withQuery(String.join(" ",
                "select derivedconcept.id as " + ID + ",", "concept.concept_cd as " + CONCEPT_CODE + ",",
                "derivedconcept.description as " + DERIVED_CONCEPT_DESCRIPTION + ",",
                "derivedconcept.sql_query as " + DERIVED_FACT_QUERY + ",", "derivedconcept.unit_cd as " + UNIT_CD + ",",
                "derivedconcept.update_date as " + UPDATED_ON + ",", "concept.concept_path as " + CONCEPT_PATH + " ",
                "from derived_concept_definition derivedconcept " + "INNER JOIN concept_dimension concept ",
                "ON derivedconcept.concept_path = concept.concept_path"));

        queryParamDto.addQueryCriteria("derivedconcept.concept_path in (:" + PATH_LIST + ")");
        queryParamDto.addQueryParameter(PATH_LIST, pathList);
        return this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(), queryParamDto.getParameterSource(),
                derivedConceptMapper);
    }
}
