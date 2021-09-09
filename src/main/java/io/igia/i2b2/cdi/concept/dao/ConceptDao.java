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

package io.igia.i2b2.cdi.concept.dao;

import io.igia.i2b2.cdi.common.dto.PaginationResult;
import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import io.igia.i2b2.cdi.concept.dto.ConceptSearchDto;

public interface ConceptDao {
    PaginationResult<ConceptDto> findConcepts(ConceptSearchDto conceptSearchDto);

    int addConcept(ConceptDto concept);

    int updateConcept(ConceptDto conceptRecord, String existingConceptPath);

    int deleteConcept(ConceptDto concept);
    
    int getTotalCount(ConceptSearchDto conceptSearchDto);
}
