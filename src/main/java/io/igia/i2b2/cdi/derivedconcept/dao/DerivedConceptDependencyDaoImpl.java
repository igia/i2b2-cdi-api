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
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.common.exception.I2B2DataNotFoundException;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencyDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencySearchDto;

@Repository
@Transactional(readOnly = true)
public class DerivedConceptDependencyDaoImpl implements DerivedConceptDependencyDao {
    protected static final String ID = "id";
    protected static final String DERIVED_CONCEPT_ID = "derivedConceptId";
    protected static final String PARENT_CONCEPT_PATH = "parentConceptPath";
    protected static final String DERIVED_CONCEPT_PATH = "derivedConceptPath";

    private static final Logger log = LoggerFactory.getLogger(DerivedConceptDependencyDaoImpl.class);
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final DerivedConceptDependencyMapper derivedConceptDependencyMapper;

    public DerivedConceptDependencyDaoImpl(DataSource dataSource,
	    DerivedConceptDependencyMapper derivedConceptDependencyMapper) {
	this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));
	this.derivedConceptDependencyMapper = derivedConceptDependencyMapper;
    }

    @Override
    @Transactional(readOnly = false)
    public int[] addDerivedConceptDependency(List<DerivedConceptDependencyDto> derivedConceptDependencies) {
	int[] affectedRows = null;
	try {
	    final String query = String.join(" ", "insert into derived_concept_dependency",
		    "(derived_concept_id, parent_concept_path) values",
		    "( :" + DERIVED_CONCEPT_ID + ", :" + PARENT_CONCEPT_PATH + ")");

	    affectedRows = namedParameterJdbcTemplate.batchUpdate(query,
		    getSqlParameterSources(derivedConceptDependencies));
	} catch (DataIntegrityViolationException e) {
	    log.error(e.getMessage(), e.getCause());
	    throw new I2B2DataNotFoundException("Dependency does not exist");
	}
	return affectedRows;
    }

    @Override
    public List<DerivedConceptDependencyDto> getAllDerivedConceptDependencies() {
	final String query = String.join(" ",
		"SELECT dependency.id as " + ID + ", dependency.derived_concept_id as " + DERIVED_CONCEPT_ID,
		", dependency.parent_concept_path as " + PARENT_CONCEPT_PATH + ", derivedconcept.concept_path as "
			+ DERIVED_CONCEPT_PATH,
		"FROM derived_concept_dependency dependency INNER JOIN derived_concept_definition derivedconcept",
		"ON dependency.derived_concept_id = derivedconcept.id");

	return this.namedParameterJdbcTemplate.query(query, this.derivedConceptDependencyMapper);
    }

    private SqlParameterSource[] getSqlParameterSources(List<DerivedConceptDependencyDto> derivedConceptDependencies) {
	SqlParameterSource[] sqlParameterSources = new MapSqlParameterSource[derivedConceptDependencies.size()];
	MapSqlParameterSource mapSqlParameterSource = null;
	DerivedConceptDependencyDto derivedConceptDependencyDto = null;
	for (int index = 0; index < derivedConceptDependencies.size(); index++) {
	    mapSqlParameterSource = new MapSqlParameterSource();
	    derivedConceptDependencyDto = derivedConceptDependencies.get(index);
	    mapSqlParameterSource.addValue(DERIVED_CONCEPT_ID, derivedConceptDependencyDto.getDerivedConceptId());
	    mapSqlParameterSource.addValue(PARENT_CONCEPT_PATH, derivedConceptDependencyDto.getParentConceptPath());
	    sqlParameterSources[index] = mapSqlParameterSource;
	}
	return sqlParameterSources;
    }

    @Override
    public List<DerivedConceptDependencyDto> getDerivedConceptDependency(
	    DerivedConceptDependencySearchDto derivedConceptDependencySearchDto) {
	QueryParamDto queryParamDto = new QueryParamDto();
	queryParamDto.withQuery(String.join(" ",
		"SELECT dependency.id as " + ID + ", dependency.derived_concept_id as " + DERIVED_CONCEPT_ID,
		", dependency.parent_concept_path as " + PARENT_CONCEPT_PATH + ", derivedconcept.concept_path as "
			+ DERIVED_CONCEPT_PATH,
		"FROM derived_concept_dependency dependency", "INNER JOIN derived_concept_definition derivedconcept",
		"ON dependency.derived_concept_id = derivedconcept.id"));

	addDerivedConceptIdCriteria(derivedConceptDependencySearchDto, queryParamDto);
	addConceptPathsCriteria(derivedConceptDependencySearchDto, queryParamDto);
	return this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(), queryParamDto.getParameterSource(),
		this.derivedConceptDependencyMapper);
    }

    private void addDerivedConceptIdCriteria(DerivedConceptDependencySearchDto derivedConceptDependencySearchDto,
	    QueryParamDto queryParamDto) {
	if (derivedConceptDependencySearchDto.getDerivedConceptId() != null) {
	    queryParamDto.addQueryCriteria("derived_concept_id = :" + DERIVED_CONCEPT_ID);
	    queryParamDto.addQueryParameter(DERIVED_CONCEPT_ID,
		    derivedConceptDependencySearchDto.getDerivedConceptId());
	}
    }

    private void addConceptPathsCriteria(DerivedConceptDependencySearchDto derivedConceptDependencySearchDto,
	    QueryParamDto queryParamDto) {
	final Set<String> parentConceptPaths = derivedConceptDependencySearchDto.getConceptPaths();
	if (parentConceptPaths != null && !parentConceptPaths.isEmpty()) {
	    queryParamDto.addQueryCriteria("parent_concept_path in (:" + PARENT_CONCEPT_PATH + ") OR concept_path in (:"
		    + DERIVED_CONCEPT_PATH + ")");
	    queryParamDto.addQueryParameter(PARENT_CONCEPT_PATH, parentConceptPaths);
	    queryParamDto.addQueryParameter(DERIVED_CONCEPT_PATH, parentConceptPaths);

	}
    }

    @Override
    @Transactional(readOnly = false)
    public int deleteDerivedConceptDependencies(DerivedConceptDependencySearchDto derivedConceptDependencySearchDto) {
	QueryParamDto queryParamDto = new QueryParamDto();
	queryParamDto.withQuery("DELETE FROM derived_concept_dependency");
	addDerivedConceptIdCriteria(derivedConceptDependencySearchDto, queryParamDto);
	addParentConceptPathsCriteria(derivedConceptDependencySearchDto, queryParamDto);

	return this.namedParameterJdbcTemplate.update(queryParamDto.getQuery(), queryParamDto.getParameterSource());
    }

    private void addParentConceptPathsCriteria(DerivedConceptDependencySearchDto derivedConceptDependencySearchDto,
	    QueryParamDto queryParamDto) {
	final List<String> parentConceptPaths = derivedConceptDependencySearchDto.getParentConceptPaths();
	if (parentConceptPaths != null && !parentConceptPaths.isEmpty()) {
	    queryParamDto.addQueryCriteria("parent_concept_path in (:" + PARENT_CONCEPT_PATH + ")");
	    queryParamDto.addQueryParameter(PARENT_CONCEPT_PATH, parentConceptPaths);
	}
    }
}
