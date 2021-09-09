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

import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.common.exception.I2b2DataValidationException;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.service.ConceptService;
import io.igia.i2b2.cdi.modifier.dao.ModifierDao;
import io.igia.i2b2.cdi.modifier.dto.ModifierDto;
import io.igia.i2b2.cdi.modifier.dto.ModifierSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.service.OntologyConceptService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ModifierServiceTest {

    @Mock
    private ModifierDao modifierDao;

    @Mock
    private ConceptService conceptService;

    @Mock
    private OntologyConceptService ontologyConceptService;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private ModifierService modifierService;

    @Before
    public void setUp() {
        modifierService = new ModifierServiceImpl(modifierDao, ontologyConceptService, conceptService);
    }

    @Test
    public void testGetModifiers() {
        List<ModifierDto> modifiers = Arrays.asList(
            createModifier("SEV:MIN", "Minor"), createModifier("SEV:MAJ", "Major"));
        given(modifierDao.findModifiers(any())).willReturn(modifiers);

        List<ModifierDto> actualModifiers = modifierService.getModifiers(new ModifierSearchDto());
        assertThat(actualModifiers).isNotNull().isNotEmpty().size().isEqualTo(2);
        assertThat(actualModifiers.get(0)).isEqualToComparingFieldByField(modifiers.get(0));
        assertThat(actualModifiers.get(1)).isEqualToComparingFieldByField(modifiers.get(1));
    }

    @Test
    public void testGetModifiers_invalidConceptCode() {
        PaginationResult<ConceptDto> concepts = new PaginationResult<>();
        concepts.setRecords(new ArrayList<ConceptDto>());
        given(conceptService.getConcepts(any())).willReturn(concepts);

        thrown.expect(I2b2DataValidationException.class);
        thrown.expectMessage("Invalid concept code.");
        modifierService.getModifiers(
            new ModifierSearchDto().setConceptCode("INVALID"));
    }

    @Test
    public void testGetModifiers_filterByConceptCode() {

        ModifierSearchDto modifierSearchDto = new ModifierSearchDto();
        modifierSearchDto.setConceptCode("ICD9:160");

        ConceptDto conceptDto = new ConceptDto();
        conceptDto.setConceptPath("'\\i2b2\\Diagnoses\\Neoplasms (140-239)\\Malignant neoplasms (140-208)\\Respiratory and intrathorasic organs (160-165)\\(160) Malignant neoplasm of nasal~\\'");
        
        given(conceptService.getConcepts(argThat(conceptSearchDto ->
            conceptSearchDto.getCode().equals(modifierSearchDto.getConceptCode()))))
            .willReturn(new PaginationResult<>(Arrays.asList(conceptDto), 0));

        OntologyConceptDto ontologyConceptDto = new OntologyConceptDto();
        ontologyConceptDto.setFullName("\\TNM\\Nodes\\");

        OntologyConceptDto ontologyConceptDto2 = new OntologyConceptDto();
        ontologyConceptDto2.setFullName("\\TNM\\Metastisis\\MX\\");
        List<String> conceptPaths = Arrays.asList(conceptDto).stream()
            .map(ConceptDto::getConceptPath)
            .collect(Collectors.toList());
        given(ontologyConceptService.getOntologyConcepts(argThat(ontologyConceptSearchDto ->
            ontologyConceptSearchDto.getConceptPaths().containsAll(conceptPaths))))
            .willReturn(Arrays.asList(ontologyConceptDto, ontologyConceptDto2));

        List<ModifierDto> modifiers = Arrays.asList(
            createModifier("SEV:MIN", "Minor"), createModifier("SEV:MAJ", "Major"));

        List<String> modifierPaths = Arrays.asList(ontologyConceptDto, ontologyConceptDto2).stream()
            .map(OntologyConceptDto::getFullName)
            .collect(Collectors.toList());
        given(modifierDao.findModifiers(argThat(actualModifierSearchDto ->
            actualModifierSearchDto.getModifierPaths().containsAll(modifierPaths)))).willReturn(modifiers);

        List<ModifierDto> actualModifiers = modifierService.getModifiers(modifierSearchDto);

        assertThat(actualModifiers).isNotNull().isNotEmpty().size().isEqualTo(2);
        assertThat(actualModifiers.get(0)).isEqualToComparingFieldByField(modifiers.get(0));
        assertThat(actualModifiers.get(1)).isEqualToComparingFieldByField(modifiers.get(1));

        verify(conceptService, times(1)).getConcepts(
            argThat(conceptSearchDto -> conceptSearchDto.getCode().equals(modifierSearchDto.getConceptCode())));

        verify(ontologyConceptService, times(1)).getOntologyConcepts(
            argThat(ontologyConceptSearchDto ->
                ontologyConceptSearchDto.getConceptPaths().containsAll(conceptPaths)));

        verify(modifierDao, times(1)).findModifiers(
            argThat(actualModifierSearchDto ->
                actualModifierSearchDto.getModifierPaths().containsAll(modifierPaths)));
    }

    @Test
    public void testGetModifiersWithDataType() {
        List<ModifierDto> modifiers = Arrays.asList(
            createModifier("SEV:MIN", "Minor", ConceptDataType.STRING),
            createModifier("SEV:MAJ", "Major", ConceptDataType.INTEGER));
        given(modifierDao.findModifiers(any())).willReturn(modifiers);

        OntologyConceptDto ontologyConceptDto = new OntologyConceptDto();
        ontologyConceptDto.setFullName("\\Minor\\SEV:MIN");
        ontologyConceptDto.setDataType(ConceptDataType.STRING);

        OntologyConceptDto ontologyConceptDto2 = new OntologyConceptDto();
        ontologyConceptDto2.setFullName("\\Major\\SEV:MAJ");
        ontologyConceptDto2.setDataType(ConceptDataType.INTEGER);

        List<String> conceptPaths = modifiers.stream()
            .map(ModifierDto::getModifierPath)
            .collect(Collectors.toList());
        given(ontologyConceptService.getOntologyConceptsWithDataType(argThat(ontologyConceptSearchDto ->
            ontologyConceptSearchDto.getConceptPaths().containsAll(conceptPaths))))
            .willReturn(Arrays.asList(ontologyConceptDto, ontologyConceptDto2));

        List<ModifierDto> actualModifiers = modifierService.getModifiersWithDataType(new ModifierSearchDto());
        assertThat(actualModifiers).isNotNull().isNotEmpty().size().isEqualTo(2);
        assertThat(actualModifiers.get(0)).isEqualToComparingFieldByField(modifiers.get(0));
        assertThat(actualModifiers.get(1)).isEqualToComparingFieldByField(modifiers.get(1));

        verify(ontologyConceptService, times(1)).getOntologyConceptsWithDataType(
            argThat(ontologyConceptSearchDto ->
                ontologyConceptSearchDto.getConceptPaths().containsAll(conceptPaths)));

        verify(modifierDao, times(1)).findModifiers(any());
    }

    @Test
    public void testValidateModifier() {
        List<ModifierDto> modifiers = Arrays.asList(
            createModifier("SEV:MIN", "Minor"));
        given(modifierDao.findModifiers(any())).willReturn(modifiers);

        modifierService.validate(new ModifierSearchDto());
        verify(modifierDao, times(1)).findModifiers(any());
    }

    @Test
    public void testValidateModifier_invalidModifierCode() {
        given(modifierDao.findModifiers(any())).willReturn(Collections.emptyList());

        thrown.expect(I2b2DataValidationException.class);
        thrown.expectMessage("Invalid modifier code.");
        modifierService.validate(new ModifierSearchDto().addModifierCode("ABC"));
        verify(modifierDao, times(1)).findModifiers(any());
    }

    private ModifierDto createModifier(String code, String name) {
        return createModifier(code, name, ConceptDataType.STRING);
    }
    private ModifierDto createModifier(String code, String name, ConceptDataType dataType) {
        ModifierDto modifierDto = new ModifierDto();
        modifierDto.setCode(code);
        modifierDto.setName(name);
        modifierDto.setModifierPath("\\" + name + "\\" + code);
        modifierDto.setSource("demo");
        modifierDto.setDataType(dataType);
        return modifierDto;
    }

}
