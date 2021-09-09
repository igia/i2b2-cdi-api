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


package io.igia.i2b2.cdi.derivedconcept.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDependencyDto;

@Component
public class DerivedConceptDependencyMapper implements RowMapper<DerivedConceptDependencyDto> {

    @Override
    public DerivedConceptDependencyDto mapRow(ResultSet rs, int rowNum) throws SQLException {
	DerivedConceptDependencyDto derivedConceptDependencyDto = new DerivedConceptDependencyDto();
	derivedConceptDependencyDto.setId(rs.getInt(DerivedConceptDependencyDaoImpl.ID));
	derivedConceptDependencyDto.setDerivedConceptId(rs.getInt(DerivedConceptDependencyDaoImpl.DERIVED_CONCEPT_ID));
	derivedConceptDependencyDto
		.setParentConceptPath(rs.getString(DerivedConceptDependencyDaoImpl.PARENT_CONCEPT_PATH));
	derivedConceptDependencyDto
		.setDerivedConceptPath(rs.getString(DerivedConceptDependencyDaoImpl.DERIVED_CONCEPT_PATH));
	return derivedConceptDependencyDto;
    }
}
