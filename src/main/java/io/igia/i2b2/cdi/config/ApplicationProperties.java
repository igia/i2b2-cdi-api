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



package io.igia.i2b2.cdi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application")
@Component
public class ApplicationProperties {
    private boolean lenientValidation;
    private String sourceSystemCode;
    private String projectId;
    private String patientSource;
    private String patientStatus;
    private String encounterSource;
    private String encounterStatus;

    public boolean isLenientValidation() {
        return lenientValidation;
    }

    public void setLenientValidation(boolean lenientValidation) {
        this.lenientValidation = lenientValidation;
    }

    public String getSourceSystemCode() {
        return sourceSystemCode;
    }

    public void setSourceSystemCode(String sourceSystemCode) {
        this.sourceSystemCode = sourceSystemCode;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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
}
