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


package io.igia.i2b2.cdi.modifier;

import io.igia.i2b2.cdi.modifier.dto.ModifierDto;
import io.igia.i2b2.cdi.modifier.dto.ModifierSearchDto;
import io.igia.i2b2.cdi.modifier.service.ModifierService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "Modifiers")
@RestController
@RequestMapping("/api")
public class ModifierResource {

    private final ModifierService modifierService;

    public ModifierResource(ModifierService modifierService) {
        this.modifierService = modifierService;
    }

    @ApiOperation(value = "Get modifiers", notes = "Get all modifiers")
    @GetMapping(value = "/modifiers", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<ModifierDto> getAllModifiers(
        @ApiParam(value = "source system code", required = false)
        @RequestParam(value = "source", required = false) String sourceSystemCode,
        @ApiParam(value = "concept code", required = false)
        @RequestParam(value = "conceptCode", required = false) String conceptCode
    ) {
        ModifierSearchDto searchDto = new ModifierSearchDto();
        searchDto.setSource(sourceSystemCode);
        searchDto.setConceptCode(conceptCode);
        return modifierService.getModifiers(searchDto);
    }
}
