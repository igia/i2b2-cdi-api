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

import io.igia.i2b2.cdi.I2b2Application;
import io.igia.i2b2.cdi.observation.dto.FactDto;
import io.igia.i2b2.cdi.observation.dto.FactModifierDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = I2b2Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class FactResourceIT {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testAddObservation_vitals() throws Exception {

        FactDto factDto = new FactDto();
        factDto.setEncounterId("E1");
        factDto.setPatientId("P1");
        factDto.setProviderId("LCS-I2B2:D000109075");
        factDto.setConceptCode("NDC:53489051010");
        factDto.setStartDate(LocalDateTime.parse("2018-03-31T00:00:00"));

        ResponseEntity<FactDto> response = testRestTemplate
            .withBasicAuth("test", "test")
            .exchange("/api/facts", HttpMethod.POST, new HttpEntity<>(factDto),
                FactDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getModifiers()).isNotNull().isEmpty();
    }

    @Test
    public void testAddObservation_withModifiers() throws Exception {

        FactDto factDto = new FactDto();
        factDto.setEncounterId("E2");
        factDto.setPatientId("P2");
        factDto.setProviderId("LCS-I2B2:D000109075");
        factDto.setConceptCode("NDC:53489051010");
        factDto.setStartDate(LocalDateTime.parse("2018-03-31T00:00:00"));
        factDto.setEndDate(LocalDateTime.parse("2018-03-31T00:00:00"));
        factDto.addModifier(new FactModifierDto().setModifierCode("MED:FREQ").setValue("QD"));
        factDto.addModifier(new FactModifierDto().setModifierCode("MED:DOSE").setValue("50").setUnits("mg"));
        factDto.addModifier(new FactModifierDto().setModifierCode("MED:ROUTE").setValue("PO"));
        factDto.addModifier(new FactModifierDto().setModifierCode("MED:SIG").setValue("1 Tablet (50 mg) PO QS PRN Insomnia"));
        factDto.addModifier(new FactModifierDto().setModifierCode("MED:INST").setValue("Take 1 (one) 50 mg tablet by mouth at bedtime as needed for insomnia"));

        ResponseEntity<FactDto> response = testRestTemplate
            .withBasicAuth("test", "test")
            .exchange("/api/facts", HttpMethod.POST, new HttpEntity<>(factDto),
                FactDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getModifiers()).isNotNull().isNotEmpty().size().isEqualTo(5);
    }
}
