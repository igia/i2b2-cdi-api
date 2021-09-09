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


package io.igia.i2b2.cdi.patient.service;

import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.config.ApplicationProperties;
import io.igia.i2b2.cdi.patient.dao.PatientDao;
import io.igia.i2b2.cdi.patient.dto.PatientDto;
import io.igia.i2b2.cdi.patient.dto.PatientSearchDto;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PatientServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private PatientDao patientDao;

    private PatientService patientService;

    @Before
    public void setUp() {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        patientService = new PatientServiceImpl(patientDao, applicationProperties);
    }

    @Test
    public void addPatient() {

        given(patientDao.addPatientMapping(argThat(e -> e.getPatientId().equals("1"))))
            .willReturn(1);

        given(patientDao.addPatient(argThat(e -> e.getPatientId().equals("1"))))
            .willReturn(1);

        PatientDto patient = patientService.addPatient(createDefaultPatient(1, "1"));
        assertThat(patient).isNotNull();
        assertThat(patient.getPatientId()).isNotNull().isEqualTo("1");

        verify(patientDao, times(1)).addPatientMapping(argThat(e -> e.getPatientId().equals("1")));
        verify(patientDao, times(1)).addPatient(argThat(e -> e.getPatientId().equals("1")));
    }

    @Test
    public void addPatient_minimalData() {

        given(patientDao.addPatientMapping(argThat(e -> e.getPatientId().equals("1"))))
            .willReturn(1);

        given(patientDao.addPatient(argThat(e -> e.getPatientId().equals("1"))))
            .willReturn(1);

        PatientDto patient = patientService.addPatient(createPatient(1, "1"));
        assertThat(patient).isNotNull();
        assertThat(patient.getPatientId()).isNotNull().isEqualTo("1");

        verify(patientDao, times(1)).addPatientMapping(argThat(e -> e.getPatientId().equals("1")));
        verify(patientDao, times(1)).addPatient(argThat(e -> e.getPatientId().equals("1")));
    }

    @Test
    public void addPatient_fail_patientMapping() {

        given(patientDao.addPatientMapping(argThat(e -> e.getPatientId().equals("1"))))
            .willReturn(0);

        thrown.expect(I2b2Exception.class);
        thrown.expectMessage("Could not add patient record.");
        patientService.addPatient(createPatient(1, "1"));
        verify(patientDao, times(1)).addPatientMapping(argThat(e -> e.getPatientId().equals("1")));
    }

    @Test
    public void addPatient_fail_patient() {

        given(patientDao.addPatientMapping(argThat(e -> e.getPatientId().equals("1"))))
            .willReturn(1);

        given(patientDao.addPatient(argThat(e -> e.getPatientId().equals("1"))))
            .willReturn(0);

        thrown.expect(I2b2Exception.class);
        thrown.expectMessage("Could not add patient record.");
        patientService.addPatient(createPatient(1, "1"));
        verify(patientDao, times(1)).addPatientMapping(argThat(e -> e.getPatientId().equals("1")));
        verify(patientDao, times(1)).addPatient(argThat(e -> e.getPatientId().equals("1")));
    }

    @Test
    public void testGetPatients() {
        List<PatientDto> patients = Arrays.asList(
            createPatient(1, "1"), createPatient(2, "2"));
        given(patientDao.findPatients(any())).willReturn(patients);

        List<PatientDto> actualPatients = patientService.getPatients(new PatientSearchDto());

        assertThat(actualPatients).isNotNull().isNotEmpty().size().isEqualTo(2);
        assertThat(actualPatients.get(0)).isEqualToComparingFieldByField(patients.get(0));
        assertThat(actualPatients.get(1)).isEqualToComparingFieldByField(patients.get(1));
    }
    
    @Test
    public void testGetPatientByPatientNum() {
        List<PatientDto> patients = Arrays.asList(createPatient(1, "1"));
        given(patientDao.findPatientByPatientNum(any())).willReturn(patients);

        List<PatientDto> actualPatients = patientService.getPatientByPatientNum(new PatientSearchDto());

        assertThat(actualPatients).isNotNull().isNotEmpty().size().isEqualTo(1);
        assertThat(actualPatients.get(0)).isEqualToComparingFieldByField(patients.get(0));
    }

    private PatientDto createPatient(Integer patientNum, String patientId) {
        return createDetailedPatient(patientNum, patientId, null, null, null, null);
    }

    private PatientDto createDefaultPatient(Integer patientNum, String patientId) {
        return createDetailedPatient(patientNum, patientId, "A", "i2b2",
            "pr1", "demo");
    }

    private PatientDto createDetailedPatient(Integer patientNum, String patientId, String patientStatus,
                                             String patientSource, String projectId, String source) {
        PatientDto patient = new PatientDto();
        patient.setPatientStatus(patientStatus);
        patient.setPatientNum(patientNum);
        patient.setPatientId(patientId);
        patient.setPatientSource(patientSource);
        patient.setProjectId(projectId);
        patient.setSource(source);
        return patient;
    }
}
