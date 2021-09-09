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
package io.igia.i2b2.cdi.concept;

import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.igia.i2b2.cdi.common.dto.Operator;
import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.common.dto.SortOrder;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.dto.ConceptSearchDto;
import io.igia.i2b2.cdi.concept.dto.PathFilterDto;
import io.igia.i2b2.cdi.concept.service.ConceptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@Api(tags = "Concepts")
@RestController
@RequestMapping("/api")
public class ConceptResource {

    private final ConceptService conceptService;

    public ConceptResource(ConceptService conceptService) {
	this.conceptService = conceptService;
    }

    @ApiOperation(value = "Get concepts", notes = "Get all concepts")
    @GetMapping(value = "/concepts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<ConceptDto>> getAllConcepts(
	    @ApiParam(value = "source system code", required = false) @RequestParam(value = "source", required = false) String sourceSystemCode,
	    @ApiParam(value = "concept path [contains] - Get concepts by wild card. E.g. 'conc' will be treated as '%conc%'", required = false) @RequestParam(value = "conceptPath[contains]", required = false) String conceptPathContains,
	    @ApiParam(value = "concept path [starts with] - Get concepts by level. E.g. `\\`, to get children concepts having level 0 and 1, And `\\concept\\` will give next childrens of `\\concept\\` having level 2, so on", required = false) @RequestParam(value = "conceptPath[startsWith]", required = false) String conceptPathStartsWith,
	    @RequestParam(name = "page", required = false) Integer page,
	    @RequestParam(name = "size", required = false) Integer size,
	    @ApiParam(value = "sort order", required = false) @RequestParam(name = "order", required = false) SortOrder order,
	    @ApiParam(value = "sort field", required = false) @RequestParam(name = "sort", required = false) String sort) {
	ConceptSearchDto searchDto = new ConceptSearchDto();
	searchDto.setSource(sourceSystemCode);

	PathFilterDto pathFilterDto = new PathFilterDto();
	if (!StringUtils.isEmpty(conceptPathContains)) {
	    pathFilterDto.setPath(conceptPathContains);
	    pathFilterDto.setOpertaor(Operator.CONTAINS);
	} else if (!StringUtils.isEmpty(conceptPathStartsWith)) {
	    pathFilterDto.setPath(conceptPathStartsWith);
        pathFilterDto.setOpertaor(Operator.STARTSWITH);
	}
	searchDto.setPathFilterDto(pathFilterDto);

	PageableDto pageableDto = new PageableDto(page, size, order, sort);

	searchDto.setPageableDto(pageableDto);
	PaginationResult<ConceptDto> concepts = conceptService.getConcepts(searchDto);
	HttpHeaders responseHeaders = new HttpHeaders();
	responseHeaders.add("X-Total-Count", String.valueOf(concepts.getTotalCount()));
	return new ResponseEntity<>(concepts.getRecords(), responseHeaders, HttpStatus.OK);
    }

    @ApiOperation(value = "Create concept", notes = "Create concept")
    @PostMapping(value = "/concepts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> createConcept(@RequestBody ConceptDto conceptDto) {
	conceptService.createConcept(conceptDto);
	return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()).build();
    }
}
