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



package io.igia.i2b2.cdi.derivedconcept.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DerivedConceptDependencySearchDto {
    private Integer derivedConceptId;
    private List<String> parentConceptPaths;
    private Set<Integer> derivedConceptIds = new HashSet<>();
    private Set<String> conceptPaths = new HashSet<>();

    public Integer getDerivedConceptId() {
	return derivedConceptId;
    }

    public void setDerivedConceptId(Integer derivedConceptId) {
	this.derivedConceptId = derivedConceptId;
    }

    public List<String> getParentConceptPaths() {
	return parentConceptPaths;
    }

    public void setParentConceptPaths(List<String> parentConceptPaths) {
	this.parentConceptPaths = parentConceptPaths;
    }

    public Set<Integer> getDerivedConceptIds() {
	return derivedConceptIds;
    }

    public void setDerivedConceptIds(Set<Integer> derivedConceptIds) {
	this.derivedConceptIds = derivedConceptIds;
    }

    public Set<String> getConceptPaths() {
	return conceptPaths;
    }

    public void setConceptPaths(Set<String> conceptPaths) {
	this.conceptPaths = conceptPaths;
    }

}
