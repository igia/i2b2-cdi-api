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



package io.igia.i2b2.cdi.observation;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.common.dto.SortOrder;
import io.igia.i2b2.cdi.observation.dto.FactDto;
import io.igia.i2b2.cdi.observation.dto.FactSearchDto;
import io.igia.i2b2.cdi.observation.service.ObservationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "Facts")
@RestController
@RequestMapping("/api")
public class FactResource {

    private final ObservationService observationService;

    public FactResource(ObservationService observationService) {
        this.observationService = observationService;
    }

    @ApiOperation(value = "Add fact", notes = "Add fact")
    @PostMapping(value = "/facts", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<FactDto> addFact(
        @Valid @RequestBody FactDto fact) {
        FactDto result = observationService.addObservation(fact);

        return ResponseEntity.created(ServletUriComponentsBuilder
            .fromCurrentRequest().build().toUri())
            .body(result);
    }
    
    @ApiOperation(value = "Get facts", notes = "Get all facts by pagination")
    @GetMapping(value = "/facts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<FactDto>> getFacts(
            @ApiParam(value = "patient id", required = false) @RequestParam(value = "patientId", required = false) String patientId,
            @ApiParam(value = "concept code", required = false) @RequestParam(value = "conceptCode", required = false) String conceptCode,
            @ApiParam(value = "modifier flag", required = false) @RequestParam(value = "modifierFlag", required = false) boolean modifierFlag,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @ApiParam(value = "sort order", required = false) @RequestParam(name = "order", required = false) SortOrder order,
            @ApiParam(value = "sort field", required = false) @RequestParam(name = "sort", required = false) String sort) {
        FactSearchDto searchDto = new FactSearchDto();
        searchDto.setPatientId(patientId);
        searchDto.setConceptCode(conceptCode);
        searchDto.setModifierFlag(modifierFlag);

        PageableDto pageableDto = new PageableDto(page, size, order, sort);

        searchDto.setPageableDto(pageableDto);
        PaginationResult<FactDto> facts = observationService.getObservations(searchDto);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("X-Total-Count", String.valueOf(facts.getTotalCount()));
        return new ResponseEntity<>(facts.getRecords(), responseHeaders, HttpStatus.OK);
    }
}
