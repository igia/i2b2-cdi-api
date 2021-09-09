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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.common.dto.PageableDto;
import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.common.exception.I2b2DataValidationException;
import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.dto.ConceptSearchDto;
import io.igia.i2b2.cdi.concept.service.ConceptService;
import io.igia.i2b2.cdi.config.ApplicationProperties;
import io.igia.i2b2.cdi.encounter.dto.EncounterDto;
import io.igia.i2b2.cdi.encounter.dto.EncounterSearchDto;
import io.igia.i2b2.cdi.encounter.service.EncounterService;
import io.igia.i2b2.cdi.modifier.dto.ModifierDto;
import io.igia.i2b2.cdi.modifier.dto.ModifierSearchDto;
import io.igia.i2b2.cdi.modifier.service.ModifierService;
import io.igia.i2b2.cdi.observation.dao.ObservationDao;
import io.igia.i2b2.cdi.observation.domain.Observation;
import io.igia.i2b2.cdi.observation.domain.ObservationModifier;
import io.igia.i2b2.cdi.observation.domain.ValueTypeCode;
import io.igia.i2b2.cdi.observation.dto.FactDto;
import io.igia.i2b2.cdi.observation.dto.FactModifierDto;
import io.igia.i2b2.cdi.observation.dto.FactSearchDto;
import io.igia.i2b2.cdi.patient.dto.PatientDto;
import io.igia.i2b2.cdi.patient.dto.PatientSearchDto;
import io.igia.i2b2.cdi.patient.service.PatientService;
import io.igia.i2b2.cdi.provider.dto.ProviderSearchDto;
import io.igia.i2b2.cdi.provider.service.ProviderService;

@Service
@Transactional(readOnly = true)
public class ObservationServiceImpl implements ObservationService {

    private static final Logger logger = LoggerFactory.getLogger(ObservationServiceImpl.class);

    private final ObservationDao observationDao;
    private final ObservationMapper observationMapper;
    private final ConceptService conceptService;
    private final ModifierService modifierService;
    private final ProviderService providerService;
    private final PatientService patientService;
    private final EncounterService encounterService;
    private final ApplicationProperties applicationProperties;
    protected static final String PATIENT_NUM = "patient_num";
    protected static final String ID = "id";

    public ObservationServiceImpl(ObservationDao observationDao, ObservationMapper observationMapper,
                                  ConceptService conceptService, ModifierService modifierService,
                                  ProviderService providerService, PatientService patientService,
                                  EncounterService encounterService, ApplicationProperties applicationProperties) {
        this.observationDao = observationDao;
        this.observationMapper = observationMapper;
        this.conceptService = conceptService;
        this.modifierService = modifierService;
        this.providerService = providerService;
        this.patientService = patientService;
        this.encounterService = encounterService;
        this.applicationProperties = applicationProperties;
    }

    @Override
    @Transactional(readOnly = false)
    public FactDto addObservation(FactDto inFactDto) {

        FactDto factDto = new FactDto(inFactDto);
        validateObservation(factDto);

        if (StringUtils.isEmpty(factDto.getProviderId())) {
            factDto.setProviderId("@");
        }

        if (factDto.getInstanceNum() == null) {
            factDto.setInstanceNum(1);
        }

        if (StringUtils.isEmpty(factDto.getSourceSystemCode())) {
            factDto.setSourceSystemCode(applicationProperties.getSourceSystemCode());
        }
        FactDto observationWithUpdatedModifiers = new FactDto(factDto);
        identifyAndPopulateModifiersDataType(observationWithUpdatedModifiers);
        observationWithUpdatedModifiers.addModifier(createDefaultModifier(observationWithUpdatedModifiers));

        populateMappedIdentifier(observationWithUpdatedModifiers);

        saveObservation(observationWithUpdatedModifiers);

        return factDto;
    }

    @Retryable(value = {DuplicateKeyException.class}, maxAttempts = 3,
        backoff = @Backoff(delay = 2000, maxDelay = 20000, multiplier = 3, random = true))
    private void saveObservation(FactDto observationWithUpdatedModifiers) {
        Observation observation = observationMapper.observationFromDto(observationWithUpdatedModifiers);
        observation.setInstanceNumber(observationDao.getNextInstanceNumberForObservationFact(
            new FactSearchDto()
                .setConceptCode(observationWithUpdatedModifiers.getConceptCode())
                .setEncounterNum(observationWithUpdatedModifiers.getEncounterNum())
                .setPatientNum(observationWithUpdatedModifiers.getPatientNum())
                .setProviderId(observationWithUpdatedModifiers.getProviderId())
                .setStartDate(observationWithUpdatedModifiers.getStartDate())));

        int[] updateCount = observationDao.add(observation);

        if (Arrays.stream(updateCount).anyMatch(count -> count == 0)) {
            throw new I2b2Exception("Could not add the fact.");
        }
    }

