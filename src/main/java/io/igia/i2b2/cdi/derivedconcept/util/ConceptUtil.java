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



package io.igia.i2b2.cdi.derivedconcept.util;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import io.igia.i2b2.cdi.derivedconcept.dto.MetadataProperties;
import io.igia.i2b2.cdi.derivedconcept.dto.ValueType;

@Component
public class ConceptUtil {

    private static final String META_DATA_XML = "<?xml version=\"1.0\"?><ValueMetadata><Version>3.02</Version><CreationDateTime>04/15/2007 01:22:23</CreationDateTime><TestID>Common</TestID><TestName>Common</TestName><DataType>PosFloat</DataType><CodeType>GRP</CodeType><Loinc>2090-9</Loinc><Flagstouse></Flagstouse><Oktousevalues>Y</Oktousevalues><MaxStringLength></MaxStringLength><LowofLowValue></LowofLowValue><HighofLowValue></HighofLowValue><LowofHighValue></LowofHighValue><HighofHighValue></HighofHighValue><LowofToxicValue></LowofToxicValue><HighofToxicValue></HighofToxicValue><EnumValues></EnumValues><CommentsDeterminingExclusion><Com></Com></CommentsDeterminingExclusion><UnitValues><NormalUnits></NormalUnits><EqualUnits></EqualUnits><ExcludingUnits></ExcludingUnits><ConvertingUnits><Units></Units><MultiplyingFactor></MultiplyingFactor></ConvertingUnits></UnitValues><Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>";
    private static final String NUMERIC = "N";
    private static final String CONCEPT_DIMENSION = "concept_dimension";
    private static final String FA = "FA";
    private static final String CA = "CA";

    public String removeSeparatorAtFirstAndLast(String path) {
	if (!StringUtils.isEmpty(path)) {
	    path = path.startsWith(MetadataProperties.I2B2_SEPARATOR) ? path.substring(1) : path;
	    path = path.endsWith(MetadataProperties.I2B2_SEPARATOR) ? path.substring(0, path.length() - 1) : path;
	    return path;
	}
	return path;
    }

    public Integer getLevel(String path) {
	if (!StringUtils.isEmpty(path)) {
	    return StringUtils.countOccurrencesOf(path, MetadataProperties.I2B2_SEPARATOR);
	}
	return -1;
    }

    public String getFullPath(String path) {
	if (!StringUtils.isEmpty(path)) {
	    path = path.startsWith(MetadataProperties.I2B2_SEPARATOR) ? path : MetadataProperties.I2B2_SEPARATOR + path;
	    path = path.endsWith(MetadataProperties.I2B2_SEPARATOR) ? path : path + MetadataProperties.I2B2_SEPARATOR;
	    return path;
	}
	return path;
    }

    public String getConceptName(String path) {
	if (!StringUtils.isEmpty(path)) {
	    String[] components = path.split(Pattern.quote(MetadataProperties.I2B2_SEPARATOR));
	    return components[components.length - 1];
	}
	return path;
    }

    public String getVisualAttributes(String path) {
	return path.contains(MetadataProperties.I2B2_SEPARATOR) ? FA : CA;
    }

    public String getToolTip(String path) {
	if (!StringUtils.isEmpty(path)) {
	    return path.replace(MetadataProperties.I2B2_SEPARATOR, " " + MetadataProperties.I2B2_SEPARATOR + " ");
	}
	return path;
    }

    public String getDimCode(String dimCode, String path) {
	return !StringUtils.isEmpty(dimCode) ? dimCode : getFullPath(path);
    }

    public String getOperator(String operator) {
	return !StringUtils.isEmpty(operator) ? operator : MetadataProperties.OPERATOR;
    }

    public String getColumnDataType(String columnDataType, String tableName) {
	if (!StringUtils.isEmpty(columnDataType) && columnDataType.equalsIgnoreCase(ValueType.NUMERIC.getValType())
		&& !tableName.equalsIgnoreCase(CONCEPT_DIMENSION)) {
	    return columnDataType;
	} else {
	    return MetadataProperties.COLUMN_DATA_TYPE;
	}
    }

    public String getColumnName(String columnName) {
	return !StringUtils.isEmpty(columnName) ? columnName : MetadataProperties.CONCEPT_PATH;
    }

    public String getTableName(String tableName) {
	return !StringUtils.isEmpty(tableName) ? tableName : MetadataProperties.TABLE_NAME;
    }

    public String getFactTableColumnName(String factTableColumn) {
	return !StringUtils.isEmpty(factTableColumn) ? factTableColumn : MetadataProperties.FACT_TABLE_COLUMN;
    }

    public String getMetadataXml(String conceptType) {
	return (!StringUtils.isEmpty(conceptType) && conceptType.equals(NUMERIC)) ? META_DATA_XML : "";
    }

    public String getConceptPath(String path) {
	if (!StringUtils.isEmpty(path)) {
	    path = path.replace("/", MetadataProperties.I2B2_SEPARATOR);
	    return getFullPath(path);
	}
	return path;
    }

    public void validateConceptPath(String conceptPath) {
	if (StringUtils.isEmpty(conceptPath))
	    throw new IllegalArgumentException("Concept path is empty or null");

	if (conceptPath.contains("/"))
	    throw new IllegalArgumentException("Concept path should not contain '/' as separator");

	if (!conceptPath.startsWith(MetadataProperties.I2B2_SEPARATOR))
	    throw new IllegalArgumentException("Concept path should start with '\'");

	if (!conceptPath.endsWith(MetadataProperties.I2B2_SEPARATOR))
	    throw new IllegalArgumentException("Concept path should end with '\'");
    }
}
