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


package io.igia.i2b2.cdi.derivedconcept;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsFetchType;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsSearchDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptQueryMasterDto;
import io.igia.i2b2.cdi.derivedconcept.service.DerivedConceptService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(tags = "Derived Concepts")
@RestController
@RequestMapping("/api")
public class DerivedConceptResource {

    private final DerivedConceptService derivedConceptService;

    public DerivedConceptResource(DerivedConceptService derivedConceptService) {
	this.derivedConceptService = derivedConceptService;
    }

    @ApiOperation(value = "Get derived concept by Id", notes = "Get derived concept")
    @GetMapping(value = "/derived-concepts/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DerivedConceptDto> getDerivedConceptById(@PathVariable(name = "id") Integer id) {
	DerivedConceptDto derivedConcept = derivedConceptService.getDerivedConceptById(id);
	return ResponseEntity.ok(derivedConcept);
    }

    @ApiOperation(value = "Get derived concepts", notes = "Get all derived concepts")
    @GetMapping(value = "/derived-concepts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<DerivedConceptDto>> getAllDerivedConcepts() {
	List<DerivedConceptDto> derivedConcepts = derivedConceptService.getDerivedConcepts();
	return ResponseEntity.ok(derivedConcepts);
    }

    @ApiOperation(value = "Add derived concept", notes = "Create derived concept")
    @PostMapping(value = "/derived-concepts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DerivedConceptDto> createDerivedConcept(@RequestBody DerivedConceptDto derivedConcept) {
	DerivedConceptDto derivedConceptResponse = derivedConceptService.addDerivedConcept(derivedConcept);
	return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri())
		.body(derivedConceptResponse);
    }

    @ApiOperation(value = "Update derived concept", notes = "Update derived concept")
    @PutMapping(value = "/derived-concepts/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DerivedConceptDto> updateDerivedConcept(@PathVariable(name = "id") Integer id,
	    @RequestBody DerivedConceptDto derivedConcept) {
	derivedConcept.setId(id);
	DerivedConceptDto updatedDerivedConcept = derivedConceptService.updateDerivedConcept(derivedConcept);
	return ResponseEntity.ok(updatedDerivedConcept);
    }

    @ApiOperation(value = "Delete derived concept", notes = "remove derived concept")
    @DeleteMapping(value = "/derived-concepts/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DerivedConceptDto> deleteDerivedConcept(@PathVariable("id") Integer id) {
	DerivedConceptDto derivedConcept = new DerivedConceptDto();
	derivedConcept.setId(id);
	DerivedConceptDto deletedDerivedConcept = derivedConceptService.deleteDerivedConcept(derivedConcept);
	return ResponseEntity.ok(deletedDerivedConcept);
    }

    @ApiOperation(value = "Calculate all derived concepts", notes = "Calculate all derived concepts")
    @PostMapping(value = "/derived-concepts/calculate-facts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> calculateAllDerivedConcepts() {
	derivedConceptService.calculateDerivedConcept(null);
	return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()).body("");
    }

    @ApiOperation(value = "Calculate derived concept", notes = "Calculate derived concept")
    @PostMapping(value = "/derived-concepts/{id}/calculate-facts", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Object> calculateDerivedConcept(@PathVariable("id") Integer id) {
	derivedConceptService.calculateDerivedConcept(id);
	return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().build().toUri()).body("");
    }

    @ApiOperation(value = "Get derived concepts job execution details", notes = "Get derived concepts job execution details")
    @GetMapping(value = "/derived-concepts/jobs", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<DerivedConceptJobDetailsDto> getDerivedConceptJobDetails(
	    @RequestParam(name = "derivedConceptId", required = false) List<Integer> derivedConceptIds,
	    @RequestParam(name = "fetchType", required = false) DerivedConceptJobDetailsFetchType fetchType) {
	DerivedConceptJobDetailsSearchDto derivedConceptJobDetailsSearchDto = new DerivedConceptJobDetailsSearchDto();
	derivedConceptJobDetailsSearchDto.setDerivedConceptIds(derivedConceptIds);
	derivedConceptJobDetailsSearchDto.setDerivedConceptJobDetailsFetchType(fetchType);
	return derivedConceptService.getDerivedConceptJobDetails(derivedConceptJobDetailsSearchDto);
    }

    @ApiOperation(value = "Get queries from query master", notes = "Get queries from query master")
    @GetMapping(value = "/derived-concepts/querymaster", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<DerivedConceptQueryMasterDto>> getQueryMasters(
	    @RequestParam(name = "fetchSize", required = false) Integer fetchSize) {
	return ResponseEntity.ok(derivedConceptService.getQueryMasterRecords(fetchSize));
    }
}
