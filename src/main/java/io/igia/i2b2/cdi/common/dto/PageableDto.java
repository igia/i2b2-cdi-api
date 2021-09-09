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

package io.igia.i2b2.cdi.common.dto;

import org.springframework.util.StringUtils;

public class PageableDto {
    private int page = 1;
    private int size = 20;
    private SortOrder sortOrder = SortOrder.ASC;
    private String sortBy = "id";

    public PageableDto() {
    }

    public PageableDto(PageableDto pageableDto) {
        this.page = pageableDto.getPage();
        this.size = pageableDto.getSize();
        if (pageableDto.getSortOrder() != null) {
            this.sortOrder = pageableDto.getSortOrder();
        }
        if (!StringUtils.isEmpty(pageableDto.getSortBy())) {
            this.sortBy = pageableDto.getSortBy();
        }
    }

    public PageableDto(Integer page, Integer size, SortOrder sortOrder, String sortBy) {        
        
        this.page = (page != null) ? page : 1;
        this.size = (size != null) ? size : 20;        
        this.sortOrder = (sortOrder != null) ? sortOrder : SortOrder.ASC;
        this.sortBy = (sortBy != null) ? sortBy : "id";
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public String toString() {
        return "PageableDto [page=" + page + ", size=" + size + ", sortOrder=" + sortOrder + ", sortBy=" + sortBy + "]";
    }
}
