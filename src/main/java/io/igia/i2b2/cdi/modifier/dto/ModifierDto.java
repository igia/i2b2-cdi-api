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



package io.igia.i2b2.cdi.modifier.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Modifier")
public class ModifierDto {

    @ApiModelProperty(value = "Modifier path")
    private String modifierPath;
    @ApiModelProperty(value = "Modifier code")
    private String code;
    @ApiModelProperty(value = "Modifier name")
    private String name;
    @ApiModelProperty(value = "Source system code")
    private String source;

    @JsonIgnore
    private ConceptDataType dataType;

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

    public String getModifierPath() {
        return modifierPath;
    }

    public void setModifierPath(String modifierPath) {
        this.modifierPath = modifierPath;
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
}
