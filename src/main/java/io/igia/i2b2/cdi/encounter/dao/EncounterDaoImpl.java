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


package io.igia.i2b2.cdi.encounter.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.common.database.DatabaseHelper;
import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.encounter.dto.EncounterDto;
import io.igia.i2b2.cdi.encounter.dto.EncounterSearchDto;

@Repository
@Transactional(readOnly = true)
public class EncounterDaoImpl implements EncounterDao {

    protected static final String ENCOUNTER_NUM = "encounterNum";
    protected static final String ENCOUNTER_ID = "encounterIde";
    private static final String ENCOUNTER_NUM_SEQUENCE = "encounter_mapping_encounter_num_seq";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final EncounterMapper encounterMapper;
    private final String currentSchema;
    private final DatabaseHelper databaseHelper;

    public EncounterDaoImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate, EncounterMapper encounterMapper,
                            DatabaseHelper databaseHelper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.encounterMapper = encounterMapper;
        this.databaseHelper = databaseHelper;
        this.currentSchema = getCurrentSchema(this.namedParameterJdbcTemplate.getJdbcTemplate());
    }

    @PostConstruct
    private void init() {
        databaseHelper.createSequenceIfNotExists(getEncounterNumSequenceName(), findMaxEncounterNum()+1, 1);
    }

    @Override
    public List<EncounterDto> findEncounters(EncounterSearchDto encounterSearchDto) {
        QueryParamDto queryParamDto = new QueryParamDto()
            .withQuery(String.join(" ",
                "select encounter.encounter_num as " + ENCOUNTER_NUM,
                ", encounter.encounter_ide as " + ENCOUNTER_ID,
                "from encounter_mapping encounter"
            ));

        addSourceCriteria(encounterSearchDto, queryParamDto);
        addProjectIdCriteria(encounterSearchDto, queryParamDto);
        addEncounterIdCriteria(encounterSearchDto, queryParamDto);
        addEncounterNumCriteria(encounterSearchDto, queryParamDto);

        return this.namedParameterJdbcTemplate.query(
            queryParamDto.getQuery(), queryParamDto.getParameterSource(), encounterMapper);
    }

    @Override
    @Transactional(readOnly = false)
    public int addEncounterMapping(EncounterDto encounterDto) {
        final String encounterIdParam = ENCOUNTER_ID;
        final String encounterSourceParam = "encounterIdeSource";
        final String encounterStatusParam = "encounterIdeStatus";
        final String encounterNumParam = ENCOUNTER_NUM;
        final String patientIdParam = "patientIde";
        final String patientSourceParam = "patientIdeSource";
        final String projectParam = "projectId";
        final String sourceSystemCodeParam = "sourceSystemCode";
        final String updateDateParam = "updateDate";

        String query = String.join(" ",
            "insert into encounter_mapping (",
            "encounter_ide, encounter_ide_source, encounter_ide_status, encounter_num, ",
            "patient_ide, patient_ide_source, project_id, sourcesystem_cd, update_date )",
            "values ( ",
            ":" + encounterIdParam + ", :" + encounterSourceParam + ", :" + encounterStatusParam + ", :" + encounterNumParam,
            ", :" + patientIdParam + ", :" + patientSourceParam + ", :" + projectParam + ", :" + sourceSystemCodeParam,
            ", :" + updateDateParam + ") "
        );

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue(encounterIdParam, encounterDto.getEncounterId())
            .addValue(encounterSourceParam, encounterDto.getEncounterSource())
            .addValue(encounterStatusParam, encounterDto.getEncounterStatus())
            .addValue(encounterNumParam, encounterDto.getEncounterNum())
            .addValue(patientIdParam, encounterDto.getPatientId())
            .addValue(patientSourceParam, encounterDto.getPatientSource())
            .addValue(projectParam, encounterDto.getProjectId())
            .addValue(sourceSystemCodeParam, encounterDto.getSource())
            .addValue(updateDateParam, LocalDateTime.now());

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    @Override
    @Transactional(readOnly = false)
    public int addEncounter(EncounterDto encounterDto) {
        final String encounterNumParam = ENCOUNTER_NUM;
        final String patientNumParam = "patientNum";
        final String sourceSystemCodeParam = "sourceSystemCode";
        final String updateDateParam = "updateDate";

        String query = "insert into visit_dimension (encounter_num, patient_num, sourcesystem_cd, update_date)" +
            "values (:" + encounterNumParam + ", :" + patientNumParam + ", :" + sourceSystemCodeParam + ", :" + updateDateParam + ") ";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource
            .addValue(encounterNumParam, encounterDto.getEncounterNum())
            .addValue(patientNumParam, encounterDto.getPatientNum())
            .addValue(sourceSystemCodeParam, encounterDto.getSource())
            .addValue(updateDateParam, LocalDateTime.now());

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    @Override
    public Integer getNextEncounterNumber() {
        return databaseHelper.getNextSequenceValue(getEncounterNumSequenceName());
    }

    private Integer findMaxEncounterNum() {
        try {
            return this.namedParameterJdbcTemplate.queryForObject(
                "select coalesce(max(encounter.encounter_num),0) as "
                    + ENCOUNTER_NUM + " from encounter_mapping encounter", new MapSqlParameterSource(), Integer.class);
        }
        // this catch is needed in the unit test cases as database table doesn't exist yet
        catch (Exception e) {
            return 0;
        }
    }

    private String getEncounterNumSequenceName() {
        return this.currentSchema + "." + ENCOUNTER_NUM_SEQUENCE;
    }

    private String getCurrentSchema(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.execute((ConnectionCallback<String>) connection -> connection.getSchema());
    }

    private void addEncounterIdCriteria(EncounterSearchDto encounterSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(encounterSearchDto.getEncounterId())) {
            final String encounterIdQueryParamName = "encounterId";
            queryParamDto
                .addQueryCriteria("UPPER(encounter.encounter_ide) = :" + encounterIdQueryParamName)
                .addQueryParameter(encounterIdQueryParamName, encounterSearchDto.getEncounterId().toUpperCase(Locale.ENGLISH));
        }
    }
    
    private void addEncounterNumCriteria(EncounterSearchDto encounterSearchDto, QueryParamDto queryParamDto) {
        if (encounterSearchDto.getEncounterNum() != null) {
            queryParamDto.addQueryCriteria("encounter.encounter_num = :" + ENCOUNTER_NUM)
                    .addQueryParameter(ENCOUNTER_NUM, encounterSearchDto.getEncounterNum());
        }
    }

    private void addProjectIdCriteria(EncounterSearchDto encounterSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(encounterSearchDto.getProjectId())) {
            final String projectQueryParamName = "projectId";
            queryParamDto
                .addQueryCriteria("UPPER(encounter.project_id) = :" + projectQueryParamName)
                .addQueryParameter(projectQueryParamName, encounterSearchDto.getProjectId().toUpperCase(Locale.ENGLISH));
        }
    }

    private void addSourceCriteria(EncounterSearchDto encounterSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(encounterSearchDto.getSource())) {
            final String sourceQueryParamName = "sourceSystem";
            queryParamDto
                .addQueryCriteria("UPPER(encounter.sourcesystem_cd) = :" + sourceQueryParamName)
                .addQueryParameter(sourceQueryParamName, encounterSearchDto.getSource().toUpperCase(Locale.ENGLISH));
        }
    }

    @Override
    public List<EncounterDto> findEncounterByEncounterNum(EncounterSearchDto encounterSearchDto) {
        QueryParamDto queryParamDto = new QueryParamDto()
                .withQuery(String.join(" ", "select encounter.encounter_num as " + ENCOUNTER_NUM,
                        ", encounter.encounter_ide as " + ENCOUNTER_ID, "from encounter_mapping encounter"));

        addEncounterNumCriteria(encounterSearchDto, queryParamDto);

        return this.namedParameterJdbcTemplate.query(queryParamDto.getQuery(), queryParamDto.getParameterSource(),
                encounterMapper);
    }
}
