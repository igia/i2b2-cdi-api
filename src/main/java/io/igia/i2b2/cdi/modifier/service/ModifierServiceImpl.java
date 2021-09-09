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


package io.igia.i2b2.cdi.modifier.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.common.exception.I2b2DataValidationException;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.dto.ConceptSearchDto;
import io.igia.i2b2.cdi.concept.service.ConceptService;
import io.igia.i2b2.cdi.modifier.dao.ModifierDao;
import io.igia.i2b2.cdi.modifier.dto.ModifierDto;
import io.igia.i2b2.cdi.modifier.dto.ModifierSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptSearchDto;
import io.igia.i2b2.cdi.ontology.service.OntologyConceptService;

@Service
@Transactional(readOnly = true)
public class ModifierServiceImpl implements ModifierService {

    public static final Logger logger = LoggerFactory.getLogger(ModifierServiceImpl.class);

    private final ModifierDao modifierDao;
    private final OntologyConceptService ontologyConceptService;
    private final ConceptService conceptService;

    public ModifierServiceImpl(ModifierDao modifierDao, OntologyConceptService ontologyConceptService,
                               ConceptService conceptService) {
        this.modifierDao = modifierDao;
        this.ontologyConceptService = ontologyConceptService;
        this.conceptService = conceptService;
    }

    @Override
    public List<ModifierDto> getModifiers(ModifierSearchDto inModifierSearchDto) {

        ModifierSearchDto modifierSearchDto = new ModifierSearchDto(inModifierSearchDto);

        if (!StringUtils.isEmpty(modifierSearchDto.getConceptCode())) {
            ConceptSearchDto conceptSearchDto = new ConceptSearchDto();
            conceptSearchDto.setCode(modifierSearchDto.getConceptCode());
            List<ConceptDto> concepts = conceptService.getConcepts(conceptSearchDto).getRecords();

            if (concepts.isEmpty()) {
                throw new I2b2DataValidationException("Invalid concept code.");
            }
            logger.info("{} number of concepts matched against concept code : {}", concepts.size(), conceptSearchDto.getCode());
            
            OntologyConceptSearchDto ontologyConceptSearchDto =
                new OntologyConceptSearchDto()
                    .setConceptPaths(concepts.stream().map(ConceptDto::getConceptPath).collect(Collectors.toList()))
                    .setModifierConcept(true);
            List<OntologyConceptDto> ontologyConcepts = ontologyConceptService.getOntologyConcepts(ontologyConceptSearchDto);

            modifierSearchDto.setModifierPaths(ontologyConcepts.stream().map(OntologyConceptDto::getFullName).collect(Collectors.toList()));
            logger.info("{} number of modifier paths matched", modifierSearchDto.getModifierPaths().size());
            logger.debug("Matched modifier paths : {}", modifierSearchDto.getModifierPaths());


            if (modifierSearchDto.getModifierPaths().isEmpty()) {
                return new ArrayList<>();
            }
        }
        return modifierDao.findModifiers(modifierSearchDto);
    }

    @Override
    public List<ModifierDto> getModifiersWithDataType(ModifierSearchDto inModifierSearchDto) {
        ModifierSearchDto modifierSearchDto = new ModifierSearchDto(inModifierSearchDto);
        List<ModifierDto> modifiers = getModifiers(modifierSearchDto);

        logger.debug("{} number of modifiers matched.", modifiers.size());

        OntologyConceptSearchDto ontologyConceptSearchDto = new OntologyConceptSearchDto();
        ontologyConceptSearchDto.setConceptPaths(modifiers.stream()
            .map(ModifierDto::getModifierPath)
            .collect(Collectors.toList()));

        Map<String, List<OntologyConceptDto>> ontologyConceptsMap =
            ontologyConceptService.getOntologyConceptsWithDataType(ontologyConceptSearchDto)
                .stream()
                .collect(Collectors.groupingBy(OntologyConceptDto::getFullName));

        modifiers.forEach(modifierDto -> {
            List<OntologyConceptDto> ontologyConcepts = ontologyConceptsMap.getOrDefault(
                modifierDto.getModifierPath(), Collections.emptyList());
            if(ontologyConcepts.isEmpty()) {
                logger.debug("No ontology concept matched against modifier path : {}", modifierDto.getModifierPath());
                // defaults to String
                modifierDto.setDataType(ConceptDataType.STRING);
            } else {
                if(ontologyConcepts.size() > 1) {
                    logger.debug("{} number of ontology concepts got mapped against modifier path.", ontologyConcepts.size());
                    logger.debug("Considering first ontology concept to determine the data type.");
                }
                // get data type associated with first concept in the list
                modifierDto.setDataType(ontologyConcepts.get(0).getDataType());
            }
        });
        return modifiers;
    }

    @Override
    public void validate(ModifierSearchDto modifierSearchDto) {
        List<String> modifierCodes = getModifiers(modifierSearchDto).stream()
            .map(ModifierDto::getCode)
            .collect(Collectors.toList());
        Optional<String> codeNotMatched = modifierSearchDto.getModifierCodes().stream()
            .filter(code -> !modifierCodes.contains(code))
            .findFirst();
        if (codeNotMatched.isPresent()) {
            throw new I2b2DataValidationException("Invalid modifier code.");
        }
    }
}
