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


package io.igia.i2b2.cdi.modifier.dto;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ModifierSearchDto {
    private String source;
    private String conceptCode;
    private Set<String> modifierPaths = new HashSet<>();
    private Set<String> modifierCodes = new HashSet<>();

    public ModifierSearchDto() {

    }

    public ModifierSearchDto(ModifierSearchDto modifierSearchDto) {
        this.source = modifierSearchDto.getSource();
        this.conceptCode = modifierSearchDto.getConceptCode();
        this.modifierPaths.addAll(modifierSearchDto.getModifierPaths());
        this.modifierCodes.addAll(modifierSearchDto.getModifierCodes());
    }

    public String getSource() {
        return source;
    }

    public ModifierSearchDto setSource(String source) {
        this.source = source;
        return this;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public ModifierSearchDto setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
        return this;
    }

    public Set<String> getModifierPaths() {
        return Collections.unmodifiableSet(modifierPaths);
    }

    public ModifierSearchDto setModifierPaths(Collection<String> modifierPaths) {
        this.modifierPaths.addAll(modifierPaths);
        return this;
    }

    public ModifierSearchDto addModifierPath(String modifierPath) {
        this.modifierPaths.add(modifierPath);
        return this;
    }

    public Set<String> getModifierCodes() {
        return modifierCodes;
    }

    public ModifierSearchDto setModifierCodes(Collection<String> modifierCodes) {
        this.modifierCodes.addAll(modifierCodes);
        return this;
    }

    public ModifierSearchDto addModifierCode(String modifierCode) {
        this.modifierCodes.add(modifierCode);
        return this;
    }
}
