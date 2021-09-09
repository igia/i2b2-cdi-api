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

import io.igia.i2b2.cdi.patient.dto.PatientDto;
import io.igia.i2b2.cdi.patient.dto.PatientSearchDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan({"io.igia.i2b2.cdi.patient.dao", "io.igia.i2b2.cdi.common.database"})
@DirtiesContext
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb"})
@Sql({"/test-schema.sql", "/test-patient-data.sql"})
public class PatientDaoTest {

    @Autowired
    private PatientDao patientDao;

    @Test
    public void findPatients() {
        List<PatientDto> patients = patientDao.findPatients(new PatientSearchDto());
        assertThat(patients).isNotNull().isNotEmpty().size().isGreaterThanOrEqualTo(3);
    }

    @Test
    public void findPatients_noRecords() {
        List<PatientDto> patients = patientDao.findPatients(new PatientSearchDto().setPatientId("4"));
        assertThat(patients).isNotNull().isEmpty();
    }

    @Test
    public void findPatients_filterByPatientId() {
        List<PatientDto> patients = patientDao.findPatients(new PatientSearchDto().setPatientId("1"));
        assertThat(patients).isNotNull().isNotEmpty().size().isEqualTo(1);
        PatientDto expectedPatient = createDetailedPatient(
            1, "1", null, null, null, null);
        assertThat(patients.get(0)).isNotNull().isEqualToComparingFieldByField(expectedPatient);
    }
    
    @Test
    public void findPatientFilterByPatientNum() {
        List<PatientDto> patients = patientDao.findPatientByPatientNum(new PatientSearchDto().setPatientNum(1));
        assertThat(patients).isNotNull().isNotEmpty().size().isEqualTo(1);
        PatientDto expectedPatient = createDetailedPatient(
            1, "1", null, null, null, null);
        assertThat(patients.get(0)).isNotNull().isEqualToComparingFieldByField(expectedPatient);
    }

    @Test
    public void findPatients_filterByPatientId_filterByProjectId_filterBySource() {
        List<PatientDto> patients = patientDao.findPatients(new PatientSearchDto()
            .setPatientId("3").setProjectId("PR1").setSource("TEST"));
        assertThat(patients).isNotNull().isNotEmpty().size().isEqualTo(1);
        PatientDto expectedPatient = createDetailedPatient(
            3, "3", null, null, null, null);
        assertThat(patients.get(0)).isNotNull().isEqualToComparingFieldByField(expectedPatient);
    }

    @Test
    public void getNextPatientNumber() {
        assertThat(patientDao.getNextPatientNumber()).isNotNull().isEqualTo(1);
        assertThat(patientDao.getNextPatientNumber()).isNotNull().isEqualTo(2);
    }

    @Test
    public void addPatientMapping() {
        int status = patientDao.addPatientMapping(createPatient(1, "1"));
        assertThat(status).isEqualTo(1);
    }

    @Test
    public void addPatient() {
        int status = patientDao.addPatient(createPatient(4, "4"));
        assertThat(status).isEqualTo(1);
    }

    private PatientDto createPatient(
        Integer patientNum, String patientId) {
        return createDetailedPatient(patientNum, patientId, "source",
            "A", "prj", "demo");
    }

    private PatientDto createDetailedPatient(Integer patientNum, String patientId, String patientSource,
                                             String patientStatus, String projectId, String source) {
        PatientDto patient = new PatientDto();
        patient.setPatientNum(patientNum);
        patient.setPatientId(patientId);
        patient.setPatientSource(patientSource);
        patient.setPatientStatus(patientStatus);
        patient.setProjectId(projectId);
        patient.setSource(source);
        return patient;
    }
}
