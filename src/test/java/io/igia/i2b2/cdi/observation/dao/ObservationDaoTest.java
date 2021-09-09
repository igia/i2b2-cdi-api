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


package io.igia.i2b2.cdi.observation.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.observation.domain.Observation;
import io.igia.i2b2.cdi.observation.domain.ObservationModifier;
import io.igia.i2b2.cdi.observation.domain.ValueTypeCode;
import io.igia.i2b2.cdi.observation.dto.FactSearchDto;

@RunWith(SpringRunner.class)
@JdbcTest
@ComponentScan({"io.igia.i2b2.cdi.observation.dao", "io.igia.i2b2.cdi.common.database"})
@DirtiesContext
@TestPropertySource(properties = {"spring.datasource.url=jdbc:h2:mem:testdb"})
@Sql({"/test-schema.sql", "/test-fact-data.sql"})
public class ObservationDaoTest {

    @Autowired
    private ObservationDao observationDao;

    @Test
    public void add_defaultModifier() {
        int[] status = observationDao.add(createObservation(1, 1, "1", "1",
            LocalDateTime.now(), Arrays.asList(createNumericModifier("@", 20d, "kg"))));
        assertThat(status).isNotNull().isNotEmpty().hasSize(1);
        assertThat(status[0]).isNotZero().isEqualTo(1);
    }

    @Test
    public void add_multipleModifiers() {
        int[] status = observationDao.add(createObservation(1, 1, "1", "1",
            LocalDateTime.now(), Arrays.asList(
                createNumericModifier("1", 20d, "kg"),
                createNumericModifier("2", 30.12d, "mg"),
                createNumericModifier("3", 50.23121d, "mg")
            )));
        assertThat(status).isNotNull().isNotEmpty().hasSize(3);
        assertThat(status[0]).isNotZero().isEqualTo(1);
        assertThat(status[1]).isNotZero().isEqualTo(1);
        assertThat(status[2]).isNotZero().isEqualTo(1);
    }

    @Test
    public void add_mixedModifiers() {
        int[] status = observationDao.add(createObservation(1, 1, "1", "1",
            LocalDateTime.now(), Arrays.asList(
                createTextModifier("@", ValueTypeCode.NONE, "", null),
                createTextModifier("2", ValueTypeCode.RAW_TEXT, "instructions ", null),
                createTextModifier("3", ValueTypeCode.TEXT, "dose", null),
                createNumericModifier("1", 20d, "kg")
            )));
        assertThat(status).isNotNull().isNotEmpty().hasSize(4);
        assertThat(status[0]).isNotZero().isEqualTo(1);
        assertThat(status[1]).isNotZero().isEqualTo(1);
        assertThat(status[2]).isNotZero().isEqualTo(1);
        assertThat(status[3]).isNotZero().isEqualTo(1);
    }

    @Test
    public void testMaxNegativeEncounterNum() {
        assertThat(observationDao.getNextNegativeEncounterNumber()).isNotNull().isEqualTo(-1);
        assertThat(observationDao.getNextNegativeEncounterNumber()).isNotNull().isEqualTo(-2);
    }

    @Test
    public void testGetNextInstanceNumberForObservationFact_factExists_withAllCriteria() {
        Integer nextInstanceNumber = observationDao.getNextInstanceNumberForObservationFact(
            new FactSearchDto().setStartDate(LocalDateTime.parse("2002-10-04T00:00:00"))
                .setProviderId("provider-1")
                .setPatientNum(1)
                .setEncounterNum(2)
                .setConceptCode("concept-1"));

        assertThat(nextInstanceNumber).isNotNull().isEqualTo(3);
    }

    @Test
    public void testGetNextInstanceNumberForObservationFact_factDoesNotExist() {
        Integer nextInstanceNumber = observationDao.getNextInstanceNumberForObservationFact(
            new FactSearchDto().setStartDate(LocalDateTime.parse("2002-10-04T00:00:00"))
                .setProviderId("provider-non")
                .setPatientNum(1)
                .setEncounterNum(2)
                .setConceptCode("concept-1"));

        assertThat(nextInstanceNumber).isNotNull().isEqualTo(1);
    }

    @Test
    public void testGetNextInstanceNumberForObservationFact_missingStartDate() {
        Integer nextInstanceNumber = observationDao.getNextInstanceNumberForObservationFact(
            new FactSearchDto()
                .setProviderId("provider-1")
                .setPatientNum(1)
                .setEncounterNum(2)
                .setConceptCode("concept-1"));

        assertThat(nextInstanceNumber).isNotNull().isEqualTo(3);
    }

    @Test
    public void testGetNextInstanceNumberForObservationFact_filterByProvider() {
        Integer nextInstanceNumber = observationDao.getNextInstanceNumberForObservationFact(
            new FactSearchDto()
                .setProviderId("provider-2"));

        assertThat(nextInstanceNumber).isNotNull().isEqualTo(2);
    }

    @Test
    public void testGetNextInstanceNumberForObservationFact_filterByEncounter() {
        Integer nextInstanceNumber = observationDao.getNextInstanceNumberForObservationFact(
            new FactSearchDto()
                .setEncounterNum(2));

        assertThat(nextInstanceNumber).isNotNull().isEqualTo(3);
    }

