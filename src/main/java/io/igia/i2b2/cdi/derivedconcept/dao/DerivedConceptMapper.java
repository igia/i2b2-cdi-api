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

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;

@Component
public class DerivedConceptMapper implements RowMapper<DerivedConceptDto> {
    public DerivedConceptDto mapRow(ResultSet resultSet, int rowNum) throws SQLException {
	DerivedConceptDto concept = new DerivedConceptDto();
	concept.setId(resultSet.getInt(DerivedConceptDaoImpl.ID));
	concept.setPath(resultSet.getString(DerivedConceptDaoImpl.CONCEPT_PATH));
	concept.setCode(resultSet.getString(DerivedConceptDaoImpl.CONCEPT_CODE));
	concept.setDescription(resultSet.getString(DerivedConceptDaoImpl.DERIVED_CONCEPT_DESCRIPTION));
	concept.setFactQuery(resultSet.getString(DerivedConceptDaoImpl.DERIVED_FACT_QUERY));
	concept.setUnit(resultSet.getString(DerivedConceptDaoImpl.UNIT_CD));
	concept.setUpdatedOn(resultSet.getTimestamp(DerivedConceptDaoImpl.UPDATED_ON).toInstant());
	return concept;
    }
}