    private void identifyAndPopulateModifiersDataType(FactDto factDto) {

        if(factDto.getModifiers().isEmpty()) {
            return;
        }
        Map<String, List<ModifierDto>> modifiersMap = modifierService.getModifiersWithDataType(new ModifierSearchDto()
            .setModifierCodes(factDto.getModifiers().stream()
                .map(FactModifierDto::getModifierCode)
                .collect(Collectors.toList())))
            .stream()
            .collect(Collectors.groupingBy(ModifierDto::getCode));

        factDto.getModifiers().stream().forEach(observationModifier -> {
            List<ModifierDto> modifiers = modifiersMap.getOrDefault(
                observationModifier.getModifierCode(), Collections.emptyList());
            if(!modifiers.isEmpty()) {
                if (modifiers.size() > 1) {
                    logger.debug("{} number of modifier detail got mapped against modifier code.", modifiers.size());
                    logger.debug("Considering first modifier detail to determine the data type.");
                }
                // get data type associated with first modifier in the list
                observationModifier.setValueTypeCode(mapObservationDataTypeFromConceptDataType(modifiers.get(0).getDataType()));
            }
            populateObservationModifierValue(observationModifier);
        });
    }

    private FactModifierDto createDefaultModifier(FactDto factDto) {
        FactModifierDto observationModifier = new FactModifierDto();
        observationModifier.setModifierCode("@");
        if (StringUtils.isEmpty(factDto.getValue())) {
            observationModifier.setValueTypeCode(ValueTypeCode.NONE);
        } else {
            observationModifier.setValueTypeCode(identifyValueTypeFromConceptTerm(factDto));
        }
        observationModifier.setValue(factDto.getValue());
        observationModifier.setUnits(factDto.getUnits());
        populateObservationModifierValue(observationModifier);
        return observationModifier;
    }

    private void populateObservationModifierValue(FactModifierDto observationModifier) {
        switch (observationModifier.getValueTypeCode()) {
            case NONE:
                observationModifier.setTextValue("");
                observationModifier.setUnits("");
                break;
            case TEXT:
                observationModifier.setTextValue(observationModifier.getValue());
                break;
            case NUMERIC:
                observationModifier.setNumberValue(Double.parseDouble(observationModifier.getValue()));
                observationModifier.setTextValue("E");
                break;
            case DATE:
                observationModifier.setNumberValue(Double.parseDouble(observationModifier.getValue()));
                observationModifier.setTextValue(observationModifier.getValue());
                observationModifier.setUnits("@");
                break;
            default:
                observationModifier.setBlob(observationModifier.getValue());
                observationModifier.setUnits("");
        }
    }

    private ValueTypeCode identifyValueTypeFromConceptTerm(FactDto factDto) {
        List<ConceptDto> concepts = conceptService.getConceptsWithDataType(
            new ConceptSearchDto().setCode(factDto.getConceptCode()));
        return mapObservationDataTypeFromConceptDataType(concepts.get(0).getDataType());
    }

    private ValueTypeCode mapObservationDataTypeFromConceptDataType(ConceptDataType dataType) {
        switch (dataType) {
            case STRING:
            case ENUM:
                return ValueTypeCode.TEXT;
            case FLOAT:
            case INTEGER:
            case POS_FLOAT:
            case POS_INTEGER:
                return ValueTypeCode.NUMERIC;
            case LARGE_STRING:
                return ValueTypeCode.RAW_TEXT;
            default:
                return ValueTypeCode.NONE;
        }
    }

    private void populateMappedIdentifier(FactDto factDto) {
        factDto.setPatientNum(getOrCreatePatientMappingIdentifier(factDto));
        // encounter creation requires patient num, so it should be placed after populating patient_num
        factDto.setEncounterNum(getOrCreateEncounterMappingIdentifier(factDto));
    }

