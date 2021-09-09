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

public class PatientDto {

    private String patientId;
    private String patientSource;
    private String patientStatus;
    private Integer patientNum;
    private String source;
    private String projectId;

    public PatientDto() {
    }

    public PatientDto(PatientDto patientDto) {
        this.patientId = patientDto.getPatientId();
        this.patientSource = patientDto.getPatientSource();
        this.patientStatus = patientDto.getPatientStatus();
        this.patientNum = patientDto.getPatientNum();
        this.source = patientDto.getSource();
        this.projectId = patientDto.getProjectId();
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientSource() {
        return patientSource;
    }

    public void setPatientSource(String patientSource) {
        this.patientSource = patientSource;
    }

    public String getPatientStatus() {
        return patientStatus;
    }

    public void setPatientStatus(String patientStatus) {
        this.patientStatus = patientStatus;
    }

    public Integer getPatientNum() {
        return patientNum;
    }

    public void setPatientNum(Integer patientNum) {
        this.patientNum = patientNum;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
