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


package io.igia.i2b2.cdi.provider.dto;

public class ProviderSearchDto {
    private String source;
    private String providerId;

    public ProviderSearchDto() {

    }

    public ProviderSearchDto(ProviderSearchDto providerSearchDto) {
        this.source = providerSearchDto.getSource();
        this.providerId = providerSearchDto.getProviderId();
    }

    public String getSource() {
        return source;
    }

    public ProviderSearchDto setSource(String source) {
        this.source = source;
        return this;
    }

    public String getProviderId() {
        return providerId;
    }

    public ProviderSearchDto setProviderId(String providerId) {
        this.providerId = providerId;
        return this;
    }
}
