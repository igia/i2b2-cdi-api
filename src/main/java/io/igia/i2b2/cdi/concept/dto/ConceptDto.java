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

package io.igia.i2b2.cdi.concept.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.igia.i2b2.cdi.observation.domain.ValueTypeCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Concept")
public class ConceptDto {

    @ApiModelProperty(value = "Concept path")
    private String conceptPath;
    @ApiModelProperty(value = "Concept code")
    private String code;
    @ApiModelProperty(value = "Concept name")
    private String name;
    @ApiModelProperty(value = "Source system code")
    private String source;
    private LocalDateTime updateTime;

    @JsonIgnore
    private ConceptDataType dataType;
    @ApiModelProperty(value = "Metadata")
    private String metadata;

    @ApiModelProperty(value = "Data type")
    private ValueTypeCode valueType;

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getConceptPath() {
	return conceptPath;
    }

    public void setConceptPath(String conceptPath) {
	this.conceptPath = conceptPath;
    }

    public String getSource() {
	return source;
    }

    public void setSource(String source) {
	this.source = source;
    }

    public ConceptDataType getDataType() {
	return dataType;
    }

    public void setDataType(ConceptDataType dataType) {
	this.dataType = dataType;
    }

    public LocalDateTime getUpdateTime() {
	return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
	this.updateTime = updateTime;
    }

    public String getMetadata() {
	return metadata;
    }

    public void setMetadata(String metadata) {
	this.metadata = metadata;
    }

    public ValueTypeCode getValueType() {
	return valueType;
    }

    public void setValueType(ValueTypeCode valueType) {
	this.valueType = valueType;
    }

}
