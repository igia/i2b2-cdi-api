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


package io.igia.i2b2.cdi.patient.dto;

public class PatientSearchDto {
    private String patientId;
    private Integer patientNum;
    private String source;
    private String projectId;

    public PatientSearchDto() {
    }

    public PatientSearchDto(PatientSearchDto patientSearchDto) {
        this.patientId = patientSearchDto.getPatientId();
        this.source = patientSearchDto.getSource();
        this.projectId = patientSearchDto.getProjectId();
        this.patientNum = patientSearchDto.getPatientNum();
    }

    public String getPatientId() {
        return patientId;
    }

    public PatientSearchDto setPatientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public String getSource() {
        return source;
    }

    public PatientSearchDto setSource(String source) {
        this.source = source;
        return this;
    }

    public String getProjectId() {
        return projectId;
    }

    public PatientSearchDto setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }

    public Integer getPatientNum() {
        return patientNum;
    }

    public PatientSearchDto setPatientNum(Integer patientNum) {
        this.patientNum = patientNum;
        return this;
    }
}
