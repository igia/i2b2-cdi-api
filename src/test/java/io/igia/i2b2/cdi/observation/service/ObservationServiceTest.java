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
package io.igia.i2b2.cdi.observation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.common.exception.I2b2DataValidationException;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.service.ConceptService;
import io.igia.i2b2.cdi.config.ApplicationProperties;
import io.igia.i2b2.cdi.encounter.dto.EncounterDto;
import io.igia.i2b2.cdi.encounter.service.EncounterService;
import io.igia.i2b2.cdi.modifier.dto.ModifierDto;
import io.igia.i2b2.cdi.modifier.service.ModifierService;
import io.igia.i2b2.cdi.observation.dao.ObservationDao;
import io.igia.i2b2.cdi.observation.domain.Observation;
import io.igia.i2b2.cdi.observation.domain.ObservationModifier;
import io.igia.i2b2.cdi.observation.dto.FactDto;
import io.igia.i2b2.cdi.observation.dto.FactModifierDto;
import io.igia.i2b2.cdi.observation.dto.FactSearchDto;
import io.igia.i2b2.cdi.patient.dto.PatientDto;
import io.igia.i2b2.cdi.patient.service.PatientService;
import io.igia.i2b2.cdi.provider.service.ProviderService;

@RunWith(MockitoJUnitRunner.class)
public class ObservationServiceTest {

    @Mock
    private ObservationDao observationDao;
    @Mock
    private ConceptService conceptService;
    @Mock
    private ModifierService modifierService;
    @Mock
    private ProviderService providerService;

    @Mock
    private PatientService patientService;

    @Mock
    private EncounterService encounterService;

    @Spy
    ApplicationProperties applicationProperties = new ApplicationProperties();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ObservationService observationService;
    private ObservationMapper observationMapper;

    @Before
    public void setUp() {
        observationMapper= new ObservationMapperImpl();
        observationService = new ObservationServiceImpl(observationDao, observationMapper, conceptService,
            modifierService, providerService, patientService, encounterService, applicationProperties);
    }

    @Test
    public void addObservation_invalidConceptCode() {
        thrown.expect(I2b2DataValidationException.class);
        willThrow(new I2b2DataValidationException("Invalid concept")).given(conceptService).validate(any());
        observationService.addObservation(createObservationDto(Arrays.asList(
            createModifierDto("1")
        )));
    }

    @Test
    public void addObservation_duplicateModifierCode() {
        thrown.expect(I2b2DataValidationException.class);
        thrown.expectMessage("The modifier code should be unique within the fact.");
        observationService.addObservation(createObservationDto(Arrays.asList(
            createModifierDto("1"),
            createModifierDto("1")
        )));
    }

    @Test
    public void addObservation_invalidModifierCode() {
        thrown.expect(I2b2DataValidationException.class);
        willThrow(new I2b2DataValidationException("Invalid concept")).given(modifierService).validate(any());
        observationService.addObservation(createObservationDto(Arrays.asList(
            createModifierDto("1")
        )));
    }

    @Test
    public void addObservation_invalidProviderCode() {
        thrown.expect(I2b2DataValidationException.class);
        willThrow(new I2b2DataValidationException("Invalid id")).given(providerService).validate(any());
        observationService.addObservation(createObservationDto(Arrays.asList(
            createModifierDto("1")
        )));
    }


    @Test
    public void addObservation_invalidProviderCode_lenientValidation() {
        given(applicationProperties.isLenientValidation()).willReturn(true);

        PatientDto patientDto = new PatientDto();
        patientDto.setPatientNum(12);
        given(patientService.getPatients(argThat(search -> search.getPatientId().equalsIgnoreCase("1"))))
            .willReturn(Arrays.asList(patientDto));

        EncounterDto encounter = new EncounterDto();
        encounter.setEncounterNum(12);
        given(encounterService.getEncounters(argThat(search -> search.getEncounterId().equalsIgnoreCase("1"))))
            .willReturn(Arrays.asList(encounter));

        ConceptDto concept = new ConceptDto();
        concept.setDataType(ConceptDataType.INTEGER);
        given(conceptService.getConceptsWithDataType(argThat(search -> search.getCode().equals("1"))))
            .willReturn(Arrays.asList(concept));

        given(observationDao.add(any())).willReturn(new int[]{1});

        FactDto outObservation = observationService.addObservation(
            createObservationDto(
                LocalDateTime.now().plusDays(1), "2", "mg",
                Collections.emptyList()));
        assertThat(outObservation).isNotNull();

        verify(providerService, times(0)).validate(any());
    }

