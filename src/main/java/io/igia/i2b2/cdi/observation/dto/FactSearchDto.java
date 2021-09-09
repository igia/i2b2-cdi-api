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
package io.igia.i2b2.cdi.observation.dto;

import java.time.LocalDateTime;

import io.igia.i2b2.cdi.common.dto.PageableDto;

public class FactSearchDto {
    private Integer encounterNum;
    private Integer patientNum;
    private String patientId;
    private String conceptCode;
    private String providerId;
    private LocalDateTime startDate;
    private boolean modifierFlag = false;
    private PageableDto pageableDto;

    public FactSearchDto() {
    }

    public FactSearchDto(FactSearchDto factSearchDto) {
        this.encounterNum = factSearchDto.getEncounterNum();
        this.patientNum = factSearchDto.getPatientNum();
        this.patientId = factSearchDto.getPatientId();
        this.conceptCode = factSearchDto.getConceptCode();
        this.providerId = factSearchDto.getProviderId();
        this.startDate = factSearchDto.getStartDate();
        this.modifierFlag = factSearchDto.getModifierFlag();
        this.pageableDto = factSearchDto.getPageableDto();
    }

    public Integer getEncounterNum() {
        return encounterNum;
    }

    public FactSearchDto setEncounterNum(Integer encounterNum) {
        this.encounterNum = encounterNum;
        return this;
    }

    public Integer getPatientNum() {
        return patientNum;
    }

    public FactSearchDto setPatientNum(Integer patientNum) {
        this.patientNum = patientNum;
        return this;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public FactSearchDto setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
        return this;
    }

    public String getProviderId() {
        return providerId;
    }

    public FactSearchDto setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public FactSearchDto setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
        return this;
    }

    public boolean getModifierFlag() {
        return modifierFlag;
    }

    public void setModifierFlag(boolean modifierFlag) {
        this.modifierFlag = modifierFlag;
    }

    public PageableDto getPageableDto() {
        return pageableDto;
    }

    public FactSearchDto setPageableDto(PageableDto pageableDto) {
        this.pageableDto = pageableDto;
        return this;
    }
}