    private Integer getOrCreateEncounterMappingIdentifier(FactDto factDto) {

        if (StringUtils.isEmpty(factDto.getEncounterId())) {
            return observationDao.getNextNegativeEncounterNumber();
        }

        EncounterSearchDto encounterSearchDto = new EncounterSearchDto()
            .setEncounterId(factDto.getEncounterId())
            .setPatientId(factDto.getPatientId());
        List<EncounterDto> encounters = encounterService.getEncounters(encounterSearchDto);
        if (encounters.isEmpty()) {
            EncounterDto encounterDto = new EncounterDto();
            encounterDto.setPatientId(factDto.getPatientId());
            encounterDto.setEncounterId(factDto.getEncounterId());
            encounterDto.setPatientNum(factDto.getPatientNum());
            EncounterDto encounter = encounterService.addEncounter(encounterDto);
            return encounter.getEncounterNum();
        } else {
            if (encounters.size() > 1) {
                logger.debug("Using first encounter num : {} to use in fact", encounters.get(0).getEncounterNum());
            }
            return encounters.get(0).getEncounterNum();
        }
    }

    private Integer getOrCreatePatientMappingIdentifier(FactDto factDto) {
        PatientSearchDto patientSearchDto = new PatientSearchDto()
            .setPatientId(factDto.getPatientId());
        List<PatientDto> patients = patientService.getPatients(patientSearchDto);
        if (patients.isEmpty()) {
            PatientDto patientDto = new PatientDto();
            patientDto.setPatientId(factDto.getPatientId());
            PatientDto patient = patientService.addPatient(patientDto);
            logger.debug("Patient created with identifier: {}", patient.getPatientNum());
            return patient.getPatientNum();
        } else {
            if (patients.size() > 1) {
                logger.debug("{} patients found by patient Id : {}, and project id : {}",
                    patients.size(), patientSearchDto.getPatientId(), patientSearchDto.getProjectId());
                logger.debug("Using first patient num : {} to use in fact", patients.get(0).getPatientNum());
            }
            logger.debug("Patient exists with identifier: {}", patients.get(0).getPatientNum());
            return patients.get(0).getPatientNum();
        }
    }

    private void validateObservation(FactDto factDto) {
        validateStartDate(factDto);
        validateConceptCode(factDto);
        validateModifierCode(factDto);
        validateProvider(factDto);
        validateEndDate(factDto);
    }

    private void validateStartDate(FactDto factDto) {
        if(factDto.getStartDate() == null) {
            throw new I2b2DataValidationException("The fact start date should not be empty.");
        }
    }

    private void validateEndDate(FactDto factDto) {
        if (factDto.getEndDate() != null
            && factDto.getEndDate().isBefore(factDto.getStartDate())) {
            throw new I2b2DataValidationException("The fact start date should come before the end date.");
        }
    }

    private void validateProvider(FactDto factDto) {
        if (!applicationProperties.isLenientValidation()
            && !StringUtils.isEmpty(factDto.getProviderId())) {
            providerService.validate(
                new ProviderSearchDto()
                    .setProviderId(factDto.getProviderId()));
        }
    }

    private void validateModifierCode(FactDto factDto) {
        if (!factDto.getModifiers().isEmpty()) {
            List<String> modifiers = factDto.getModifiers().stream()
                .map(FactModifierDto::getModifierCode).collect(Collectors.toList());

            if (modifiers.stream().allMatch(new HashSet<>()::add)) {
                modifierService.validate(
                    new ModifierSearchDto()
                        .setConceptCode(factDto.getConceptCode())
                        .setModifierCodes(modifiers));
            } else {
                throw new I2b2DataValidationException("The modifier code should be unique within the fact.");
            }
        }
    }

    private void validateConceptCode(FactDto factDto) {
        conceptService.validate(
            new ConceptSearchDto()
                .setCode(factDto.getConceptCode()));
    }

