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



package io.igia.i2b2.cdi.ontology.dao;

import java.time.LocalDateTime;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyDto;

@Repository
@Transactional(readOnly = true, transactionManager = "i2b2OntologyTransactionManager")
public class OntologyConceptDaoImpl implements OntologyConceptDao {

    protected static final String CONCEPT_FULL_NAME = "conceptFullName";
    protected static final String METADATA_XML = "metadataXml";
    private static final String MODIFIER_APPLIED_PATH = "modifierAppliedPath";
    protected static final String WILDCARD_CHARACTER = "%";

    protected static final String CH_LEVEL = "chLevel";
    protected static final String C_NAME = "cName";
    protected static final String C_SYNONYM_CD = "cSynonymCd";
    protected static final String C_VISUAL_ATTRIBUTES = "cVisualAttributes";
    protected static final String C_FACT_TABLE_COLUMN = "cFactTableColumn";
    protected static final String C_TABLE_NAME = "cTableName";
    protected static final String C_COLUMN_NAME = "cColumnName";
    protected static final String C_COLUMN_DATATYPE = "cColumnDatatype";
    protected static final String C_OPERATOR = "cOperator";
    protected static final String C_DIMCODE = "cDimcode";
    protected static final String C_TOOLTIP = "cTooltip";
    protected static final String M_APPLIED_PATH = "mAppliedPath";
    protected static final String UPDATE_DATE = "updateDate";
    protected static final String SOURCE_SYSTEM_CD = "sourceSystemCd";
    protected static final String C_TABLE_CD = "cTableAccess";
    protected static final String ACCESS_TABLE_NAME = "accessTableName";
    protected static final String C_PROTECTED_ACCESS = "cProtectedAccess";
    protected static final String EXISTING_CONCEPT_FULL_NAME = "existingConceptFullName";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final OntologyConceptMapper ontologyConceptMapper;

