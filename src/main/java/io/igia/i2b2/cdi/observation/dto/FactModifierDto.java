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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.igia.i2b2.cdi.observation.domain.ValueTypeCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "FactModifier")
public class FactModifierDto {

    @ApiModelProperty(value = "Modifier code in the i2b2 data mart")
    private String modifierCode;
    @ApiModelProperty(value = "Value recorded")
    private String value;
    @ApiModelProperty(value = "Units")
    private String units;

    @JsonIgnore
    private String textValue;
    @JsonIgnore
    private Double numberValue;
    @JsonIgnore
    private ValueTypeCode valueTypeCode;
    @JsonIgnore
    private String blob;

    public FactModifierDto() {
    }

    public FactModifierDto(FactModifierDto modifier) {
        setModifierCode(modifier.getModifierCode());
        setValue(modifier.getValue());
        setUnits(modifier.getUnits());
    }

    public String getModifierCode() {
        return modifierCode;
    }

    public FactModifierDto setModifierCode(String modifierCode) {
        this.modifierCode = modifierCode;
        return this;
    }

    public String getValue() {
        return value;
    }

    public FactModifierDto setValue(String value) {
        this.value = value;
        return this;
    }

    public String getUnits() {
        return units;
    }

    public FactModifierDto setUnits(String units) {
        this.units = units;
        return this;
    }

    public String getTextValue() {
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public Double getNumberValue() {
        return numberValue;
    }

    public void setNumberValue(Double numberValue) {
        this.numberValue = numberValue;
    }

    public ValueTypeCode getValueTypeCode() {
        return valueTypeCode;
    }

    public void setValueTypeCode(ValueTypeCode valueTypeCode) {
        this.valueTypeCode = valueTypeCode;
    }

    public String getBlob() {
        return blob;
    }

    public void setBlob(String blob) {
        this.blob = blob;
    }
}
