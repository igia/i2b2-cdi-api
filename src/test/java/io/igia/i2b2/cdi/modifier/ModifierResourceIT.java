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

import io.igia.i2b2.cdi.I2b2Application;
import io.igia.i2b2.cdi.modifier.dto.ModifierDto;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = I2b2Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class ModifierResourceIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testGetAllModifiers() throws Exception {

        ResponseEntity<List<ModifierDto>> response = testRestTemplate
            .withBasicAuth("test", "test")
            .exchange(
                "/api/modifiers", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ModifierDto>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isNotEmpty().size().isEqualTo(75);

        ModifierDto expectedModifier = createModifier("0", "Admit Diagnosis", "\\Admit Diagnosis\\", "DEMO");
        assertThat(response.getBody().get(0)).isEqualToComparingFieldByField(expectedModifier);
    }

    @Test
    public void testGetAllModifiers_filterByConceptCode_and_filterBySource() throws Exception {

        ResponseEntity<List<ModifierDto>> response = testRestTemplate
            .withBasicAuth("test", "test")
            .exchange(
                "/api/modifiers?source=DEMO&conceptCode=ICD9:160", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<ModifierDto>>() {
                });

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().isNotEmpty().size().isEqualTo(39);

        ModifierDto expectedModifier = createModifier("0", "Admit Diagnosis", "\\Admit Diagnosis\\", "DEMO");
        assertThat(response.getBody().get(0)).isEqualToComparingFieldByField(expectedModifier);
    }

    private ModifierDto createModifier(String code, String name, String path, String source) {
        ModifierDto modifierDto = new ModifierDto();
        modifierDto.setCode(code);
        modifierDto.setName(name);
        modifierDto.setModifierPath(path);
        modifierDto.setSource(source);
        return modifierDto;
    }
}
