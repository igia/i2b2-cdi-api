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


package io.igia.i2b2.cdi.derivedconcept.service;

import java.util.List;
import java.util.Set;

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencyDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsSearchDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptQueryMasterDto;

public interface DerivedConceptService {
    DerivedConceptDto getDerivedConceptById(Integer id);

    List<DerivedConceptDto> getDerivedConcepts();

    DerivedConceptDto addDerivedConcept(DerivedConceptDto derivedConceptDto);

    DerivedConceptDto updateDerivedConcept(DerivedConceptDto derivedConceptDto);

    DerivedConceptDto deleteDerivedConcept(DerivedConceptDto derivedConceptDto);

    int[] calculateDerivedConcept(Integer id);

    List<DerivedConceptJobDetailsDto> getDerivedConceptJobDetails(
	    DerivedConceptJobDetailsSearchDto derivedConceptJobDetailsSearchDto);

    List<Set<DerivedConceptDependencyDto>> getAllDerivedConceptDependencyHierarchy();

    List<DerivedConceptQueryMasterDto> getQueryMasterRecords(Integer fetchSize);
}
