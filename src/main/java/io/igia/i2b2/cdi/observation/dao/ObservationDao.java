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



package io.igia.i2b2.cdi.observation.dao;

import java.util.List;

import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.observation.domain.Observation;
import io.igia.i2b2.cdi.observation.dto.FactSearchDto;

public interface ObservationDao {

    int[] add(Observation observation);

    Integer getNextInstanceNumberForObservationFact(FactSearchDto factSearchDto);

    Integer getNextNegativeEncounterNumber();
    
    PaginationResult<Observation> findObservations(FactSearchDto factSearchDto);
    
    List<Observation> findObservationModifiers(FactSearchDto factSearchDto);

    int getTotalCount(FactSearchDto searchDto);
}
