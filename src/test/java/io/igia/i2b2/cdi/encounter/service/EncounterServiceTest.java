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

package io.igia.i2b2.cdi.encounter.service;

import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.config.ApplicationProperties;
import io.igia.i2b2.cdi.encounter.dao.EncounterDao;
import io.igia.i2b2.cdi.encounter.dto.EncounterDto;
import io.igia.i2b2.cdi.encounter.dto.EncounterSearchDto;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EncounterServiceTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private EncounterDao encounterDao;

    private EncounterService encounterService;

    @Before
    public void setUp() {
        ApplicationProperties applicationProperties = new ApplicationProperties();
        encounterService = new EncounterServiceImpl(encounterDao, applicationProperties);
    }

    @Test
    public void addEncounter() {

        given(encounterDao.addEncounterMapping(argThat(e -> e.getEncounterId().equals("1"))))
            .willReturn(1);

        given(encounterDao.addEncounter(argThat(e -> e.getEncounterId().equals("1"))))
            .willReturn(1);

        EncounterDto encounter = encounterService.addEncounter(createDefaultEncounter(1, "1"));
        assertThat(encounter).isNotNull();
        assertThat(encounter.getEncounterId()).isNotNull().isEqualTo("1");

        verify(encounterDao, times(1)).addEncounterMapping(argThat(e -> e.getEncounterId().equals("1")));
        verify(encounterDao, times(1)).addEncounter(argThat(e -> e.getEncounterId().equals("1")));
    }

    @Test
    public void addEncounter_minimalData() {

        given(encounterDao.addEncounterMapping(argThat(e -> e.getEncounterId().equals("1"))))
            .willReturn(1);

        given(encounterDao.addEncounter(argThat(e -> e.getEncounterId().equals("1"))))
            .willReturn(1);

        EncounterDto encounter = encounterService.addEncounter(createEncounter(1, "1"));
        assertThat(encounter).isNotNull();
        assertThat(encounter.getEncounterId()).isNotNull().isEqualTo("1");

        verify(encounterDao, times(1)).addEncounterMapping(argThat(e -> e.getEncounterId().equals("1")));
        verify(encounterDao, times(1)).addEncounter(argThat(e -> e.getEncounterId().equals("1")));
    }

    @Test
    public void addEncounter_fail_encounterMapping() {

        given(encounterDao.addEncounterMapping(argThat(e -> e.getEncounterId().equals("1"))))
            .willReturn(0);

        thrown.expect(I2b2Exception.class);
        thrown.expectMessage("Could not add encounter record.");
        encounterService.addEncounter(createEncounter(1, "1"));
        verify(encounterDao, times(1)).addEncounterMapping(argThat(e -> e.getEncounterId().equals("1")));
    }

    @Test
    public void addEncounter_fail_encounter() {

        given(encounterDao.addEncounterMapping(argThat(e -> e.getEncounterId().equals("1"))))
            .willReturn(1);

        given(encounterDao.addEncounter(argThat(e -> e.getEncounterId().equals("1"))))
            .willReturn(0);

        thrown.expect(I2b2Exception.class);
        thrown.expectMessage("Could not add encounter record.");
        encounterService.addEncounter(createEncounter(1, "1"));
        verify(encounterDao, times(1)).addEncounterMapping(argThat(e -> e.getEncounterId().equals("1")));
        verify(encounterDao, times(1)).addEncounter(argThat(e -> e.getEncounterId().equals("1")));
    }

    @Test
    public void testGetProviders() {
        List<EncounterDto> encounters = Arrays.asList(
            createEncounter(1, "1"), createEncounter(2, "2"));
        given(encounterDao.findEncounters(any())).willReturn(encounters);

        List<EncounterDto> actualEncounters = encounterService.getEncounters(new EncounterSearchDto());

        assertThat(actualEncounters).isNotNull().isNotEmpty().size().isEqualTo(2);
        assertThat(actualEncounters.get(0)).isEqualToComparingFieldByField(encounters.get(0));
        assertThat(actualEncounters.get(1)).isEqualToComparingFieldByField(encounters.get(1));
    }
    
    @Test
    public void testGetEncounterByEncounterNum() {
        List<EncounterDto> encounters = Arrays.asList(createEncounter(1, "1"));
        given(encounterDao.findEncounterByEncounterNum(any())).willReturn(encounters);

        List<EncounterDto> actualEncounters = encounterService.getEncounterByEncounterNum(new EncounterSearchDto());

        assertThat(actualEncounters).isNotNull().isNotEmpty().size().isEqualTo(1);
        assertThat(actualEncounters.get(0)).isEqualToComparingFieldByField(encounters.get(0));
    }

    private EncounterDto createEncounter(
        Integer encounterNum, String encounterId) {
        return createDetailedEncounter(encounterNum, encounterId, null, null,
            1, "1", null, null, null);
    }

    private EncounterDto createDefaultEncounter(
        Integer encounterNum, String encounterId) {
        return createDetailedEncounter(encounterNum, encounterId, "src", "A",
            1, "1", "i2b2","pr1", "demo");
    }

    private EncounterDto createDetailedEncounter(Integer encounterNum, String encounterId, String encounterSource,
                                                 String encounterStatus, Integer patientNum, String patientId,
                                                 String patientSource, String projectId, String source) {
        EncounterDto encounter = new EncounterDto();
        encounter.setEncounterNum(encounterNum);
        encounter.setEncounterId(encounterId);
        encounter.setEncounterSource(encounterSource);
        encounter.setEncounterStatus(encounterStatus);
        encounter.setPatientNum(patientNum);
        encounter.setPatientId(patientId);
        encounter.setPatientSource(patientSource);
        encounter.setProjectId(projectId);
        encounter.setSource(source);
        return encounter;
    }
}