    @Test
    public void addObservation_emptyStartDate() {
        thrown.expect(I2b2DataValidationException.class);
        thrown.expectMessage("The fact start date should not be empty.");
        FactDto fact = createObservationDto(null, "2", "mg",
            Collections.emptyList());
        fact.setStartDate(null);
        observationService.addObservation(fact);
    }

    @Test
    public void addObservation_endDateBeforeStartDate() {
        thrown.expect(I2b2DataValidationException.class);
        thrown.expectMessage("The fact start date should come before the end date.");
        observationService.addObservation(createObservationDto(LocalDateTime.now().minusDays(1), "2", "mg",
            Collections.emptyList()));
    }

    @Test
    public void addObservation_noModifier() {
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientNum(12);
        given(patientService.getPatients(argThat(search -> search.getPatientId().equalsIgnoreCase("1"))))
            .willReturn(Arrays.asList(patientDto));

        EncounterDto encounter = new EncounterDto();
        encounter.setEncounterNum(12);
        given(encounterService.getEncounters(argThat(search -> search.getEncounterId().equalsIgnoreCase("1"))))
            .willReturn(Arrays.asList(encounter));

        ConceptDto concept = new ConceptDto();
        concept.setDataType(ConceptDataType.INTEGER);
        given(conceptService.getConceptsWithDataType(argThat(search -> search.getCode().equals("1"))))
            .willReturn(Arrays.asList(concept));

        given(observationDao.add(any())).willReturn(new int[]{1});

        FactDto outObservation = observationService.addObservation(
            createObservationDto(
                LocalDateTime.now().plusDays(1), "2", "mg",
                Collections.emptyList()));
        assertThat(outObservation).isNotNull();
    }

    @Test
    public void addObservation_withModifier_andNumericData() {
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientNum(12);
        given(patientService.getPatients(argThat(search -> search.getPatientId().equalsIgnoreCase("1"))))
            .willReturn(Arrays.asList(patientDto));

        EncounterDto encounter = new EncounterDto();
        encounter.setEncounterNum(12);
        given(encounterService.getEncounters(argThat(search -> search.getEncounterId().equalsIgnoreCase("1"))))
            .willReturn(Arrays.asList(encounter));

        ModifierDto modifier = new ModifierDto();
        modifier.setDataType(ConceptDataType.INTEGER);
        modifier.setCode("1");
        given(modifierService.getModifiersWithDataType(argThat(search -> search.getModifierCodes().contains("1"))))
            .willReturn(Arrays.asList(modifier));

        given(observationDao.add(any())).willReturn(new int[]{1});

        FactDto outObservation = observationService.addObservation(
            createObservationDto(
                LocalDateTime.now().plusDays(1), "", "",
                Arrays.asList(createModifierDto("1"))));
        assertThat(outObservation).isNotNull();
    }

