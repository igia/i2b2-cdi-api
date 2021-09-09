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


package io.igia.i2b2.cdi.ontology.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class OntologyConceptSearchDto {

    private boolean modifierConcept;
    private List<String> conceptPaths = new ArrayList<>();
    private List<String> modifierAppliedPaths = new ArrayList<>();
    private OntologyDto ontologyDto;
    private String existingConceptFullName;
    private String conceptPath;
    private List<Integer> conceptLevels = new ArrayList<>();

    public OntologyConceptSearchDto() {
    }

    public OntologyConceptSearchDto(OntologyConceptSearchDto ontologyConceptSearchDto) {
	this.modifierConcept = ontologyConceptSearchDto.isModifierConcept();
	this.conceptPaths.addAll(ontologyConceptSearchDto.getConceptPaths());
	this.modifierAppliedPaths.addAll(ontologyConceptSearchDto.getModifierAppliedPaths());
	this.conceptLevels.addAll(ontologyConceptSearchDto.getConceptLevels());
    }

    public boolean isModifierConcept() {
	return modifierConcept;
    }

    public OntologyConceptSearchDto setModifierConcept(boolean modifierConcept) {
	this.modifierConcept = modifierConcept;
	return this;
    }

    public List<String> getModifierAppliedPaths() {
	return Collections.unmodifiableList(modifierAppliedPaths);
    }

    public OntologyConceptSearchDto setModifierAppliedPaths(Collection<String> modifierAppliedPaths) {
	this.modifierAppliedPaths.addAll(modifierAppliedPaths);
	return this;
    }

    public List<String> getConceptPaths() {
	return Collections.unmodifiableList(conceptPaths);
    }

    public OntologyConceptSearchDto setConceptPaths(List<String> conceptPaths) {
	this.conceptPaths.addAll(conceptPaths);
	return this;
    }

    public OntologyDto getOntologyDto() {
	return ontologyDto;
    }

    public void setOntologyDto(OntologyDto ontologyDto) {
	this.ontologyDto = ontologyDto;
    }

    public String getExistingConceptFullName() {
	return existingConceptFullName;
    }

    public void setExistingConceptFullName(String existingConceptFullName) {
	this.existingConceptFullName = existingConceptFullName;
    }

    public String getConceptPath() {
	return conceptPath;
    }

    public void setConceptPath(String conceptPath) {
	this.conceptPath = conceptPath;
    }

    public List<Integer> getConceptLevels() {
        return conceptLevels;
    }

    public void setConceptLevels(List<Integer> conceptLevels) {
        this.conceptLevels = conceptLevels;
    }
}
