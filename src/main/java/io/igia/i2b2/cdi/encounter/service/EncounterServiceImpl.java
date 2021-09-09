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



package io.igia.i2b2.cdi.encounter.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.common.exception.I2b2Exception;
import io.igia.i2b2.cdi.config.ApplicationProperties;
import io.igia.i2b2.cdi.encounter.dao.EncounterDao;
import io.igia.i2b2.cdi.encounter.dto.EncounterDto;
import io.igia.i2b2.cdi.encounter.dto.EncounterSearchDto;

@Service
@Transactional(readOnly = true)
public class EncounterServiceImpl implements EncounterService {

    private final EncounterDao encounterDao;
    private final ApplicationProperties applicationProperties;

    public EncounterServiceImpl(EncounterDao encounterDao, ApplicationProperties applicationProperties) {
        this.encounterDao = encounterDao;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public List<EncounterDto> getEncounters(EncounterSearchDto inEncounterSearchDto) {
        EncounterSearchDto encounterSearchDto = new EncounterSearchDto(inEncounterSearchDto);
        if (StringUtils.isEmpty(encounterSearchDto.getProjectId())) {
            encounterSearchDto.setProjectId(applicationProperties.getProjectId());
        }

        return encounterDao.findEncounters(encounterSearchDto);
    }

    @Override
    @Transactional(readOnly = false)
    public EncounterDto addEncounter(EncounterDto inEncounterDto) {
        EncounterDto encounterDto = new EncounterDto(inEncounterDto);

        if (StringUtils.isEmpty(encounterDto.getProjectId())) {
            encounterDto.setProjectId(applicationProperties.getProjectId());
        }
        if (StringUtils.isEmpty(encounterDto.getSource())) {
            encounterDto.setSource(applicationProperties.getSourceSystemCode());
        }
        if (StringUtils.isEmpty(encounterDto.getPatientSource())) {
            encounterDto.setPatientSource(applicationProperties.getPatientSource());
        }
        if (StringUtils.isEmpty(encounterDto.getEncounterSource())) {
            encounterDto.setEncounterSource(applicationProperties.getEncounterSource());
        }
        if (StringUtils.isEmpty(encounterDto.getEncounterStatus())) {
            encounterDto.setEncounterStatus(applicationProperties.getEncounterStatus());
        }

        encounterDto.setEncounterNum(encounterDao.getNextEncounterNumber());

        int updateCount = encounterDao.addEncounterMapping(encounterDto);
        if (updateCount == 0) {
            throw new I2b2Exception("Could not add encounter record.");
        }
        updateCount = encounterDao.addEncounter(encounterDto);
        if (updateCount == 0) {
            throw new I2b2Exception("Could not add encounter record.");
        }
        return encounterDto;
    }

    @Override
    public List<EncounterDto> getEncounterByEncounterNum(EncounterSearchDto encounterSearchDto) {
        return encounterDao.findEncounterByEncounterNum(encounterSearchDto);
    }
}
