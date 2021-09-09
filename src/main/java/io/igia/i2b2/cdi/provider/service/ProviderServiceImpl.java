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



package io.igia.i2b2.cdi.provider.service;

import io.igia.i2b2.cdi.common.exception.I2b2DataValidationException;
import io.igia.i2b2.cdi.provider.dao.ProviderDao;
import io.igia.i2b2.cdi.provider.dto.ProviderDto;
import io.igia.i2b2.cdi.provider.dto.ProviderSearchDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProviderServiceImpl implements ProviderService {

    private final ProviderDao providerDao;

    public ProviderServiceImpl(ProviderDao providerDao) {
        this.providerDao = providerDao;
    }

    @Override
    public List<ProviderDto> getProviders(ProviderSearchDto inProviderSearchDto) {
        ProviderSearchDto providerSearchDto = new ProviderSearchDto(inProviderSearchDto);
        return providerDao.findProviders(providerSearchDto);
    }

    @Override
    public void validate(ProviderSearchDto providerSearchDto) {
        if (getProviders(providerSearchDto).isEmpty()) {
            throw new I2b2DataValidationException("Invalid provider identifier.");
        }
    }
}
