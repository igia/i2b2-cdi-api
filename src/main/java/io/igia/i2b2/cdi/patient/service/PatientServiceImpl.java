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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class PatientServiceImpl implements PatientService {

    private final PatientDao patientDao;
    private final ApplicationProperties applicationProperties;


    public PatientServiceImpl(PatientDao patientDao, ApplicationProperties applicationProperties) {
        this.patientDao = patientDao;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public List<PatientDto> getPatients(PatientSearchDto inPatientSearchDto) {

        PatientSearchDto patientSearchDto = new PatientSearchDto(inPatientSearchDto);
        if (StringUtils.isEmpty(patientSearchDto.getProjectId())) {
            patientSearchDto.setProjectId(applicationProperties.getProjectId());
        }

        return patientDao.findPatients(patientSearchDto);
    }

    @Override
    @Transactional(readOnly = false)
    public PatientDto addPatient(PatientDto inPatientDto) {

        PatientDto patientDto = new PatientDto(inPatientDto);

        if (StringUtils.isEmpty(patientDto.getProjectId())) {
            patientDto.setProjectId(applicationProperties.getProjectId());
        }

        if (StringUtils.isEmpty(patientDto.getSource())) {
            patientDto.setSource(applicationProperties.getSourceSystemCode());
        }
        if (StringUtils.isEmpty(patientDto.getPatientSource())) {
            patientDto.setPatientSource(applicationProperties.getPatientSource());
        }
        if (StringUtils.isEmpty(patientDto.getPatientStatus())) {
            patientDto.setPatientStatus(applicationProperties.getPatientStatus());
        }

        patientDto.setPatientNum(patientDao.getNextPatientNumber());

        int updateCount = patientDao.addPatientMapping(patientDto);
        if (updateCount == 0) {
            throw new I2b2Exception("Could not add patient record.");
        }
        updateCount = patientDao.addPatient(patientDto);
        if (updateCount == 0) {
            throw new I2b2Exception("Could not add patient record.");
        }
        return patientDto;
    }

    @Override
    public List<PatientDto> getPatientByPatientNum(PatientSearchDto patientSearchDto) {
        return patientDao.findPatientByPatientNum(patientSearchDto);
    }
}