    @Override
    public PaginationResult<FactDto> getObservations(FactSearchDto inSearchDto) {
        PaginationResult<FactDto> observationFacts = new PaginationResult<>();

        // If patient id is not null, lookup into patient mapping and get
        // patientNum. 
        if (!StringUtils.isEmpty(inSearchDto.getPatientId())) {
            PatientSearchDto patientSearchDto = new PatientSearchDto().setPatientId(inSearchDto.getPatientId());
            List<PatientDto> patients = patientService.getPatientByPatientNum(patientSearchDto);
            if (!patients.isEmpty()) {
                inSearchDto.setPatientNum(patients.get(0).getPatientNum());
            } else {
                List<FactDto> factDtos = new ArrayList<>();
                observationFacts.setRecords(factDtos);
                return observationFacts;
            }
        }
        
        PageableDto pageableDto = inSearchDto.getPageableDto();
        if (pageableDto != null) {
            String sortBy = pageableDto.getSortBy();
            if (StringUtils.isEmpty(sortBy) || (!StringUtils.isEmpty(sortBy) && sortBy.equals(ID))) {
                pageableDto.setSortBy(PATIENT_NUM);
            }
            inSearchDto.setPageableDto(pageableDto);
        }
        
        PaginationResult<Observation> facts = observationDao.findObservations(inSearchDto);
        List<FactDto> obsFacts = new ArrayList<>();
        facts.getRecords().stream()
                .forEach(observation -> obsFacts.add(mapObservationFactObject(observation, inSearchDto)));

        observationFacts.setRecords(obsFacts);
        observationFacts.setTotalCount(observationDao.getTotalCount(inSearchDto));
        return observationFacts;
    }

    private FactDto mapObservationFactObject(Observation observation, FactSearchDto inSearchDto) {
        FactDto fact = new FactDto();
        fact.setEncounterId(getEncounterIdFromMappingIdentifier(observation));
        fact.setPatientId(getPatientIdFromMappingIdentifier(observation));
        fact.setConceptCode(observation.getConceptCode());
        fact.setProviderId(observation.getProviderId());
        fact.setStartDate(observation.getStartDate());
        fact.setEndDate(observation.getEndDate());

        List<FactModifierDto> modifiers = new ArrayList<>();
        if (inSearchDto.getModifierFlag()) {
            FactSearchDto searchDto = new FactSearchDto();
            searchDto.setPatientNum(observation.getPatientNum());
            searchDto.setConceptCode(observation.getConceptCode());
            List<Observation> observationsModifiers = getObservationModifiers(searchDto);
            if (!observationsModifiers.isEmpty()) {
                observationsModifiers.stream().forEach(observationsModifier -> {
                    ObservationModifier obsModifier = observationsModifier.getModifiers().get(0);
                    FactModifierDto modifier = new FactModifierDto();
                    modifier.setModifierCode(obsModifier.getModifierCode());
                    if (obsModifier.getValueTypeCode() == ValueTypeCode.NUMERIC) {
                        modifier.setValue(String.valueOf(obsModifier.getNumberValue()));
                    } else {
                        modifier.setValue(obsModifier.getTextValue());
                    }
                    modifier.setUnits(obsModifier.getUnits());
                    modifier.setBlob(obsModifier.getBlob());
                    modifiers.add(modifier);
                });
            } else {
                mapObservationModifierObject(observation, fact);
            }
        } else {
            mapObservationModifierObject(observation, fact);
        }

        fact.setModifiers(modifiers);
        return fact;
    }

    private void mapObservationModifierObject(Observation observation, FactDto fact) {
        ObservationModifier obsModifier = observation.getModifiers().get(0);
        if (obsModifier.getValueTypeCode() == ValueTypeCode.NUMERIC) {
            fact.setValue(String.valueOf(obsModifier.getNumberValue()));
        } else {
            fact.setValue(obsModifier.getTextValue());
        }
        fact.setUnits(obsModifier.getUnits());
    }

    @Override
    public List<Observation> getObservationModifiers(FactSearchDto factSearchDto) {
        return observationDao.findObservationModifiers(factSearchDto);
    }
    
    private String getPatientIdFromMappingIdentifier(Observation observation) {
        String patientId = "";
        PatientSearchDto patientSearchDto = new PatientSearchDto();
        patientSearchDto.setPatientNum(observation.getPatientNum());
        List<PatientDto> patientDtos = patientService.getPatientByPatientNum(patientSearchDto);
        if (!patientDtos.isEmpty()) {
            patientId = patientDtos.get(0).getPatientId();
        }
        return patientId;
    }

    private String getEncounterIdFromMappingIdentifier(Observation observation) {
        String encounterId = "";
        EncounterSearchDto encounterSearchDto = new EncounterSearchDto();
        encounterSearchDto.setEncounterNum(observation.getEncounterNum());
        List<EncounterDto> encounterDtos = encounterService.getEncounterByEncounterNum(encounterSearchDto);
        if (!encounterDtos.isEmpty()) {
            encounterId = encounterDtos.get(0).getEncounterId();
        }
        return encounterId;
    }
}
