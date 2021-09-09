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

import io.igia.i2b2.cdi.common.dto.Operator;

public class PathFilterDto {

    private String path;
    private Operator opertaor;

    public PathFilterDto() {

    }

    public PathFilterDto(PathFilterDto conceptSearchDto) {
        this.path = conceptSearchDto.getPath();
        this.opertaor = conceptSearchDto.getOpertaor();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Operator getOpertaor() {
        return opertaor;
    }

    public void setOpertaor(Operator opertaor) {
        this.opertaor = opertaor;
    }
}
