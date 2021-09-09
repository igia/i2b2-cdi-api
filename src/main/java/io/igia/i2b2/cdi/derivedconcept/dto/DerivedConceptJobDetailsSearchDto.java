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

import java.util.ArrayList;
import java.util.List;

public class DerivedConceptJobDetailsSearchDto {
    
    private List<Integer> derivedConceptIds = new ArrayList<>();
    private DerivedConceptJobDetailsFetchType derivedConceptJobDetailsFetchType;

    public List<Integer> getDerivedConceptIds() {
        return derivedConceptIds;
    }

    public void setDerivedConceptIds(List<Integer> derivedConceptIds) {
        this.derivedConceptIds = derivedConceptIds;
    }

    public DerivedConceptJobDetailsFetchType getDerivedConceptJobDetailsFetchType() {
        return derivedConceptJobDetailsFetchType;
    }

    public void setDerivedConceptJobDetailsFetchType(DerivedConceptJobDetailsFetchType derivedConceptJobDetailsFetchType) {
        this.derivedConceptJobDetailsFetchType = derivedConceptJobDetailsFetchType;
    }
}