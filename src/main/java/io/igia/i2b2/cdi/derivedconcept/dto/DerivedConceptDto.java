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
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.igia.i2b2.cdi.common.dto.WarningDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Derived Concept")
public class DerivedConceptDto {
    @ApiModelProperty(value = "Id")
    private Integer id;
    @ApiModelProperty(value = "Concept path")
    private String path;
    @ApiModelProperty(value = "Concept code")
    private String code;
    @ApiModelProperty(value = "Concept type")
    private DerivedConceptType type;
    @ApiModelProperty(value = "Metadata Xml")
    private String metadata;
    @ApiModelProperty(value = "Concept description")
    private String description;
    @ApiModelProperty(value = "Derived fact query")
    private String factQuery;
    @ApiModelProperty(value = "Unit")
    private String unit;
    @ApiModelProperty(value = "Updated On")
    private Instant updatedOn;

    @ApiModelProperty(value = "Derived Concept Dependencies")
    private List<DependencyDto> dependencies = new ArrayList<>();

    @ApiModelProperty(value = "Cyclic Dependency Warning")
    private List<WarningDto> warnings = new ArrayList<>();

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public String getCode() {
	return code;
    }

    public void setCode(String code) {
	this.code = code;
    }

    public DerivedConceptType getType() {
	return type;
    }

    public void setType(DerivedConceptType type) {
	this.type = type;
    }

    public String getMetadata() {
	return metadata;
    }

    public void setMetadata(String metadata) {
	this.metadata = metadata;
    }

    public String getDescription() {
	return description;
    }

    public void setDescription(String description) {
	this.description = description;
    }

    public String getFactQuery() {
	return factQuery;
    }

    public void setFactQuery(String factQuery) {
	this.factQuery = factQuery;
    }

    public String getUnit() {
	return unit;
    }

    public void setUnit(String unit) {
	this.unit = unit;
    }

    public List<DependencyDto> getDependencies() {
	return dependencies;
    }

    public void setDependencies(List<DependencyDto> dependencies) {
	this.dependencies = dependencies;
    }

    @JsonInclude(value = Include.NON_NULL)
    public List<WarningDto> getWarnings() {
	return warnings;
    }

    public void setWarnings(List<WarningDto> warnings) {
	this.warnings = warnings;
    }

    public Instant getUpdatedOn() {
	return updatedOn;
    }

    public void setUpdatedOn(Instant updatedOn) {
	this.updatedOn = updatedOn;
    }

    public void addWarning(WarningDto warning) {
	if (this.warnings == null) {
	    this.warnings = new ArrayList<>();
	}
	warnings.add(warning);
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((code == null) ? 0 : code.hashCode());
	result = prime * result + ((path == null) ? 0 : path.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	DerivedConceptDto other = (DerivedConceptDto) obj;
	if (code == null) {
	    if (other.code != null)
		return false;
	} else if (!code.equals(other.code))
	    return false;
	if (path == null) {
	    if (other.path != null)
		return false;
	} else if (!path.equals(other.path))
	    return false;
	return true;
    }

}