    @Test
    public void addObservation_encounter_patient_withModifier_numericAndTextData() {
        PatientDto patientDto = new PatientDto();
        patientDto.setPatientNum(12);
        given(patientService.getPatients(argThat(search -> search.getPatientId().equalsIgnoreCase("1"))))
            .willReturn(Collections.emptyList());
        given(patientService.addPatient(argThat(patient -> patient.getPatientId().equalsIgnoreCase("1"))))
            .willReturn(patientDto);

        EncounterDto encounter = new EncounterDto();
        encounter.setEncounterNum(12);
        given(encounterService.getEncounters(argThat(search -> search.getEncounterId().equalsIgnoreCase("1"))))
            .willReturn(Collections.emptyList());
        given(encounterService.addEncounter(argThat(encounter1 -> encounter1.getEncounterId().equalsIgnoreCase("1"))))
            .willReturn(encounter);

        ModifierDto modifier = new ModifierDto();
        modifier.setCode("1");
        modifier.setDataType(ConceptDataType.INTEGER);

        ModifierDto modifier2 = new ModifierDto();
        modifier2.setCode("2");
        modifier2.setDataType(ConceptDataType.LARGE_STRING);

        ModifierDto modifier3 = new ModifierDto();
        modifier3.setCode("3");
        modifier3.setDataType(ConceptDataType.ENUM);

        given(modifierService.getModifiersWithDataType(
            argThat(search -> search.getModifierCodes().containsAll(Arrays.asList("1", "2", "3")))
        )).willReturn(Arrays.asList(modifier, modifier2, modifier3));

        given(observationDao.add(any())).willReturn(new int[]{1, 1, 1});

        FactDto inObservation = createObservationDto(
            null, "", "",
            Arrays.asList(
                createModifierDto("1"),
                createModifierDto("2", "long raw text", null),
                createModifierDto("3", "small text", "%")
            ));
        inObservation.setProviderId(null);
        inObservation.setInstanceNum(1);
        inObservation.setSourceSystemCode("DEM");

        FactDto outObservation = observationService.addObservation(inObservation);
        assertThat(outObservation).isNotNull();
        assertThat(outObservation.getModifiers()).isNotNull().isNotEmpty().size().isEqualTo(3);
    }

    private FactModifierDto createModifierDto(String code) {
        return createModifierDto(code, "2", "mg");
    }
    private FactModifierDto createModifierDto(String code, String value, String unit) {
        FactModifierDto modifierDto = new FactModifierDto();
        modifierDto.setModifierCode(code);
        modifierDto.setUnits(unit);
        modifierDto.setValue(value);
        return modifierDto;
    }

    private FactDto createObservationDto(List<FactModifierDto> modifiers) {
        return createObservationDto(null, null, null, modifiers);
    }

    private FactDto createObservationDto(LocalDateTime endDate, String value, String units,
                                         List<FactModifierDto> modifiers) {
        FactDto factDto = new FactDto();
        factDto.setConceptCode("1");
        factDto.setEncounterId("1");
        factDto.setPatientId("1");
        factDto.setProviderId("1");
        factDto.setStartDate(LocalDateTime.now());
        factDto.setEndDate(endDate);
        factDto.setValue(value);
        factDto.setUnits(units);

        factDto.setModifiers(modifiers);
        return factDto;
    }
    
    @Test
    public void getObservations() {
        FactSearchDto factSearchDto = new FactSearchDto();
        factSearchDto.setConceptCode("C1");
        PageableDto pageableDto = new PageableDto();
        factSearchDto.setPageableDto(pageableDto);

        List<Observation> facts = Arrays.asList(createObservation(1, true), createObservation(2, true),
                createObservation(3, true));
        
        PaginationResult<Observation> result = new PaginationResult<>(facts, 3);
        when(observationDao.findObservations(factSearchDto)).thenReturn(result);
        
        List<EncounterDto> encounterDtos = new ArrayList<>();
        EncounterDto encounter = new EncounterDto();
        encounter.setEncounterNum(1);
        encounter.setEncounterId("E1");
        encounterDtos.add(encounter);
        when(encounterService.getEncounterByEncounterNum(any())).thenReturn(encounterDtos);
        
        List<PatientDto> patientDtos = new ArrayList<>();
        PatientDto patient = new PatientDto();
        patient.setPatientNum(1);
        patient.setPatientId("P1");
        patientDtos.add(patient);
        when(patientService.getPatientByPatientNum(any())).thenReturn(patientDtos);
        
        when(observationDao.getTotalCount(any())).thenReturn(3);
        
        PaginationResult<FactDto> obsFacts = observationService.getObservations(factSearchDto);
        assertThat(obsFacts.getRecords()).size().isEqualTo(3);
        assertThat(obsFacts.getTotalCount()).isEqualTo(3);
    }
    
