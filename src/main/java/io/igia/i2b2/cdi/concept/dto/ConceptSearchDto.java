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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.igia.i2b2.cdi.common.dto.PageableDto;

public class ConceptSearchDto {
    private String source;
    private String code;
    private PathFilterDto pathFilterDto;
    private PageableDto pageableDto;
    private List<String> conceptPaths = new ArrayList<>();

    public ConceptSearchDto() {

    }

    public ConceptSearchDto(ConceptSearchDto conceptSearchDto) {
        this.source = conceptSearchDto.getSource();
        this.code = conceptSearchDto.getCode();
        this.pathFilterDto = conceptSearchDto.getPathFilterDto();
        this.pageableDto = conceptSearchDto.getPageableDto();
        this.conceptPaths = conceptSearchDto.getConceptPaths();
    }

    public String getSource() {
        return source;
    }

    public ConceptSearchDto setSource(String source) {
        this.source = source;
        return this;
    }

    public String getCode() {
        return code;
    }

    public ConceptSearchDto setCode(String code) {
        this.code = code;
        return this;
    }

    public PathFilterDto getPathFilterDto() {
        return pathFilterDto;
    }

    public void setPathFilterDto(PathFilterDto pathFilterDto) {
        this.pathFilterDto = pathFilterDto;
    }

    public PageableDto getPageableDto() {
        return pageableDto;
    }

    public void setPageableDto(PageableDto pageableDto) {
        this.pageableDto = pageableDto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ConceptSearchDto that = (ConceptSearchDto) o;
        return Objects.equals(source, that.source) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, code);
    }

    public List<String> getConceptPaths() {
        return conceptPaths;
    }

    public void setConceptPaths(List<String> conceptPaths) {
        this.conceptPaths = conceptPaths;
    }
}
