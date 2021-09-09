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


package io.igia.i2b2.cdi.ontology.dto;

import io.igia.i2b2.cdi.derivedconcept.dto.DerivedConceptDto;

public class OntologyDto extends DerivedConceptDto {
    private Integer chLevel;
    private String cName;
    private String cSynonymCd;
    private String cVisualAttributes;
    private String cFactTableColumn;
    private String cTableName;
    private String cColumnName;
    private String cColumnDatatype;
    private String cOperator;
    private String cDimcode;
    private String cTooltip;
    private String mAppliedPath;
    private String updateDate;
    private String sourceSystemCd;
    private String cTableCd;
    private String accessTableName;
    private String cProtectedAccess;

    public String getcName() {
	return cName;
    }

    public void setcName(String cName) {
	this.cName = cName;
    }

    public String getcSynonymCd() {
	return cSynonymCd;
    }

    public void setcSynonymCd(String cSynonymCd) {
	this.cSynonymCd = cSynonymCd;
    }

    public String getcVisualAttributes() {
	return cVisualAttributes;
    }

    public void setcVisualAttributes(String cVisualAttributes) {
	this.cVisualAttributes = cVisualAttributes;
    }

    public String getcFactTableColumn() {
	return cFactTableColumn;
    }

    public void setcFactTableColumn(String cFactTableColumn) {
	this.cFactTableColumn = cFactTableColumn;
    }

    public String getcTableName() {
	return cTableName;
    }

    public void setcTableName(String cTableName) {
	this.cTableName = cTableName;
    }

    public String getcColumnName() {
	return cColumnName;
    }

    public void setcColumnName(String cColumnName) {
	this.cColumnName = cColumnName;
    }

    public String getcColumnDatatype() {
	return cColumnDatatype;
    }

    public void setcColumnDatatype(String cColumnDatatype) {
	this.cColumnDatatype = cColumnDatatype;
    }

    public String getcOperator() {
	return cOperator;
    }

    public void setcOperator(String cOperator) {
	this.cOperator = cOperator;
    }

    public String getcDimcode() {
	return cDimcode;
    }

    public void setcDimcode(String cDimcode) {
	this.cDimcode = cDimcode;
    }

    public String getcTooltip() {
	return cTooltip;
    }

    public void setcTooltip(String cTooltip) {
	this.cTooltip = cTooltip;
    }

    public String getmAppliedPath() {
	return mAppliedPath;
    }

    public void setmAppliedPath(String mAppliedPath) {
	this.mAppliedPath = mAppliedPath;
    }

    public String getUpdateDate() {
	return updateDate;
    }

    public void setUpdateDate(String updateDate) {
	this.updateDate = updateDate;
    }

    public String getSourceSystemCd() {
	return sourceSystemCd;
    }

    public void setSourceSystemCd(String sourceSystemCd) {
	this.sourceSystemCd = sourceSystemCd;
    }

    public String getcTableCd() {
	return cTableCd;
    }

    public void setcTableCd(String cTableCd) {
	this.cTableCd = cTableCd;
    }

    public String getAccessTableName() {
	return accessTableName;
    }

    public void setAccessTableName(String accessTableName) {
	this.accessTableName = accessTableName;
    }

    public String getcProtectedAccess() {
	return cProtectedAccess;
    }

    public void setcProtectedAccess(String cProtectedAccess) {
	this.cProtectedAccess = cProtectedAccess;
    }

    public Integer getChLevel() {
	return chLevel;
    }

    public void setChLevel(Integer chLevel) {
	this.chLevel = chLevel;
    }
}
