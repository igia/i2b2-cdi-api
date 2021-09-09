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

public class ObservationModifier {
    private String modifierCode;
    private ValueTypeCode valueTypeCode;
    private String textValue;
    private Double numberValue;
    private String units;
    private String blob;

    public ObservationModifier() {
    }

    public ObservationModifier(String modifierCode, ValueTypeCode valueTypeCode, String textValue,
                               Double numberValue, String units, String blob) {
        this.modifierCode = modifierCode;
        this.valueTypeCode = valueTypeCode;
        this.textValue = textValue;
        this.numberValue = numberValue;
        this.units = units;
        this.blob = blob;
    }

    public String getModifierCode() {
        return modifierCode;
    }

    public void setModifierCode(String modifierCode) {
        this.modifierCode = modifierCode;
    }

    public ValueTypeCode getValueTypeCode() {
        return valueTypeCode;
    }

    public void setValueTypeCode(ValueTypeCode valueTypeCode) {
        this.valueTypeCode = valueTypeCode;
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

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getBlob() {
        return blob;
    }

    public void setBlob(String blob) {
        this.blob = blob;
    }
}
