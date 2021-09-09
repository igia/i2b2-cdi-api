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


package io.igia.i2b2.cdi.derivedconcept.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencyDto;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptTopologicalSortDto;

public class DerivedConceptTopologicalSortWrapperTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    DerivedConceptTopologicalSortWrapper derivedConceptTopologicalSortWrapper;

    @Before
    public void setUp() {
	derivedConceptTopologicalSortWrapper = new DerivedConceptTopologicalSortWrapper();
    }

    @Test
    public void testFindDerivedConceptCyclicDependency() {
	List<DerivedConceptDependencyDto> derivedConceptDependencies = new ArrayList<>(
		Arrays.asList(createDerivedConceptDependency(1, 1, "\\Derived\\test-2\\", "\\Derived\\test-1\\"),
			createDerivedConceptDependency(2, 1, "\\Derived\\test-3\\", "\\Derived\\test-1\\"),
			createDerivedConceptDependency(3, 3, "\\Derived\\test-1\\", "\\Derived\\test-3\\")));

	DerivedConceptTopologicalSortDto derivedConceptTopologicalSortDto = derivedConceptTopologicalSortWrapper
		.detectDerivedConceptCyclicDependency(derivedConceptDependencies);
	assertThat(derivedConceptTopologicalSortDto.getMessage()).isNotNull()
		.isEqualTo("There exists a cycle in the graph !!");
    }

    private DerivedConceptDependencyDto createDerivedConceptDependency(Integer id, Integer derivedConceptId,
	    String parentConceptPath, String derivedConceptPath) {
	DerivedConceptDependencyDto derivedConceptDependencyDto = new DerivedConceptDependencyDto();
	derivedConceptDependencyDto.setId(id);
	derivedConceptDependencyDto.setDerivedConceptId(derivedConceptId);
	derivedConceptDependencyDto.setParentConceptPath(parentConceptPath);
	derivedConceptDependencyDto.setDerivedConceptPath(derivedConceptPath);
	return derivedConceptDependencyDto;
    }
}
