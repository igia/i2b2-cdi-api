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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import io.igia.i2b2.cdi.I2b2Application;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.observation.domain.ValueTypeCode;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = I2b2Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ConceptResourceIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testGetAllConcepts() throws Exception {

	ResponseEntity<List<ConceptDto>> response = testRestTemplate.withBasicAuth("test", "test")
		.exchange("/api/concepts", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConceptDto>>() {
		});

	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	assertThat(response.getBody()).isNotNull().isNotEmpty().size().isEqualTo(20);

	ConceptDto expectedConcept = createConcept("birn:cdr1", "mild dementia",
		"\\BIRN\\oasis\\Clinical Measures\\Clinical Dementia Rating (CDR)\\mild dementia\\", "OASIS");
	assertThat(response.getBody().get(0)).isEqualToComparingFieldByField(expectedConcept);
    }

    @Test
    public void testGetAllConcepts_filterBySourceSystemCode() throws Exception {

	ResponseEntity<List<ConceptDto>> response = testRestTemplate.withBasicAuth("test", "test").exchange(
		"/api/concepts?source=DEMO", HttpMethod.GET, null, new ParameterizedTypeReference<List<ConceptDto>>() {
		});

	assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	assertThat(response.getBody()).isNotNull().isNotEmpty().size().isEqualTo(20);

	ConceptDto expectedConcept = createConcept("DEM|AGE:0", "0 years old",
		"\\i2b2\\Demographics\\Age\\0-9 years old\\0 years old\\", "DEMO");
	assertThat(response.getBody().get(0)).isEqualToComparingFieldByField(expectedConcept);
    }

    private ConceptDto createConcept(String code, String name, String path, String source) {
	ConceptDto conceptDto = new ConceptDto();
	conceptDto.setCode(code);
	conceptDto.setName(name);
	conceptDto.setConceptPath(path);
	conceptDto.setSource(source);
	conceptDto.setValueType(ValueTypeCode.TEXT);
	return conceptDto;
    }
}
