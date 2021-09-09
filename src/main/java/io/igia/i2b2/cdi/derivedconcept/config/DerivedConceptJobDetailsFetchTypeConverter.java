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

package io.igia.i2b2.cdi.derivedconcept.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import io.igia.i2b2.cdi.common.exception.I2b2DataValidationException;
import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptJobDetailsFetchType;

@Component
public class DerivedConceptJobDetailsFetchTypeConverter
	implements Converter<String, DerivedConceptJobDetailsFetchType> {

    @Override
    public DerivedConceptJobDetailsFetchType convert(String source) {
	try {
	    return DerivedConceptJobDetailsFetchType.valueOf(source.toUpperCase());
	} catch (IllegalArgumentException e) {
	    throw new I2b2DataValidationException("Invalid fetchType provided. Please provide LATEST or ALL");
	}
    }
}
