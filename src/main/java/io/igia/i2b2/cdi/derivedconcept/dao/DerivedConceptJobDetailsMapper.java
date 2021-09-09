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
import java.sql.Timestamp;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsDto;
import io.igia.i2b2.cdi.derivedconcept.dto.Status;

@Component
public class DerivedConceptJobDetailsMapper implements RowMapper<DerivedConceptJobDetailsDto> {

    @Override
    public DerivedConceptJobDetailsDto mapRow(ResultSet rs, int rowNum) throws SQLException {
	DerivedConceptJobDetailsDto derivedConceptJobDetailsDto = new DerivedConceptJobDetailsDto();
	derivedConceptJobDetailsDto.setId(rs.getInt(DerivedConceptJobDetailsDaoImpl.ID));
	derivedConceptJobDetailsDto.setDerivedConceptId(rs.getInt(DerivedConceptJobDetailsDaoImpl.DERIVED_CONCEPT_ID));
	derivedConceptJobDetailsDto.setErrorStack(rs.getString(DerivedConceptJobDetailsDaoImpl.ERROR_STACK));
	derivedConceptJobDetailsDto
		.setDerivedConceptSql(rs.getString(DerivedConceptJobDetailsDaoImpl.DERIVED_CONCEPT_SQL));
	derivedConceptJobDetailsDto.setStatus(Status.valueOf(rs.getString(DerivedConceptJobDetailsDaoImpl.STATUS)));

	Timestamp startedOn = rs.getTimestamp(DerivedConceptJobDetailsDaoImpl.STARTED_ON);
	derivedConceptJobDetailsDto.setStartedOn((startedOn != null) ? startedOn.toInstant() : null);

	Timestamp completedOn = rs.getTimestamp(DerivedConceptJobDetailsDaoImpl.COMPLETED_ON);
	derivedConceptJobDetailsDto.setCompletedOn((completedOn != null) ? completedOn.toInstant() : null);
	return derivedConceptJobDetailsDto;
    }
}
