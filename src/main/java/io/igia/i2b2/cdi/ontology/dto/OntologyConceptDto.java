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

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.igia.i2b2.cdi.concept.dto.ConceptDataType;

public class OntologyConceptDto {
    private String fullName;
    private String metadataXml;

    @JsonIgnore
    private ConceptDataType dataType;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMetadataXml() {
        return metadataXml;
    }

    public void setMetadataXml(String metadataXml) {
        this.metadataXml = metadataXml;
    }

    public ConceptDataType getDataType() {
        return dataType;
    }

    public void setDataType(ConceptDataType dataType) {
        this.dataType = dataType;
    }
}
