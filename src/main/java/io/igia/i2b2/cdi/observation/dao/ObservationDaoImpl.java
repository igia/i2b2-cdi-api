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

package io.igia.i2b2.cdi.observation.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.common.cache.RequestCache;
import io.igia.i2b2.cdi.common.database.DatabaseHelper;
import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationQueryParamName;
import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.observation.domain.Observation;
import io.igia.i2b2.cdi.observation.domain.ObservationModifier;
import io.igia.i2b2.cdi.observation.dto.FactSearchDto;

@Repository
@Transactional(readOnly = true)
public class ObservationDaoImpl implements ObservationDao {

    private static final String NEGATIVE_ENCOUNTER_NUM_SEQUENCE = "observation_fact_negative_encounter_num_seq";
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final String currentSchema;
    private final DatabaseHelper databaseHelper;
    private final ObservationMapper observationMapper;
    protected static final String ENCOUNTER_NUM = "encounterNum";
    protected static final String PATIENT_NUM = "patientNum";
    protected static final String CONCEPT_CODE = "conceptCode";
    protected static final String PROVIDER_ID = "providerId";
    protected static final String START_DATE = "startDate";
    protected static final String MODIFIER_CODE = "modifierCode";
    protected static final String INSTANCE_NUM = "instanceNum";
    protected static final String VALTYPE_CODE = "valTypeCode";
    protected static final String TEXT_VAL = "textVal";
    protected static final String NUMERIC_VAL = "numericVal";
    protected static final String UNITS = "units";
    protected static final String END_DATE = "endDate";
    protected static final String OBSERVATION_BLOB = "observationBlob";
    protected static final String SOURCE_SYSTEM_CODE = "sourceSystemCode";
    protected static final String UPDATE_DATE = "updateDate";

