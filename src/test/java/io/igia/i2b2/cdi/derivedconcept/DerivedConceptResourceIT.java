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

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;

import io.igia.i2b2.cdi.I2b2Application;
import io.igia.i2b2.cdi.derivedconcept.dto.DependencyDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptType;
import io.igia.i2b2.cdi.derivedconcept.dto.Status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = I2b2Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Sql({ "/test-ontology-schema.sql", "/test-derivedconcept-schema.sql", "/test-derivedconcept-data.sql" })
@Sql(scripts = "/test-truncate-derivedconcept.sql", executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
public class DerivedConceptResourceIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testGetDerivedConceptById() {
	ResponseEntity<DerivedConceptDto> response = testRestTemplate.withBasicAuth("test", "test").exchange(
		"/api/derived-concepts/1", HttpMethod.GET, null, new ParameterizedTypeReference<DerivedConceptDto>() {
		});

	assertThat(response.getBody()).isNotNull();

	assertThat(response.getBody())
		.isEqualToComparingOnlyGivenFields(createDerivedConcept(1, "\\Derived\\Test1\\", "derived:test1"));
    }

    @Test
    public void testGetAllDerivedConcepts() throws Exception {

	ResponseEntity<List<DerivedConceptDto>> response = testRestTemplate.withBasicAuth("test", "test").exchange(
		"/api/derived-concepts", HttpMethod.GET, null,
		new ParameterizedTypeReference<List<DerivedConceptDto>>() {
		});

	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	assertThat(response.getBody()).isNotNull().isNotEmpty().size().isEqualTo(3);

	DerivedConceptDto expectedDerivedConcept = createDerivedConcept(1, "\\Derived\\Test1\\", "derived:test1",
		"Test description 1", "Select * from table1", "test-unit", Status.PENDING);
	assertThat(response.getBody().get(0)).isEqualToIgnoringGivenFields(expectedDerivedConcept, "updatedOn",
		"dependencies");
    }

    @Test
    public void testCreateDerivedConcept() {
	DerivedConceptDto derivedConcept = createDerivedConcept(4, "\\Derived\\test4\\", "derived:test4");
	derivedConcept.setDependencies(createDerivedConceptDependencies(2, "\\Derived\\dependenttest"));
	ResponseEntity<DerivedConceptDto> response = testRestTemplate.withBasicAuth("test", "test").exchange(
		"/api/derived-concepts", HttpMethod.POST, new HttpEntity<DerivedConceptDto>(derivedConcept),
		DerivedConceptDto.class);

	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	assertThat(response.getBody()).isNotNull().isEqualToIgnoringGivenFields(derivedConcept, "updatedOn",
		"dependencies");

    }

    private DerivedConceptDto createDerivedConcept(Integer id, String path, String code, String description,
	    String factQuery, String unit, Status status) {
	DerivedConceptDto derivedConceptDto = new DerivedConceptDto();
	derivedConceptDto.setId(id);
	derivedConceptDto.setPath(path);
	derivedConceptDto.setCode(code);
	derivedConceptDto.setDescription(description);
	derivedConceptDto.setFactQuery(factQuery);
	derivedConceptDto.setUnit(unit);
	derivedConceptDto.setUpdatedOn(Instant.now());
	return derivedConceptDto;
    }

    private List<DependencyDto> createDerivedConceptDependencies(int size, String path) {
	List<DependencyDto> dependencies = new ArrayList<>();
	DependencyDto derivedConceptDependency = null;
	for (int sequence = 1; sequence <= size; sequence++) {
	    derivedConceptDependency = new DependencyDto();
	    derivedConceptDependency.setPath(path + sequence + "\\");
	    dependencies.add(derivedConceptDependency);
	}
	return dependencies;
    }

    private DerivedConceptDto createDerivedConcept(Integer id, String path, String code) {
	DerivedConceptDto derivedConcept = new DerivedConceptDto();
	derivedConcept.setId(id);
	derivedConcept.setPath(path);
	derivedConcept.setCode(code);
	derivedConcept.setDescription("Test description 1");
	derivedConcept.setFactQuery("Select * from table1");
	derivedConcept.setMetadata("MetaDataXml1");
	derivedConcept.setUnit("test-unit");
	derivedConcept.setType(DerivedConceptType.TEXTUAL);
	derivedConcept.setUpdatedOn(Instant.now());
	return derivedConcept;
    }

    /*
     * @Test public void testUpdateDerivedConcept() { DerivedConceptDto
     * derivedConcept = createDerivedConcept(1,
     * "\\Derived\\Test1\\", "derived:test1"); ResponseEntity<DerivedConceptDto>
     * response = testRestTemplate.withBasicAuth("test", "test").exchange(
     * "/api/derived-concepts/1", HttpMethod.PUT, new
     * HttpEntity<DerivedConceptDto>(derivedConcept), DerivedConceptDto.class);
     * 
     * assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
     * assertThat(response.getBody()).isNotNull().isEqualToIgnoringGivenFields(
     * derivedConcept, "updatedOn");
     * 
     * }
     */
}
