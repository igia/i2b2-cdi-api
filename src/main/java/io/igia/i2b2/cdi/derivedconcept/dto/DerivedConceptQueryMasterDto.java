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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Derived Concept Query Master")
public class DerivedConceptQueryMasterDto {
    @ApiModelProperty(value = "Id")
    private int id;

    @ApiModelProperty(value = "Name")
    private String name;

    @ApiModelProperty(value = "Generated Sql")
    private String generatedSql;

    @ApiModelProperty(value = "Created Date")
    private Instant createdDate;

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getGeneratedSql() {
	return generatedSql;
    }

    public void setGeneratedSql(String generatedSql) {
	this.generatedSql = generatedSql;
    }

    public Instant getCreatedDate() {
	return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
	this.createdDate = createdDate;
    }
}