    public ObservationDaoImpl(DataSource dataSource, DatabaseHelper databaseHelper,
            ObservationMapper observationMapper) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));
        this.databaseHelper = databaseHelper;
        this.currentSchema = getCurrentSchema(this.namedParameterJdbcTemplate.getJdbcTemplate());
        this.observationMapper = observationMapper;
    }

    @PostConstruct
    private void init() {
        databaseHelper.createSequenceIfNotExists(getNegativeEncounterNumSequenceName(),
            findMinimumNegativeEncounterNum()-1, -1);
    }

    @Override
    @Transactional(readOnly = false)
    public int[] add(Observation observation) {

        String query = String.join(" ",
            "insert into observation_fact (",
            "encounter_num, patient_num, concept_cd, provider_id, ",
            "start_date, modifier_cd, instance_num, valtype_cd, ",
            "tval_char, nval_num, units_cd, end_date, ",
            "observation_blob, sourcesystem_cd, update_date)",
            "values ( ",
            ":" + ENCOUNTER_NUM + ", :" + PATIENT_NUM + ", :" + CONCEPT_CODE + ", :" + PROVIDER_ID + ", ",
            ":" + START_DATE + ", :" + MODIFIER_CODE + ", :" + INSTANCE_NUM + ", :" + VALTYPE_CODE + ", ",
            ":" + TEXT_VAL + ", :" + NUMERIC_VAL + ", :" + UNITS + ", :" + END_DATE + ", ",
            ":" + OBSERVATION_BLOB + ", :" + SOURCE_SYSTEM_CODE + ", :" + UPDATE_DATE + ") "
        );

        SqlParameterSource[] parameterSources = new SqlParameterSource[observation.getModifiers().size()];
        for (int modIndex = 0; modIndex < observation.getModifiers().size(); modIndex++) {
            ObservationModifier modifier = observation.getModifiers().get(modIndex);
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue(ENCOUNTER_NUM, observation.getEncounterNum())
                .addValue(PATIENT_NUM, observation.getPatientNum())
                .addValue(CONCEPT_CODE, observation.getConceptCode())
                .addValue(PROVIDER_ID, observation.getProviderId())
                .addValue(START_DATE, observation.getStartDate())
                .addValue(INSTANCE_NUM, observation.getInstanceNumber())
                .addValue(END_DATE, observation.getEndDate())
                .addValue(SOURCE_SYSTEM_CODE, observation.getSourceSystemCode())
                .addValue(MODIFIER_CODE, modifier.getModifierCode())
                .addValue(VALTYPE_CODE, modifier.getValueTypeCode().getCode())
                .addValue(TEXT_VAL, modifier.getTextValue())
                .addValue(NUMERIC_VAL, modifier.getNumberValue())
                .addValue(UNITS, modifier.getUnits())
                .addValue(OBSERVATION_BLOB, modifier.getBlob())
                .addValue(UPDATE_DATE, LocalDateTime.now());


            parameterSources[modIndex] = parameterSource;
        }

        return namedParameterJdbcTemplate.batchUpdate(query, parameterSources);
    }

    @Override
    public Integer getNextInstanceNumberForObservationFact(FactSearchDto factSearchDto) {

        QueryParamDto queryParamDto = new QueryParamDto()
            .withQuery(String.join(" ",
                "select coalesce(max(observation.instance_num)+1, 1) as nextInstanceNumber from",
                "observation_fact observation"
            ));

        addEncounterCriteria(factSearchDto, queryParamDto);
        addPatientCriteria(factSearchDto, queryParamDto);
        addConceptCodeCriteria(factSearchDto, queryParamDto);
        addProviderCriteria(factSearchDto, queryParamDto);
        addStartDateCriteria(factSearchDto, queryParamDto);
        return this.namedParameterJdbcTemplate.queryForObject(queryParamDto.getQuery(), queryParamDto.getParameterSource(), Integer.class);
    }

    private void addEncounterCriteria(FactSearchDto factSearchDto, QueryParamDto queryParamDto) {
        if (factSearchDto.getEncounterNum() != null) {
            queryParamDto
                .addQueryCriteria("observation.encounter_num = :" + ENCOUNTER_NUM)
                .addQueryParameter(ENCOUNTER_NUM, factSearchDto.getEncounterNum());
        }
    }

    private void addPatientCriteria(FactSearchDto factSearchDto, QueryParamDto queryParamDto) {
        if (factSearchDto.getPatientNum() != null) {
            queryParamDto
                .addQueryCriteria("observation.patient_num = :" + PATIENT_NUM)
                .addQueryParameter(PATIENT_NUM, factSearchDto.getPatientNum());
        }
    }

    private void addConceptCodeCriteria(FactSearchDto factSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(factSearchDto.getConceptCode())) {
            queryParamDto
                .addQueryCriteria("UPPER(observation.concept_cd) = :" + CONCEPT_CODE)
                .addQueryParameter(CONCEPT_CODE, factSearchDto.getConceptCode().toUpperCase(Locale.ENGLISH));
        }
    }

    private void addProviderCriteria(FactSearchDto factSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(factSearchDto.getProviderId())) {
            queryParamDto
                .addQueryCriteria("UPPER(observation.provider_id) = :" + PROVIDER_ID)
                .addQueryParameter(PROVIDER_ID, factSearchDto.getProviderId().toUpperCase(Locale.ENGLISH));
        }
    }

    private void addStartDateCriteria(FactSearchDto factSearchDto, QueryParamDto queryParamDto) {
        if (factSearchDto.getStartDate() != null) {
            queryParamDto
                .addQueryCriteria("observation.start_date = :" + START_DATE)
                .addQueryParameter(START_DATE, factSearchDto.getStartDate());
        }
    }

    @Override
    public Integer getNextNegativeEncounterNumber() {
        return databaseHelper.getNextSequenceValue(getNegativeEncounterNumSequenceName());
    }

    private String getNegativeEncounterNumSequenceName() {
        return this.currentSchema + "." + NEGATIVE_ENCOUNTER_NUM_SEQUENCE;
    }

    private String getCurrentSchema(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.execute((ConnectionCallback<String>) connection -> connection.getSchema());
    }

    private Integer findMinimumNegativeEncounterNum() {
        try {
            return this.namedParameterJdbcTemplate.queryForObject(
                "select coalesce(min(case when observation.encounter_num < 0 then observation.encounter_num end), 0) " +
                    "as maxNegEncounterNum from observation_fact observation", new MapSqlParameterSource(), Integer.class);
        }
        // this catch is needed in the unit test cases as database table doesn't exist yet
        catch (Exception e) {
            return 0;
        }
    }

    @Override
    @RequestCache
    public PaginationResult<Observation> findObservations(FactSearchDto factSearchDto) {

        QueryParamDto queryParamDto = new QueryParamDto().withQuery(String.join(" ",
                "select observation.encounter_num as " + ENCOUNTER_NUM + ",",
                "observation.patient_num as " + PATIENT_NUM + ",", "observation.concept_cd as " + CONCEPT_CODE + ",",
                "observation.provider_id as " + PROVIDER_ID + ",",
                "observation.start_date as " + START_DATE + ",", "observation.modifier_cd as " + MODIFIER_CODE + ",",
                "observation.instance_num as " + INSTANCE_NUM + ",", "observation.valtype_cd as " + VALTYPE_CODE + ",",
                "observation.tval_char as " + TEXT_VAL + ",", "observation.nval_num as " + NUMERIC_VAL + ",",
                "observation.units_cd as " + UNITS + ",", "observation.end_date as " + END_DATE + ",",
                "observation.observation_blob as " + OBSERVATION_BLOB,
                "from observation_fact observation"));

        addModifierEqualsCriteria(queryParamDto);
        addPatientCriteria(factSearchDto, queryParamDto);
        addConceptCodeCriteria(factSearchDto, queryParamDto);
        addPaginationCriteria(queryParamDto, factSearchDto.getPageableDto());
        List<Observation> observations = this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(),
                queryParamDto.getParameterSource(), observationMapper);
        return new PaginationResult<>(observations, 0);
    }

    private void addModifierEqualsCriteria(QueryParamDto queryParamDto) {
        queryParamDto.addQueryCriteria("observation.modifier_cd = :" + MODIFIER_CODE)
                .addQueryParameter(MODIFIER_CODE, "@");
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
    
    @Override
    public int getTotalCount(FactSearchDto factSearchDto) {
        int count = 0;
        QueryParamDto countQueryParamDto = new QueryParamDto();
        countQueryParamDto.withQuery(String.join(" ", "select count(patient_num) from observation_fact observation"));
        addModifierEqualsCriteria(countQueryParamDto);
        addPatientCriteria(factSearchDto, countQueryParamDto);
        addConceptCodeCriteria(factSearchDto, countQueryParamDto);
        count = this.namedParameterJdbcTemplate.queryForObject(countQueryParamDto.getQuery(),
                countQueryParamDto.getParameterSource(), Integer.class);
        return count;
    }

    @Override
    @RequestCache
    public List<Observation> findObservationModifiers(FactSearchDto factSearchDto) {
        QueryParamDto queryParamDto = new QueryParamDto().withQuery(String.join(" ",
                "select observation.encounter_num as " + ENCOUNTER_NUM + ",",
                "observation.patient_num as " + PATIENT_NUM + ",", "observation.concept_cd as " + CONCEPT_CODE + ",",
                "observation.provider_id as " + PROVIDER_ID + ",",
                "observation.start_date as " + START_DATE + ",", "observation.modifier_cd as " + MODIFIER_CODE + ",",
                "observation.instance_num as " + INSTANCE_NUM + ",", "observation.valtype_cd as " + VALTYPE_CODE + ",",
                "observation.tval_char as " + TEXT_VAL + ",", "observation.nval_num as " + NUMERIC_VAL + ",",
                "observation.units_cd as " + UNITS + ",", "observation.end_date as " + END_DATE + ",",
                "observation.observation_blob as " + OBSERVATION_BLOB,
                "from observation_fact observation"));

        addModifierNotEqualsCriteria(queryParamDto);
        addPatientCriteria(factSearchDto, queryParamDto);
        addConceptCodeCriteria(factSearchDto, queryParamDto);
        return this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(),
                queryParamDto.getParameterSource(), observationMapper);
    }
    
    private void addModifierNotEqualsCriteria(QueryParamDto queryParamDto) {
        queryParamDto.addQueryCriteria("observation.modifier_cd != :" + MODIFIER_CODE)
                .addQueryParameter(MODIFIER_CODE, "@");
    }
}
