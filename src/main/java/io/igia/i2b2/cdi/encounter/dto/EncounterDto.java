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



package io.igia.i2b2.cdi.encounter.dto;

public class EncounterDto {

    private String encounterId;
    private String encounterSource;
    private String encounterStatus;
    private Integer encounterNum;
    private String patientId;
    private Integer patientNum;
    private String patientSource;
    private String projectId;
    private String source;

    public EncounterDto() {
    }

    public EncounterDto(EncounterDto encounterDto) {
        this.encounterId = encounterDto.getEncounterId();
        this.encounterSource = encounterDto.getEncounterSource();
        this.encounterStatus = encounterDto.getEncounterStatus();
        this.encounterNum = encounterDto.getEncounterNum();
        this.patientId = encounterDto.getPatientId();
        this.patientNum = encounterDto.getPatientNum();
        this.patientSource = encounterDto.getPatientSource();
        this.projectId = encounterDto.getProjectId();
        this.source = encounterDto.getSource();
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public String getEncounterSource() {
        return encounterSource;
    }

    public void setEncounterSource(String encounterSource) {
        this.encounterSource = encounterSource;
    }

    public String getEncounterStatus() {
        return encounterStatus;
    }

    public void setEncounterStatus(String encounterStatus) {
        this.encounterStatus = encounterStatus;
    }

    public Integer getEncounterNum() {
        return encounterNum;
    }

    public void setEncounterNum(Integer encounterNum) {
        this.encounterNum = encounterNum;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Integer getPatientNum() {
        return patientNum;
    }

    public void setPatientNum(Integer patientNum) {
        this.patientNum = patientNum;
    }

    public String getPatientSource() {
        return patientSource;
    }

    public void setPatientSource(String patientSource) {
        this.patientSource = patientSource;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
