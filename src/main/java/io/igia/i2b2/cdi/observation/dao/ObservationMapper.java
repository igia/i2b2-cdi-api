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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import io.igia.i2b2.cdi.observation.domain.Observation;
import io.igia.i2b2.cdi.observation.domain.ObservationModifier;
import io.igia.i2b2.cdi.observation.domain.ValueTypeCode;

@Component
public class ObservationMapper implements RowMapper<Observation> {

    public Observation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Observation observation = new Observation();
        observation.setEncounterNum(rs.getInt(ObservationDaoImpl.ENCOUNTER_NUM));
        observation.setPatientNum(rs.getInt(ObservationDaoImpl.PATIENT_NUM));
        observation.setConceptCode(rs.getString(ObservationDaoImpl.CONCEPT_CODE));
        observation.setProviderId(rs.getString(ObservationDaoImpl.PROVIDER_ID));
        observation.setStartDate(rs.getTimestamp(ObservationDaoImpl.START_DATE).toLocalDateTime());
        observation.setInstanceNumber(rs.getInt(ObservationDaoImpl.INSTANCE_NUM));
        if (rs.getTimestamp(ObservationDaoImpl.END_DATE) != null) {
            observation.setEndDate(rs.getTimestamp(ObservationDaoImpl.END_DATE).toLocalDateTime());
        }
              
        ObservationModifier observationModifier = new ObservationModifier();
        observationModifier.setModifierCode(rs.getString(ObservationDaoImpl.MODIFIER_CODE));
        observationModifier.setValueTypeCode(ValueTypeCode.valueOfCode(rs.getString(ObservationDaoImpl.VALTYPE_CODE)));
        observationModifier.setTextValue(rs.getString(ObservationDaoImpl.TEXT_VAL));
        observationModifier.setNumberValue(rs.getDouble(ObservationDaoImpl.NUMERIC_VAL));
        observationModifier.setUnits(rs.getString(ObservationDaoImpl.UNITS));
        observationModifier.setBlob(rs.getString(ObservationDaoImpl.OBSERVATION_BLOB));
        observation.addModifier(observationModifier);
        return observation;
    }
}
