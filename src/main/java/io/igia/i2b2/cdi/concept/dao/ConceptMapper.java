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

import io.igia.i2b2.cdi.concept.dto.ConceptDto;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ConceptMapper implements RowMapper<ConceptDto> {

    public ConceptDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ConceptDto concept = new ConceptDto();
        concept.setCode(resultSet.getString(ConceptDaoImpl.CONCEPT_CODE));
        concept.setName(resultSet.getString(ConceptDaoImpl.CONCEPT_NAME));
        concept.setConceptPath(resultSet.getString(ConceptDaoImpl.CONCEPT_PATH));
        concept.setSource(resultSet.getString(ConceptDaoImpl.CONCEPT_SOURCE));
        return concept;
    }
}
