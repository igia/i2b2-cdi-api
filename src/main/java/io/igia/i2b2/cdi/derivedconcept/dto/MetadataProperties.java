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

package io.igia.i2b2.cdi.derivedconcept.dto;

public class MetadataProperties {

	private MetadataProperties() {
		throw new IllegalStateException("Constants class");
	}

	public static final String SYNONYM_CD = "N";
	public static final String FACT_TABLE_COLUMN = "concept_cd";
	public static final String TABLE_NAME = "concept_dimension";
	public static final String COLUMN_NAME = "concept_path";
	public static final String COLUMN_DATA_TYPE = "T";
	public static final String OPERATOR = "LIKE";
	public static final String TABLE_ACCESS_CD = "i2b2_DEMO";
	public static final String TABLE_ACCESS_NAME = "I2B2";
	public static final String PROTECTED_ACCESS = "N";
	public static final String I2B2_SEPARATOR = "\\";
	public static final String CONCEPT_PATH = "concept_path";
	public static final String APPLIED_PATH = "@";
}
