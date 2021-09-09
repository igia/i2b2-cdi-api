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


package io.igia.i2b2.cdi.observation.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Observation {

    private Integer encounterNum;
    private Integer patientNum;
    private String conceptCode;
    private String providerId;
    private LocalDateTime startDate;
    private Integer instanceNumber;
    private LocalDateTime endDate;
    private String sourceSystemCode;
    private List<ObservationModifier> modifiers = new ArrayList<>();

    public Observation() {
    }

    public Observation(Integer encounterNum, Integer patientNum, String conceptCode, String providerId, LocalDateTime startDate) {
        this.encounterNum = encounterNum;
        this.patientNum = patientNum;
        this.conceptCode = conceptCode;
        this.providerId = providerId;
        this.startDate = startDate;
    }

    public Integer getEncounterNum() {
        return encounterNum;
    }

    public void setEncounterNum(Integer encounterNum) {
        this.encounterNum = encounterNum;
    }

    public Integer getPatientNum() {
        return patientNum;
    }

    public void setPatientNum(Integer patientNum) {
        this.patientNum = patientNum;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Integer getInstanceNumber() {
        return instanceNumber;
    }

    public void setInstanceNumber(Integer instanceNumber) {
        this.instanceNumber = instanceNumber;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getSourceSystemCode() {
        return sourceSystemCode;
    }

    public void setSourceSystemCode(String sourceSystemCode) {
        this.sourceSystemCode = sourceSystemCode;
    }

    public List<ObservationModifier> getModifiers() {
        return modifiers;
    }

    public void addModifier(ObservationModifier modifier) {
        this.modifiers.add(modifier);
    }
}