    @Test
    public void getObservationsByModifiersFlagTrue() {
        FactSearchDto factSearchDto = new FactSearchDto();
        factSearchDto.setConceptCode("C1");
        factSearchDto.setModifierFlag(true);
        PageableDto pageableDto = new PageableDto();
        factSearchDto.setPageableDto(pageableDto);

        List<Observation> facts = Arrays.asList(createObservation(1, true), createObservation(2, true),
                createObservation(3, true));
        
        PaginationResult<Observation> result = new PaginationResult<>(facts, 3);
        when(observationDao.findObservations(factSearchDto)).thenReturn(result);
        
        List<EncounterDto> encounterDtos = new ArrayList<>();
        EncounterDto encounter = new EncounterDto();
        encounter.setEncounterNum(1);
        encounter.setEncounterId("E1");
        encounterDtos.add(encounter);
        when(encounterService.getEncounterByEncounterNum(any())).thenReturn(encounterDtos);
        
        List<PatientDto> patientDtos = new ArrayList<>();
        PatientDto patient = new PatientDto();
        patient.setPatientNum(1);
        patient.setPatientId("P1");
        patientDtos.add(patient);
        when(patientService.getPatientByPatientNum(any())).thenReturn(patientDtos);
               
        when(observationDao.getTotalCount(any())).thenReturn(3);
        
        PaginationResult<FactDto> obsFacts = observationService.getObservations(factSearchDto);
        assertThat(obsFacts.getRecords()).size().isEqualTo(3);
        assertThat(obsFacts.getTotalCount()).isEqualTo(3);
    }
    
    @Test
    public void getObservationsByPatientId() {
        FactSearchDto factSearchDto = new FactSearchDto();
        factSearchDto.setPatientId("P1");
        factSearchDto.setConceptCode("C1");
        factSearchDto.setModifierFlag(true);
        PageableDto pageableDto = new PageableDto();
        factSearchDto.setPageableDto(pageableDto);

        List<Observation> facts = Arrays.asList(createObservation(1, true), createObservation(2, true),
                createObservation(3, true));
        
        PaginationResult<Observation> result = new PaginationResult<>(facts, 3);
        when(observationDao.findObservations(factSearchDto)).thenReturn(result);
        
        List<EncounterDto> encounterDtos = new ArrayList<>();
        EncounterDto encounter = new EncounterDto();
        encounter.setEncounterNum(1);
        encounter.setEncounterId("E1");
        encounterDtos.add(encounter);
        when(encounterService.getEncounterByEncounterNum(any())).thenReturn(encounterDtos);
        
        List<PatientDto> patientDtos = new ArrayList<>();
        PatientDto patient = new PatientDto();
        patient.setPatientNum(1);
        patient.setPatientId("P1");
        patientDtos.add(patient);
        when(patientService.getPatientByPatientNum(any())).thenReturn(patientDtos);
               
        when(observationDao.getTotalCount(any())).thenReturn(3);
        
        PaginationResult<FactDto> obsFacts = observationService.getObservations(factSearchDto);
        assertThat(obsFacts.getRecords()).size().isEqualTo(3);
        assertThat(obsFacts.getTotalCount()).isEqualTo(3);
    }
    
    @Test
    public void getObservationsByPatientIdNotFound() {
        FactSearchDto factSearchDto = new FactSearchDto();
        factSearchDto.setPatientId("P1");
        factSearchDto.setConceptCode("C1");
        factSearchDto.setModifierFlag(true);
        PageableDto pageableDto = new PageableDto();
        factSearchDto.setPageableDto(pageableDto);
       
        PaginationResult<FactDto> obsFacts = observationService.getObservations(factSearchDto);
        assertThat(obsFacts.getRecords()).size().isEqualTo(0);
        assertThat(obsFacts.getTotalCount()).isEqualTo(0);
    }
    
    private Observation createObservation(int i, boolean modifierFlag) {
        Observation observation = new Observation();
        observation.setPatientNum(i);
        observation.setEncounterNum(i);
        observation.setConceptCode("C" + 1);
        if (modifierFlag) {
            ObservationModifier observationModifier = new ObservationModifier();
            observationModifier.setModifierCode("M" + 1);
            observation.addModifier(observationModifier);
        }
        return observation;
    }
}
