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



package io.igia.i2b2.cdi.ontology.dao;

import java.util.List;

import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyDto;

public interface OntologyConceptDao {

    List<OntologyConceptDto> findOntologyConcepts(OntologyConceptSearchDto ontologyConceptSearchDto);

    int addOntologyToI2b2(OntologyDto ontology);

    int addOntologyToTableAccess(OntologyDto ontology);

    int deleteOntologyFromI2b2(OntologyDto ontology);

    int deleteOntologyFromTableAccess(OntologyDto ontology);

    int updateOntologyToI2b2(OntologyConceptSearchDto ontologyConceptSearchDto);

    int updateOntologyToTableAccess(OntologyConceptSearchDto ontologyConceptSearchDto);

    List<OntologyConceptDto> findOntologyConceptsByLevel(OntologyConceptSearchDto ontologyConceptSearchDto);
}
