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

public enum ConceptDataType {
    STRING("string"),
    FLOAT("float"),
    ENUM("enum"),
    INTEGER("integer"),
    POS_FLOAT("posfloat"),
    POS_INTEGER("posinteger"),
    LARGE_STRING("largestring");

    private String code;

    ConceptDataType(String code) {
        this.code = code;
    }

    public static ConceptDataType fromCode(String code) {
        for (ConceptDataType conceptDataType : ConceptDataType.values()) {
            if (conceptDataType.getCode().equalsIgnoreCase(code)) {
                return conceptDataType;
            }
        }
        return STRING;
    }

    public String getCode() {
        return code;
    }
}
