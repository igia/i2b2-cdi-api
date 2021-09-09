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

package io.igia.i2b2.cdi.encounter.dao;

import io.igia.i2b2.cdi.encounter.dto.EncounterDto;
import io.igia.i2b2.cdi.encounter.dto.EncounterSearchDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan({"io.igia.i2b2.cdi.encounter.dao", "io.igia.i2b2.cdi.common.database"})
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb"})
@DirtiesContext
@Sql({"/test-schema.sql", "/test-encounter-data.sql"})
public class EncounterDaoTest {

    @Autowired
    private EncounterDao encounterDao;

    @Test
    public void findEncounters() {
        List<EncounterDto> encounters = encounterDao.findEncounters(new EncounterSearchDto());
        assertThat(encounters).isNotNull().isNotEmpty().size().isGreaterThanOrEqualTo(3);
    }

    @Test
    public void findEncounters_noRecords() {
        List<EncounterDto> encounters = encounterDao.findEncounters(new EncounterSearchDto().setEncounterId("4"));
        assertThat(encounters).isNotNull().isEmpty();
    }

    @Test
    public void findEncounters_filterByEncounterId() {
        List<EncounterDto> encounters = encounterDao.findEncounters(new EncounterSearchDto().setEncounterId("1"));
        assertThat(encounters).isNotNull().isNotEmpty().size().isEqualTo(1);
        EncounterDto expectedEncounter = createEncounter(1, "1");
        assertThat(encounters.get(0)).isNotNull().isEqualToComparingFieldByField(expectedEncounter);
    }
    
    @Test
    public void findEncounterFilterByEncounterNum() {
        List<EncounterDto> encounters = encounterDao
                .findEncounterByEncounterNum(new EncounterSearchDto().setEncounterNum(1));
        assertThat(encounters).isNotNull().isNotEmpty().size().isEqualTo(1);
        EncounterDto expectedEncounter = createEncounter(1, "1");
        assertThat(encounters.get(0)).isNotNull().isEqualToComparingFieldByField(expectedEncounter);
    }

    @Test
    public void findEncounters_filterByEncounterId_filterByProjectId_filterBySource() {
        List<EncounterDto> encounters = encounterDao.findEncounters(new EncounterSearchDto()
            .setEncounterId("3").setProjectId("PR1").setSource("TEST"));
        assertThat(encounters).isNotNull().isNotEmpty().size().isEqualTo(1);
        EncounterDto expectedEncounter = createEncounter(3, "3");
        assertThat(encounters.get(0)).isNotNull().isEqualToComparingFieldByField(expectedEncounter);
    }

    @Test
    public void getNextEncounterNumber() {
        Integer encounterNum = encounterDao.getNextEncounterNumber();
        assertThat(encounterNum).isNotNull().isEqualTo(1);

        assertThat(encounterDao.getNextEncounterNumber()).isNotNull().isEqualTo(2);
    }

    @Test
    public void addEncounterMapping() {
        int status = encounterDao.addEncounterMapping(createDetailedEncounter(4, "4",
            "i2b2", "A", 1, "1", "i2b2", "pr1", "demo" ));
        assertThat(status).isEqualTo(1);
    }

    @Test
    public void addEncounter() {
        int status = encounterDao.addEncounter(createDefaultEncounter(4, "4"));
        assertThat(status).isEqualTo(1);
    }

    private EncounterDto createEncounter(
        Integer encounterNum, String encounterId) {
        return createDetailedEncounter(encounterNum, encounterId, null, null,
            null, null, null, null, null);
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
