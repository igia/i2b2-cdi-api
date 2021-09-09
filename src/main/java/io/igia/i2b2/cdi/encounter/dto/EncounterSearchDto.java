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

public class EncounterSearchDto {

    private String encounterId;
    private Integer encounterNum;
    private String patientId;
    private String source;
    private String projectId;

    public EncounterSearchDto() {
    }

    public EncounterSearchDto(EncounterSearchDto encounterSearchDto) {
        this.encounterId = encounterSearchDto.getEncounterId();
        this.patientId = encounterSearchDto.getPatientId();
        this.source = encounterSearchDto.getSource();
        this.projectId = encounterSearchDto.getProjectId();
        this.encounterNum = encounterSearchDto.getEncounterNum();
    }

    public String getEncounterId() {
        return encounterId;
    }

    public EncounterSearchDto setEncounterId(String encounterId) {
        this.encounterId = encounterId;
        return this;
    }
    
    public Integer getEncounterNum() {
        return encounterNum;
    }

    public EncounterSearchDto setEncounterNum(Integer encounterNum) {
        this.encounterNum = encounterNum;
        return this;
    }

    public String getPatientId() {
        return patientId;
    }

    public EncounterSearchDto setPatientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public String getSource() {
        return source;
    }

    public EncounterSearchDto setSource(String source) {
        this.source = source;
        return this;
    }

    public String getProjectId() {
        return projectId;
    }

    public EncounterSearchDto setProjectId(String projectId) {
        this.projectId = projectId;
        return this;
    }
}
