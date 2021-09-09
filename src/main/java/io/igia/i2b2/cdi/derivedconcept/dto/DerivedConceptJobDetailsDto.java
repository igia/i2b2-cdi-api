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


package io.igia.i2b2.cdi.derivedconcept.dto;

import java.time.Instant;

public class DerivedConceptJobDetailsDto {

    private Integer id;
    private Integer derivedConceptId;
    private String errorStack;
    private String derivedConceptSql;
    private Status status;
    private Instant startedOn;
    private Instant completedOn;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public Integer getDerivedConceptId() {
	return derivedConceptId;
    }

    public void setDerivedConceptId(Integer derivedConceptId) {
	this.derivedConceptId = derivedConceptId;
    }

    public String getErrorStack() {
	return errorStack;
    }

    public void setErrorStack(String errorStack) {
	this.errorStack = errorStack;
    }

    public String getDerivedConceptSql() {
	return derivedConceptSql;
    }

    public void setDerivedConceptSql(String derivedConceptSql) {
	this.derivedConceptSql = derivedConceptSql;
    }

    public Status getStatus() {
	return status;
    }

    public void setStatus(Status status) {
	this.status = status;
    }

    public Instant getStartedOn() {
	return startedOn;
    }

    public void setStartedOn(Instant startedOn) {
	this.startedOn = startedOn;
    }

    public Instant getCompletedOn() {
	return completedOn;
    }

    public void setCompletedOn(Instant completedOn) {
	this.completedOn = completedOn;
    }
}