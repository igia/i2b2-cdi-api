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


package io.igia.i2b2.cdi.observation.service;

import io.igia.i2b2.cdi.observation.domain.Observation;
import io.igia.i2b2.cdi.observation.domain.ObservationModifier;
import io.igia.i2b2.cdi.observation.dto.FactDto;
import io.igia.i2b2.cdi.observation.dto.FactModifierDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ObservationMapper {

    @Mapping(source = "instanceNum", target = "instanceNumber")
    Observation observationFromDto(FactDto factDto);

    ObservationModifier observationModifierFromDto(FactModifierDto factModifierDto);
}
