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


package io.igia.i2b2.cdi.ontology.service;

import java.util.List;

import io.igia.i2b2.cdi.ontology.dto.OntologyConceptDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyConceptSearchDto;
import io.igia.i2b2.cdi.ontology.dto.OntologyDto;

public interface OntologyConceptService {

    List<OntologyConceptDto> getOntologyConcepts(OntologyConceptSearchDto ontologyConceptSearchDto);

    List<OntologyConceptDto> getOntologyConceptsWithDataType(OntologyConceptSearchDto inOntologyConceptSearchDto);

    OntologyDto addOntology(OntologyDto ontologyDto);

    OntologyDto updateOntology(OntologyDto ontology, String existingConceptPath);

    OntologyDto deleteOntology(OntologyDto ontologyDto);

    List<OntologyConceptDto> findOntologyConcepts(OntologyConceptSearchDto ontologyConceptSearchDto);

    List<OntologyConceptDto> getOntologyConceptsByLevel(OntologyConceptSearchDto inOntologyConceptSearchDto);
}