    public OntologyConceptDaoImpl(@Qualifier("ontology") DataSource dataSource,
	    OntologyConceptMapper ontologyConceptMapper) {
	this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));
	this.ontologyConceptMapper = ontologyConceptMapper;
    }

    @Override
    public List<OntologyConceptDto> findOntologyConcepts(OntologyConceptSearchDto ontologyConceptSearchDto) {
	QueryParamDto queryParamDto = new QueryParamDto()
		.withQuery(String.join(" ", "select distinct ontology.c_fullname as " + CONCEPT_FULL_NAME,
			", ontology.c_metadataxml as " + METADATA_XML, "from i2b2 ontology"));

	addConceptPathCriteria(ontologyConceptSearchDto, queryParamDto);
	addModifierAppliedPathCriteria(ontologyConceptSearchDto, queryParamDto);
	addModifierExclusionCriteria(ontologyConceptSearchDto, queryParamDto);
	addConceptCriteria(ontologyConceptSearchDto, queryParamDto);

	String orderClause = " order by ontology.c_fullname";

	return this.namedParameterJdbcTemplate.query(queryParamDto.getQuery() + orderClause,
		queryParamDto.getParameterSource(), ontologyConceptMapper);
    }

    private void addConceptCriteria(OntologyConceptSearchDto ontologyConceptSearchDto, QueryParamDto queryParamDto) {
	if (!StringUtils.isEmpty(ontologyConceptSearchDto.getConceptPath())) {
	    final String conceptPathQueryParamName = "conceptPathQueryParam";
	    queryParamDto.addQueryCriteria("ontology.c_fullname = :" + conceptPathQueryParamName + " ")
		    .addQueryParameter(conceptPathQueryParamName, ontologyConceptSearchDto.getConceptPath());
	}
    }

    private void addConceptPathCriteria(OntologyConceptSearchDto ontologyConceptSearchDto,
	    QueryParamDto queryParamDto) {
	if (!ontologyConceptSearchDto.isModifierConcept() && !ontologyConceptSearchDto.getConceptPaths().isEmpty()) {
	    final String conceptPathQueryParamName = "conceptPathQueryParam";
	    queryParamDto.addQueryCriteria("ontology.c_fullname in ( :" + conceptPathQueryParamName + " )")
		    .addQueryParameter(conceptPathQueryParamName, ontologyConceptSearchDto.getConceptPaths());
	}
    }

    private void addModifierAppliedPathCriteria(OntologyConceptSearchDto ontologyConceptSearchDto,
	    QueryParamDto queryParamDto) {

	if (!ontologyConceptSearchDto.getModifierAppliedPaths().isEmpty()) {
	    final String modifierAppliedPathQueryParamName = MODIFIER_APPLIED_PATH;
	    queryParamDto.addQueryCriteria("ontology.m_applied_path in ( :" + modifierAppliedPathQueryParamName + " )")
		    .addQueryParameter(modifierAppliedPathQueryParamName,
			    ontologyConceptSearchDto.getModifierAppliedPaths());
	}
    }

    private void addModifierExclusionCriteria(OntologyConceptSearchDto ontologyConceptSearchDto,
	    QueryParamDto queryParamDto) {

	if (!ontologyConceptSearchDto.getModifierAppliedPaths().isEmpty()) {
	    final String modifierExclusionPathQueryParamName = "modifierExclusionPath";
	    queryParamDto.addQueryCriteria(
		    "ontology.c_fullname not in ( select distinct innerOntology.c_fullname from i2b2 innerOntology "
			    + " where innerOntology.m_applied_path in (:" + modifierExclusionPathQueryParamName
			    + " ) and innerOntology.m_exclusion_cd = 'X')")
		    .addQueryParameter(modifierExclusionPathQueryParamName,
			    ontologyConceptSearchDto.getModifierAppliedPaths());
	}
    }

    @Override
    @Transactional(readOnly = false)
    public int addOntologyToI2b2(OntologyDto ontology) {
	final String query = String.join(" ", "INSERT INTO i2b2 ",
		"(c_hlevel,c_fullname,c_name,c_synonym_cd,c_visualattributes,c_metadataxml,",
		"c_facttablecolumn,c_tablename,c_columnname,c_columndatatype,c_operator,c_dimcode,",
		"c_tooltip,m_applied_path,update_date, sourcesystem_cd) ", "VALUES (",
		":" + CH_LEVEL + ", :" + CONCEPT_FULL_NAME + ", :" + C_NAME + ", :" + C_SYNONYM_CD + ", :"
			+ C_VISUAL_ATTRIBUTES + ", :" + METADATA_XML + ", :" + C_FACT_TABLE_COLUMN,
		", :" + C_TABLE_NAME + ", :" + C_COLUMN_NAME + ", :" + C_COLUMN_DATATYPE + ", :" + C_OPERATOR + ", :"
			+ C_DIMCODE + ", :" + C_TOOLTIP + ", :" + M_APPLIED_PATH + ", :" + UPDATE_DATE + ", :"
			+ SOURCE_SYSTEM_CD + ")");

	MapSqlParameterSource parameterMap = getMappedSqlParam(ontology);

	return namedParameterJdbcTemplate.update(query, parameterMap);

    }

    @Override
    @Transactional(readOnly = false)
    public int addOntologyToTableAccess(OntologyDto ontology) {

	final String query = String.join(" ", "INSERT INTO table_access ",
		"(c_table_cd, c_table_name, c_protected_access, c_hlevel, c_fullname, c_name, c_synonym_cd, c_visualattributes,",
		"c_facttablecolumn, c_dimtablename, c_columnname, c_columndatatype, c_operator, c_dimcode,",
		"c_tooltip) VALUES (",
		":" + C_TABLE_CD + ", :" + ACCESS_TABLE_NAME + ", :" + C_PROTECTED_ACCESS + ", :" + CH_LEVEL + ", :"
			+ CONCEPT_FULL_NAME + ", :" + C_NAME,
		", :" + C_SYNONYM_CD + ", :" + C_VISUAL_ATTRIBUTES + ", :" + C_FACT_TABLE_COLUMN + ", :" + C_TABLE_NAME
			+ ", :" + C_COLUMN_NAME,
		", :" + C_COLUMN_DATATYPE + ", :" + C_OPERATOR + ", :" + C_DIMCODE + ", :" + C_TOOLTIP + ")");

	MapSqlParameterSource parameterMap = getMappedSqlParam(ontology);

	return namedParameterJdbcTemplate.update(query, parameterMap);
    }

    private MapSqlParameterSource getMappedSqlParam(OntologyDto ontology) {
	MapSqlParameterSource param = new MapSqlParameterSource();

	param.addValue(CH_LEVEL, ontology.getChLevel());
	param.addValue(CONCEPT_FULL_NAME, ontology.getPath());
	param.addValue(C_NAME, ontology.getcName());
	param.addValue(C_SYNONYM_CD, ontology.getcSynonymCd());
	param.addValue(C_VISUAL_ATTRIBUTES, ontology.getcVisualAttributes());
	param.addValue(METADATA_XML, ontology.getMetadata());
	param.addValue(C_FACT_TABLE_COLUMN, ontology.getcFactTableColumn());
	param.addValue(C_TABLE_NAME, ontology.getcTableName());
	param.addValue(C_COLUMN_NAME, ontology.getcColumnName());
	param.addValue(C_COLUMN_DATATYPE, ontology.getcColumnDatatype());
	param.addValue(C_OPERATOR, ontology.getcOperator());
	param.addValue(C_DIMCODE, ontology.getcDimcode());
	param.addValue(C_TOOLTIP, ontology.getcTooltip());
	param.addValue(M_APPLIED_PATH, ontology.getmAppliedPath());
	param.addValue(UPDATE_DATE, LocalDateTime.now());
	param.addValue(SOURCE_SYSTEM_CD, ontology.getSourceSystemCd());
	param.addValue(C_TABLE_CD, ontology.getcTableCd());
	param.addValue(ACCESS_TABLE_NAME, ontology.getAccessTableName());
	param.addValue(C_PROTECTED_ACCESS, ontology.getcProtectedAccess());

	return param;
    }

    @Override
    @Transactional(readOnly = false)
    public int updateOntologyToI2b2(OntologyConceptSearchDto ontologyConceptSearchDto) {
	QueryParamDto queryParamDto = new QueryParamDto().withQuery(String.join(" ", "update i2b2 set",
		" c_hlevel = :" + CH_LEVEL, ", c_name = :" + C_NAME, ", c_synonym_cd = :" + C_SYNONYM_CD,
		", c_visualattributes = :" + C_VISUAL_ATTRIBUTES, ", c_metadataxml = :" + METADATA_XML,
		", c_facttablecolumn = :" + C_FACT_TABLE_COLUMN, ", c_tablename = :" + C_TABLE_NAME,
		", c_columnname = :" + C_COLUMN_NAME, ", c_columndatatype = :" + C_COLUMN_DATATYPE,
		", c_operator = :" + C_OPERATOR, ", c_dimcode = :" + C_DIMCODE, ", c_tooltip = :" + C_TOOLTIP,
		", m_applied_path = :" + M_APPLIED_PATH, ", update_date = :" + UPDATE_DATE,
		", sourcesystem_cd = :" + SOURCE_SYSTEM_CD, ", c_fullname = :" + CONCEPT_FULL_NAME));

	MapSqlParameterSource parameterMap = getMappedSqlParam(ontologyConceptSearchDto.getOntologyDto());
	addExistingConceptFullNameCriteria(ontologyConceptSearchDto, queryParamDto, parameterMap);

	return namedParameterJdbcTemplate.update(queryParamDto.getQuery(), parameterMap);
    }

    @Override
    @Transactional(readOnly = false)
    public int updateOntologyToTableAccess(OntologyConceptSearchDto ontologyConceptSearchDto) {
	QueryParamDto queryParamDto = new QueryParamDto().withQuery(String.join(" ", "update table_access set",
		" c_table_cd = :" + C_TABLE_CD, ", c_table_name = :" + ACCESS_TABLE_NAME,
		", c_protected_access = :" + C_PROTECTED_ACCESS, ", c_hlevel = :" + CH_LEVEL, ", c_name = :" + C_NAME,
		", c_synonym_cd = :" + C_SYNONYM_CD, ", c_visualattributes = :" + C_VISUAL_ATTRIBUTES,
		", c_facttablecolumn = :" + C_FACT_TABLE_COLUMN, ", c_dimtablename = :" + C_TABLE_NAME,
		", c_columnname = :" + C_COLUMN_NAME, ", c_columndatatype = :" + C_COLUMN_DATATYPE,
		", c_operator = :" + C_OPERATOR, ", c_dimcode = :" + C_DIMCODE, ", c_tooltip = :" + C_TOOLTIP,
		", c_fullname = :" + CONCEPT_FULL_NAME));

	MapSqlParameterSource parameterMap = getMappedSqlParam(ontologyConceptSearchDto.getOntologyDto());

	addExistingConceptFullNameCriteria(ontologyConceptSearchDto, queryParamDto, parameterMap);
	return namedParameterJdbcTemplate.update(queryParamDto.getQuery(), parameterMap);
    }

    private void addExistingConceptFullNameCriteria(OntologyConceptSearchDto ontologyConceptSearchDto,
	    QueryParamDto queryParamDto, MapSqlParameterSource parameterMap) {
	String existingConceptFullName = ontologyConceptSearchDto.getExistingConceptFullName();
	if (existingConceptFullName != null && !existingConceptFullName.isEmpty()) {
	    queryParamDto.addQueryCriteria("c_fullname = :" + EXISTING_CONCEPT_FULL_NAME);
	    parameterMap.addValue(EXISTING_CONCEPT_FULL_NAME, existingConceptFullName);
	}
    }

    @Override
    @Transactional(readOnly = false)
    public int deleteOntologyFromI2b2(OntologyDto ontology) {
	QueryParamDto queryParamDto = new QueryParamDto().withQuery("delete from i2b2");
	addConceptFullNameCriteria(ontology, queryParamDto);

	return namedParameterJdbcTemplate.update(queryParamDto.getQuery(), queryParamDto.getParameterSource());
    }

    private void addConceptFullNameCriteria(OntologyDto ontology, QueryParamDto queryParamDto) {
	if (ontology.getPath() != null && !ontology.getPath().isEmpty()) {
	    queryParamDto.addQueryCriteria("c_fullname = :" + CONCEPT_FULL_NAME);
	    queryParamDto.addQueryParameter(CONCEPT_FULL_NAME, ontology.getPath());
	}
    }

    @Override
    @Transactional(readOnly = false)
    public int deleteOntologyFromTableAccess(OntologyDto ontology) {
	QueryParamDto queryParamDto = new QueryParamDto().withQuery("delete from table_access");
	addConceptFullNameCriteria(ontology, queryParamDto);
	return namedParameterJdbcTemplate.update(queryParamDto.getQuery(), queryParamDto.getParameterSource());
    }

    @Override
    public List<OntologyConceptDto> findOntologyConceptsByLevel(OntologyConceptSearchDto ontologyConceptSearchDto) {
        QueryParamDto queryParamDto = new QueryParamDto()
                .withQuery(String.join(" ", "select distinct ontology.c_fullname as " + CONCEPT_FULL_NAME,
                        ", ontology.c_metadataxml as " + METADATA_XML, "from i2b2 ontology"));

        addConceptPathLikeCriteria(ontologyConceptSearchDto, queryParamDto);
        addCLevelCriteria(ontologyConceptSearchDto, queryParamDto);
        String orderClause = " order by ontology.c_fullname";
        return this.namedParameterJdbcTemplate.query(queryParamDto.getQuery() + orderClause,
                queryParamDto.getParameterSource(), ontologyConceptMapper);
    }

    private void addConceptPathLikeCriteria(OntologyConceptSearchDto ontologyConceptSearchDto,
            QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(ontologyConceptSearchDto.getConceptPath())) {
            final String conceptPathQueryParamName = "conceptPathQueryParam";
            queryParamDto.addQueryCriteria("ontology.c_fullname like :" + conceptPathQueryParamName + " ")
                    .addQueryParameter(conceptPathQueryParamName,
                            ontologyConceptSearchDto.getConceptPath() + WILDCARD_CHARACTER);
        }
    }

    private void addCLevelCriteria(OntologyConceptSearchDto ontologyConceptSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(ontologyConceptSearchDto.getConceptPath())) {
            final String conceptLevelQueryParamName = "conceptLevelQueryParam";
            queryParamDto.addQueryCriteria("ontology.c_hlevel in ( :" + conceptLevelQueryParamName + " )")
                    .addQueryParameter(conceptLevelQueryParamName, ontologyConceptSearchDto.getConceptLevels());
        }
    }
}
