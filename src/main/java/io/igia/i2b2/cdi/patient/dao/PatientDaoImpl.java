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

package io.igia.i2b2.cdi.patient.dao;

import io.igia.i2b2.cdi.common.database.DatabaseHelper;
import io.igia.i2b2.cdi.common.dto.QueryParamDto;
import io.igia.i2b2.cdi.patient.dto.PatientDto;
import io.igia.i2b2.cdi.patient.dto.PatientSearchDto;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Repository
@Transactional(readOnly = true)
public class PatientDaoImpl implements PatientDao {

    protected static final String PATIENT_NUM = "patientNum";
    protected static final String PATIENT_ID = "patientIde";
    private static final String PATIENT_NUM_SEQUENCE = "patient_mapping_patient_num_seq";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PatientMapper patientMapper;
    private final String currentSchema;
    private final DatabaseHelper databaseHelper;

    public PatientDaoImpl(DataSource dataSource, PatientMapper patientMapper, DatabaseHelper databaseHelper) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(new JdbcTemplate(dataSource));
        this.patientMapper = patientMapper;
        this.databaseHelper = databaseHelper;
        this.currentSchema = getCurrentSchema(this.namedParameterJdbcTemplate.getJdbcTemplate());
    }

    @PostConstruct
    private void init() {
        databaseHelper.createSequenceIfNotExists(getPatientNumSequenceName(),
            findMaxPatientNum() + 1, 1);
    }

    @Override
    public List<PatientDto> findPatients(PatientSearchDto patientSearchDto) {
        QueryParamDto queryParamDto = new QueryParamDto()
            .withQuery(String.join(" ",
                "select patient.patient_num as " + PATIENT_NUM,
                ", patient.patient_ide as " + PATIENT_ID,
                "from patient_mapping patient"
            ));

        addSourceCriteria(patientSearchDto, queryParamDto);
        addProjectIdCriteria(patientSearchDto, queryParamDto);
        addPatientIdCriteria(patientSearchDto, queryParamDto);
        addPatientNumCriteria(patientSearchDto, queryParamDto);

        return this.namedParameterJdbcTemplate.query(
            queryParamDto.getQuery(), queryParamDto.getParameterSource(), patientMapper);
    }

    @Override
    public List<PatientDto> findPatientByPatientNum(PatientSearchDto patientSearchDto) {
        QueryParamDto queryParamDto = new QueryParamDto()
            .withQuery(String.join(" ",
                "select patient.patient_num as " + PATIENT_NUM,
                ", patient.patient_ide as " + PATIENT_ID,
                "from patient_mapping patient"
            ));
        addPatientNumCriteria(patientSearchDto, queryParamDto);

        return this.namedParameterJdbcTemplate.query(
            queryParamDto.getQuery(), queryParamDto.getParameterSource(), patientMapper);
    }
    
    @Override
    public int addPatientMapping(PatientDto patientDto) {

        final String patientIdParam = PATIENT_ID;
        final String patientSourceParam = "patientIdeSource";
        final String patientStatusParam = "patientIdeStatus";
        final String patientNumParam = PATIENT_NUM;
        final String projectParam = "projectId";
        final String sourceSystemCodeParam = "sourceSystemCode";
        final String updateDateParam = "updateDate";

        String query = String.join(" ",
            "insert into patient_mapping (",
            "patient_ide, patient_ide_source, patient_ide_status, patient_num, ",
            "project_id, sourcesystem_cd, update_date )",
            "values ( ",
            ":" + patientIdParam + ", :" + patientSourceParam + ", :" + patientStatusParam + ", :" + patientNumParam + ", ",
            ":" + projectParam + ", :" + sourceSystemCodeParam + ", :" + updateDateParam + ") "
        );

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue(patientIdParam, patientDto.getPatientId())
            .addValue(patientSourceParam, patientDto.getPatientSource())
            .addValue(patientStatusParam, patientDto.getPatientStatus())
            .addValue(patientNumParam, patientDto.getPatientNum())
            .addValue(projectParam, patientDto.getProjectId())
            .addValue(sourceSystemCodeParam, patientDto.getSource())
            .addValue(updateDateParam, LocalDateTime.now());

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    @Override
    public int addPatient(PatientDto patientDto) {

        final String patientNumParam = PATIENT_NUM;
        final String sourceSystemCodeParam = "sourceSystemCode";
        final String updateDateParam = "updateDate";

        String query = "insert into patient_dimension (patient_num, sourcesystem_cd, update_date)" +
            "values (:" + patientNumParam + ", :" + sourceSystemCodeParam + ", :" + updateDateParam + ") ";

        MapSqlParameterSource parameterSource = new MapSqlParameterSource();
        parameterSource.addValue(patientNumParam, patientDto.getPatientNum())
            .addValue(sourceSystemCodeParam, patientDto.getSource())
            .addValue(updateDateParam, LocalDateTime.now());

        return namedParameterJdbcTemplate.update(query, parameterSource);
    }

    @Override
    public Integer getNextPatientNumber() {
        return databaseHelper.getNextSequenceValue(getPatientNumSequenceName());
    }

    private Integer findMaxPatientNum() {
        try {
            QueryParamDto queryParamDto = new QueryParamDto()
                .withQuery(String.join(" ",
                    "select coalesce(MAX(patient.patient_num),0) as " + PATIENT_NUM,
                    "from patient_mapping patient"
                ));

            return this.namedParameterJdbcTemplate.queryForObject(
                queryParamDto.getQuery(), queryParamDto.getParameterSource(), Integer.class);
        }
        // this catch is needed in the unit test cases as database table doesn't exist yet
        catch (Exception e) {
            return 0;
        }
    }

    private String getPatientNumSequenceName() {
        return this.currentSchema + "." + PATIENT_NUM_SEQUENCE;
    }

    private String getCurrentSchema(JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.execute((ConnectionCallback<String>) connection -> connection.getSchema());
    }

    private void addPatientIdCriteria(PatientSearchDto patientSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(patientSearchDto.getPatientId())) {
            final String patientQueryParamName = "patientId";
            queryParamDto
                .addQueryCriteria("UPPER(patient.patient_ide) = :" + patientQueryParamName)
                .addQueryParameter(patientQueryParamName, patientSearchDto.getPatientId().toUpperCase(Locale.ENGLISH));
        }
    }

    private void addProjectIdCriteria(PatientSearchDto patientSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(patientSearchDto.getProjectId())) {
            final String projectQueryParamName = "projectId";
            queryParamDto
                .addQueryCriteria("UPPER(patient.project_id) = :" + projectQueryParamName)
                .addQueryParameter(projectQueryParamName, patientSearchDto.getProjectId().toUpperCase(Locale.ENGLISH));
        }
    }

    private void addSourceCriteria(PatientSearchDto patientSearchDto, QueryParamDto queryParamDto) {
        if (!StringUtils.isEmpty(patientSearchDto.getSource())) {
            final String sourceQueryParamName = "sourceSystem";
            queryParamDto
                .addQueryCriteria("UPPER(patient.sourcesystem_cd) = :" + sourceQueryParamName)
                .addQueryParameter(sourceQueryParamName, patientSearchDto.getSource().toUpperCase(Locale.ENGLISH));
        }
    }
    
    private void addPatientNumCriteria(PatientSearchDto patientSearchDto, QueryParamDto queryParamDto) {
        if (patientSearchDto.getPatientNum() != null) {
            queryParamDto
                .addQueryCriteria("patient.patient_num = :" + PATIENT_NUM)
                .addQueryParameter(PATIENT_NUM, patientSearchDto.getPatientNum());
        }
    }
}
