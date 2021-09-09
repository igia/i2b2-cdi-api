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



package io.igia.i2b2.cdi.provider;

import io.igia.i2b2.cdi.provider.dto.ProviderDto;
import io.igia.i2b2.cdi.provider.dto.ProviderSearchDto;
import io.igia.i2b2.cdi.provider.service.ProviderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Providers")
@RestController
@RequestMapping("/api")
public class ProviderResource {

    private final ProviderService providerService;

    public ProviderResource(ProviderService providerService) {
        this.providerService = providerService;
    }

    @ApiOperation(value = "Get providers", notes = "Get all providers")
    @GetMapping(path = "/providers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<ProviderDto> getAllProviders(
        @ApiParam(value = "source system code", required = false)
        @RequestParam(value = "source", required = false) String sourceSystemCode) {
        ProviderSearchDto searchDto = new ProviderSearchDto();
        searchDto.setSource(sourceSystemCode);
        return providerService.getProviders(searchDto);
    }
}