    @Test
    public void testGetNextInstanceNumberForObservationFact_filterByPatient() {
        Integer nextInstanceNumber = observationDao.getNextInstanceNumberForObservationFact(
            new FactSearchDto()
                .setPatientNum(2));

        assertThat(nextInstanceNumber).isNotNull().isEqualTo(2);
    }

    private Observation createObservation(
        Integer encounterId, Integer patientId, String conceptCode, String providerId, LocalDateTime startDate,
        List<ObservationModifier> modifiers) {

        return createDetailedObservation(encounterId, patientId, conceptCode, providerId, startDate,
            1, null, "demo", modifiers);
    }

    private Observation createDetailedObservation(
        Integer encounterId, Integer patientId, String conceptCode, String providerId, LocalDateTime startDate,
        Integer instanceNumber, LocalDateTime endDate, String sourceSystemCode, List<ObservationModifier> modifiers) {

        Observation observation = new Observation(encounterId, patientId, conceptCode, providerId, startDate);
        observation.setInstanceNumber(instanceNumber);
        observation.setEndDate(endDate);
        observation.setSourceSystemCode(sourceSystemCode);

        modifiers.forEach(modifier -> observation.addModifier(modifier));
        return observation;
    }

    private ObservationModifier createNumericModifier(String modifierCode, Double value,
                                                      String units) {
        ObservationModifier modifier = new ObservationModifier(modifierCode, ValueTypeCode.NUMERIC,
            "E", value, units, null);
        return modifier;
    }

    private ObservationModifier createTextModifier(String modifierCode, ValueTypeCode valueTypeCode, String value,
                                                   String units) {
        ObservationModifier modifier = new ObservationModifier(modifierCode, valueTypeCode,
            valueTypeCode == ValueTypeCode.TEXT ? value : null,
            null, units, valueTypeCode != ValueTypeCode.TEXT ? value : null);
        return modifier;
    }
    
    @Test
    public void testFindObservationFactsNoFilterCriteria() {
        FactSearchDto searchDto = new FactSearchDto();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("patient_num");
        searchDto.setPageableDto(pageableDto);
        
        PaginationResult<Observation> facts = observationDao.findObservations(searchDto) ;
        assertThat(facts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(4);

        Observation expectedFact = createObservation(1);
        assertThat(facts.getRecords().get(0)).isEqualToComparingFieldByFieldRecursively(expectedFact);
    }
    
    @Test
    public void testFindObservationFactsNoFilterCriteriaAndPagination() {
        FactSearchDto searchDto = new FactSearchDto();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setPage(1);
        pageableDto.setSize(2);
        pageableDto.setSortBy("patient_num");
        searchDto.setPageableDto(pageableDto);
        
        PaginationResult<Observation> facts = observationDao.findObservations(searchDto) ;
        assertThat(facts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(2);

        Observation expectedFact = createObservation(1);
        assertThat(facts.getRecords().get(0)).isEqualToComparingFieldByFieldRecursively(expectedFact);
    }
    
    @Test
    public void testFindObservationFactsByConceptCode() {
        FactSearchDto searchDto = new FactSearchDto();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("patient_num");
        searchDto.setConceptCode("concept-1");
        searchDto.setPageableDto(pageableDto);
        
        PaginationResult<Observation> facts = observationDao.findObservations(searchDto) ;
        assertThat(facts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(4);

        Observation expectedFact = createObservation(1);
        assertThat(facts.getRecords().get(0)).isEqualToComparingFieldByFieldRecursively(expectedFact);
    }
    
    @Test
    public void testFindObservationFactsByPatientNum() {
        FactSearchDto searchDto = new FactSearchDto();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("patient_num");
        searchDto.setPatientNum(1);
        searchDto.setPageableDto(pageableDto);
        
        PaginationResult<Observation> facts = observationDao.findObservations(searchDto) ;
        assertThat(facts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(3);

        Observation expectedFact = createObservation(1);
        assertThat(facts.getRecords().get(0)).isEqualToComparingFieldByFieldRecursively(expectedFact);
    }
    
    @Test
    public void testFindObservationFactsByPatientNumAndConceptCode() {
        FactSearchDto searchDto = new FactSearchDto();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setSortBy("patient_num");
        searchDto.setPatientNum(2);
        searchDto.setConceptCode("concept-1");
        searchDto.setPageableDto(pageableDto);
        
        PaginationResult<Observation> facts = observationDao.findObservations(searchDto) ;
        assertThat(facts.getRecords()).isNotNull().isNotEmpty().size().isEqualTo(1);
    }
    
    private Observation createObservation(int i) {
        Observation observation = new Observation();
        observation.setPatientNum(i);
        observation.setEncounterNum(i);
        observation.setConceptCode("concept-1");
        observation.setProviderId("provider-1");
        observation.setInstanceNumber(1);
        observation.setStartDate(LocalDateTime.parse("2002-10-04T00:00:00"));
        ObservationModifier observationModifier = new ObservationModifier();
        observationModifier.setModifierCode("@");
        observationModifier.setNumberValue(0.0);
        observation.addModifier(observationModifier);
        return observation;
    }
}
