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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ConceptUtilTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    ConceptUtil conceptUtil;

    @Before
    public void setUp() {
	conceptUtil = new ConceptUtil();
    }

    @Test
    public void testValidateConceptPath_nullConceptPath() {
	thrown.expect(IllegalArgumentException.class);
	thrown.expectMessage("Concept path is empty or null");
	conceptUtil.validateConceptPath(null);
    }

    @Test
    public void testValidateConceptPath_invalidSeparator() {
	thrown.expect(IllegalArgumentException.class);
	thrown.expectMessage("Concept path should not contain '/' as separator");
	conceptUtil.validateConceptPath("/Derived/test/");
    }

    @Test
    public void testValidateConceptPath_invalidConceptPathStart() {
	thrown.expect(IllegalArgumentException.class);
	thrown.expectMessage("Concept path should start with '\'");
	conceptUtil.validateConceptPath("Derivedtest");
    }

    @Test
    public void testValidateConceptPath_invalidConceptPathEnd() {
	thrown.expect(IllegalArgumentException.class);
	thrown.expectMessage("Concept path should end with '\'");
	conceptUtil.validateConceptPath("\\Derivedtest");
    }

    @Test
    public void testRemoveSeparatorAtFirstAndLast() {
	String expectedConceptPath = "Derived\\test";
	String actualConceptPath = conceptUtil.removeSeparatorAtFirstAndLast("\\Derived\\test\\");
	assertThat(actualConceptPath).isNotNull().isEqualTo(expectedConceptPath);
    }

    @Test
    public void testGetLevel() {
	int actualLevel = conceptUtil.getLevel("Derived\\test");
	assertThat(actualLevel).isNotNull().isEqualTo(1);
    }

    @Test
    public void testGetFullPath() {
	String expectedConceptPath = "\\Derived\\test\\";
	String actualConceptPath = conceptUtil.getFullPath("Derived\\test");
	assertThat(actualConceptPath).isNotNull().isEqualTo(expectedConceptPath);
    }

    @Test
    public void testGetConceptName() {
	String expectedConceptName = "test";
	String actualConceptName = conceptUtil.getConceptName("\\Derived\\test\\");
	assertThat(actualConceptName).isNotNull().isEqualTo(expectedConceptName);
    }

    @Test
    public void testGetVisualAttributes() {
	String expectedVisualAttribute = "FA";
	String actualVisualAttribute = conceptUtil.getVisualAttributes("\\Derived\\test\\");
	assertThat(actualVisualAttribute).isNotNull().isEqualTo(expectedVisualAttribute);
    }

    @Test
    public void testGetToolTip() {
	String expectedToolTip = " \\ Derived \\ test \\ ";
	String actualToolTip = conceptUtil.getToolTip("\\Derived\\test\\");
	assertThat(actualToolTip).isNotNull().isEqualTo(expectedToolTip);
    }

    @Test
    public void testGetDimCode() {
	String expectedDimCode = "derived";
	String actualDimCode = conceptUtil.getDimCode("derived", "\\Derived\\test\\");
	assertThat(actualDimCode).isNotNull().isEqualTo(expectedDimCode);
    }

    @Test
    public void testGetOperator() {
	String expectedOperator = "LIKE";
	String actualOperator = conceptUtil.getOperator("LIKE");
	assertThat(actualOperator).isNotNull().isEqualTo(expectedOperator);
    }

    @Test
    public void testGetColumnDataType() {
	String expectedColumnDataType = "T";
	String actualColumnDataType = conceptUtil.getColumnDataType("TEXTUAL", "concept_dimension");
	assertThat(actualColumnDataType).isNotNull().isEqualTo(expectedColumnDataType);
    }

    @Test
    public void testGetColumnName() {
	String expectedColumnName = "concept_code";
	String actualColumnName = conceptUtil.getColumnName("concept_code");
	assertThat(actualColumnName).isNotNull().isEqualTo(expectedColumnName);
    }

    @Test
    public void testGetTableName() {
	String expectedTableName = "concept_dimension";
	String actualTableName = conceptUtil.getTableName("concept_dimension");
	assertThat(actualTableName).isNotNull().isEqualTo(expectedTableName);
    }

    @Test
    public void testGetFactTableColumnName() {
	String expectedColumnName = "concept_code";
	String actualColumnName = conceptUtil.getFactTableColumnName("concept_code");
	assertThat(actualColumnName).isNotNull().isEqualTo(expectedColumnName);
    }

    @Test
    public void testGetMetadataXml() {
	String expectedMetadataXml = "<?xml version=\"1.0\"?><ValueMetadata><Version>3.02</Version><CreationDateTime>04/15/2007 01:22:23</CreationDateTime><TestID>Common</TestID><TestName>Common</TestName><DataType>PosFloat</DataType><CodeType>GRP</CodeType><Loinc>2090-9</Loinc><Flagstouse></Flagstouse><Oktousevalues>Y</Oktousevalues><MaxStringLength></MaxStringLength><LowofLowValue></LowofLowValue><HighofLowValue></HighofLowValue><LowofHighValue></LowofHighValue><HighofHighValue></HighofHighValue><LowofToxicValue></LowofToxicValue><HighofToxicValue></HighofToxicValue><EnumValues></EnumValues><CommentsDeterminingExclusion><Com></Com></CommentsDeterminingExclusion><UnitValues><NormalUnits></NormalUnits><EqualUnits></EqualUnits><ExcludingUnits></ExcludingUnits><ConvertingUnits><Units></Units><MultiplyingFactor></MultiplyingFactor></ConvertingUnits></UnitValues><Analysis><Enums /><Counts /><New /></Analysis></ValueMetadata>";
	String actualMetadataXml = conceptUtil.getMetadataXml("N");
	assertThat(actualMetadataXml).isNotNull().isEqualTo(expectedMetadataXml);
    }

    @Test
    public void testGetConceptPath() {
	String expectedConceptPath = "\\derived\\test\\";
	String actualConceptPath = conceptUtil.getConceptPath("derived/test");
	assertThat(actualConceptPath).isNotNull().isEqualTo(expectedConceptPath);
    }
}
