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

package io.igia.i2b2.cdi.concept.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.common.cache.RequestCache;
import io.igia.i2b2.cdi.common.database.DatabaseHelper;
import io.igia.i2b2.cdi.common.dto.Operator;
import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationQueryParamName;
import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.dto.ConceptSearchDto;
import io.igia.i2b2.cdi.concept.dto.PathFilterDto;

@Repository
@Transactional(readOnly = true)
public class ConceptDaoImpl implements ConceptDao {

    protected static final String CONCEPT_CODE = "conceptCode";
    protected static final String CONCEPT_NAME = "conceptName";
    protected static final String CONCEPT_PATH = "conceptPath";
    protected static final String CONCEPT_SOURCE = "conceptSource";
    protected static final String UPDATE_DATE = "updateDate";
    protected static final String EXISTING_CONCEPT_PATH = "existingConceptPath";
    protected static final String WILDCARD_CHARACTER = "%";
    protected static final String SQL_OPERATOR_LIKE = "LIKE";
    protected static final String SQL_OPERATOR_EQUAL = "=";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ConceptMapper conceptMapper;
    private final DatabaseHelper databaseHelper;

    public ConceptDaoImpl(DataSource dataSource, ConceptMapper conceptMapper, DatabaseHelper databaseHelper) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));
        this.conceptMapper = conceptMapper;
        this.databaseHelper = databaseHelper;
    }
    
    @Override
    @RequestCache
    public PaginationResult<ConceptDto> findConcepts(final ConceptSearchDto conceptSearchDto) {

        QueryParamDto queryParamDto = new QueryParamDto()
                .withQuery(String.join(" ", "select concept.concept_cd as " + CONCEPT_CODE + ",",
                        "concept.concept_path as " + CONCEPT_PATH + ",", "concept.name_char as " + CONCEPT_NAME + ",",
                        "concept.sourcesystem_cd as " + CONCEPT_SOURCE, "from concept_dimension concept"));

        addSourceCriteria(conceptSearchDto, queryParamDto);
        if (!conceptSearchDto.getConceptPaths().isEmpty()) {
            addConceptPathsCriteria(conceptSearchDto, queryParamDto);
        } else {
            addConceptPathCriteria(conceptSearchDto, queryParamDto);
        }
        addConceptCodeCriteria(conceptSearchDto, queryParamDto);
        addPaginationCriteria(queryParamDto, conceptSearchDto.getPageableDto());

        List<ConceptDto> concepts = this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(),
                queryParamDto.getParameterSource(), conceptMapper);
        return new PaginationResult<>(concepts, 0);
    }

    private void addPaginationCriteria(QueryParamDto queryParamDto, PageableDto pageableDto) {
        if (pageableDto != null) {
            int limit = pageableDto.getSize();
            int offset = queryParamDto.calculateOffset(pageableDto.getPage(), pageableDto.getSize());

            databaseHelper.addPaginationCriteria(queryParamDto, pageableDto);

            queryParamDto.addQueryParameter(PaginationQueryParamName.LIMIT, limit);
            queryParamDto.addQueryParameter(PaginationQueryParamName.OFFSET, offset);
        }        
    }

    private void addConceptPathCriteria(ConceptSearchDto conceptSearchDto, QueryParamDto queryParamDto) {
        PathFilterDto pathFilterDto = conceptSearchDto.getPathFilterDto();
        if (pathFilterDto != null && !StringUtils.isEmpty(pathFilterDto.getPath())
                && !StringUtils.isEmpty(pathFilterDto.getOpertaor())) {
            evaluateConceptPathCriteria(pathFilterDto, queryParamDto);
        }
    }
    
    private void addConceptPathsCriteria(ConceptSearchDto conceptSearchDto, QueryParamDto queryParamDto) {
        if (!conceptSearchDto.getConceptPaths().isEmpty()) {
            queryParamDto.addQueryCriteria(" concept.concept_path in" + " (:" + CONCEPT_PATH + ")");
            queryParamDto.addQueryParameter(CONCEPT_PATH, conceptSearchDto.getConceptPaths());
        }
    }

    private void evaluateConceptPathCriteria(PathFilterDto pathFilterDto, QueryParamDto queryParamDto) {
        final String conceptPathQueryParamName = CONCEPT_PATH;
        final Operator operator = pathFilterDto.getOpertaor();
        String conceptPathQueryParamValue = "";
        String sqlOperator = "";

        switch (operator) {
        case CONTAINS:
            sqlOperator = SQL_OPERATOR_LIKE;
            conceptPathQueryParamValue = WILDCARD_CHARACTER + pathFilterDto.getPath() + WILDCARD_CHARACTER;
            break;
        case EQUAL:
            sqlOperator = SQL_OPERATOR_EQUAL;
            conceptPathQueryParamValue = pathFilterDto.getPath();
            break;
        case STARTSWITH:
            sqlOperator = SQL_OPERATOR_LIKE;
            conceptPathQueryParamValue = pathFilterDto.getPath() + WILDCARD_CHARACTER;
            break;
        case ENDSWITH:
            sqlOperator = SQL_OPERATOR_LIKE;
            conceptPathQueryParamValue = WILDCARD_CHARACTER + pathFilterDto.getPath();
            break;
        }
        queryParamDto.addQueryCriteria(" UPPER(concept.concept_path) " + sqlOperator + " :" + conceptPathQueryParamName);
        queryParamDto.addQueryParameter(conceptPathQueryParamName,
                conceptPathQueryParamValue.toUpperCase(Locale.ENGLISH));
    }

    private void addConceptCodeCriteria(ConceptSearchDto conceptSearchDto, QueryParamDto queryParamDto) {

	if (!StringUtils.isEmpty(conceptSearchDto.getCode())) {
	    final String conceptCodeQueryParamName = CONCEPT_CODE;
	    queryParamDto.addQueryCriteria("UPPER(concept.concept_cd) = :" + conceptCodeQueryParamName)
		    .addQueryParameter(conceptCodeQueryParamName,
			    conceptSearchDto.getCode().toUpperCase(Locale.ENGLISH));
	}
    }

    private void addSourceCriteria(ConceptSearchDto conceptSearchDto, QueryParamDto queryParamDto) {
	if (!StringUtils.isEmpty(conceptSearchDto.getSource())) {
	    final String sourceQueryParamName = "sourceSystem";
	    queryParamDto.addQueryCriteria("UPPER(concept.sourcesystem_cd) = :" + sourceQueryParamName)
		    .addQueryParameter(sourceQueryParamName, conceptSearchDto.getSource().toUpperCase(Locale.ENGLISH));
	}
    }

    @Override
    @Transactional(readOnly = false)
    public int addConcept(ConceptDto concept) {
	final String sql = String.join(" ", "INSERT INTO concept_dimension (",
		"concept_path, concept_cd, name_char, update_date, sourcesystem_cd) VALUES (",
		":" + CONCEPT_PATH + ", :" + CONCEPT_CODE + ", :" + CONCEPT_NAME + ", :" + UPDATE_DATE + ", :"
			+ CONCEPT_SOURCE + ")");

	MapSqlParameterSource param = new MapSqlParameterSource();
	param.addValue(CONCEPT_PATH, concept.getConceptPath());
	param.addValue(CONCEPT_CODE, concept.getCode());
	param.addValue(CONCEPT_NAME, concept.getName());
	param.addValue(UPDATE_DATE, LocalDateTime.now());
	param.addValue(CONCEPT_SOURCE, concept.getSource());

	return namedParameterJdbcTemplate.update(sql, param);
    }

    @Override
    @Transactional(readOnly = false)
    public int updateConcept(ConceptDto concept, String existingConceptPath) {
	final String updateQuery = String.join(" ", "update concept_dimension set", " concept_cd = :" + CONCEPT_CODE,
		", name_char = :" + CONCEPT_NAME, ", sourcesystem_cd = :" + CONCEPT_SOURCE,
		", update_date = :" + UPDATE_DATE, ", concept_path = :" + CONCEPT_PATH,
		" where concept_path = :" + EXISTING_CONCEPT_PATH);

	MapSqlParameterSource param = new MapSqlParameterSource();
	param.addValue(CONCEPT_PATH, concept.getConceptPath());
	param.addValue(CONCEPT_CODE, concept.getCode());
	param.addValue(CONCEPT_NAME, concept.getName());
	param.addValue(UPDATE_DATE, LocalDateTime.now());
	param.addValue(CONCEPT_SOURCE, concept.getSource());
	param.addValue(EXISTING_CONCEPT_PATH, existingConceptPath);

	return namedParameterJdbcTemplate.update(updateQuery, param);
    }

    @Override
    @Transactional(readOnly = false)
    public int deleteConcept(ConceptDto concept) {
	final String query = String.join(" ", "delete from concept_dimension",
		" where concept_path = :" + CONCEPT_PATH);

	MapSqlParameterSource param = new MapSqlParameterSource();
	param.addValue(CONCEPT_PATH, concept.getConceptPath());

	return namedParameterJdbcTemplate.update(query, param);
    }

    @Override
    public int getTotalCount(ConceptSearchDto conceptSearchDto) {
        int count = 0;
        QueryParamDto countQueryParamDto = new QueryParamDto();
        countQueryParamDto.withQuery(String.join(" ", "select count(concept_path) from concept_dimension concept"));
        addSourceCriteria(conceptSearchDto, countQueryParamDto);
        addConceptCodeCriteria(conceptSearchDto, countQueryParamDto);
        if (!conceptSearchDto.getConceptPaths().isEmpty()) {
            addConceptPathsCriteria(conceptSearchDto, countQueryParamDto);
        } else {
            addConceptPathCriteria(conceptSearchDto, countQueryParamDto);
        }
        count = this.namedParameterJdbcTemplate.queryForObject(countQueryParamDto.getQuery(),
                countQueryParamDto.getParameterSource(), Integer.class);

        return count;
    }
}
