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


package io.igia.i2b2.cdi.observation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel(value = "Fact")
public class FactDto {

    @Size(max = 200)
    @ApiModelProperty(value = "Encounter identifier in the source system")
    private String encounterId;

    @NotBlank
    @Size(max = 200)
    @ApiModelProperty(value = "Patient identifier in the source system", required = true)
    private String patientId;

    @Size(max = 50)
    @ApiModelProperty(value = "Provider identifier in the i2b2 data mart")
    private String providerId;

    @NotBlank
    @Size(max = 50)
    @ApiModelProperty(value = "Concept code in the i2b2 data mart", required = true)
    private String conceptCode;

    @ApiModelProperty(value = "Start date of fact", required = true)
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startDate;

    @Positive
    @Min(value = 1)
    @ApiModelProperty(value = "positive number to distinguish similar facts. Defaults to 1")
    @JsonIgnore
    private Integer instanceNum;

    @ApiModelProperty(value = "Value recorded")
    private String value;

    @Size(max = 50)
    @ApiModelProperty(value = "Units")
    private String units;

    @ApiModelProperty(value = "End date of fact")
    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endDate;

    @JsonIgnore
    private Integer patientNum;
    @JsonIgnore
    private Integer encounterNum;
    @JsonIgnore
    private String sourceSystemCode;

    private List<FactModifierDto> modifiers = new ArrayList<>();

    public FactDto() {
    }

    public FactDto(FactDto factDto) {
        setProviderId(factDto.getProviderId());
        setUnits(factDto.getUnits());
        setValue(factDto.getValue());
        setEndDate(factDto.getEndDate());
        setPatientId(factDto.getPatientId());
        setEncounterId(factDto.getEncounterId());
        setConceptCode(factDto.getConceptCode());
        setStartDate(factDto.getStartDate());
        setInstanceNum(factDto.getInstanceNum());
        setPatientNum(factDto.getPatientNum());
        setEncounterNum(factDto.getEncounterNum());
        setSourceSystemCode(factDto.getSourceSystemCode());
        setModifiers(factDto.getModifiers().stream().map(FactModifierDto::new).collect(Collectors.toList()));
    }

    public String getEncounterId() {
        return encounterId;
    }

    public void setEncounterId(String encounterId) {
        this.encounterId = encounterId;
    }

    public Integer getEncounterNum() {
        return encounterNum;
    }

    public void setEncounterNum(Integer encounterNum) {
        this.encounterNum = encounterNum;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public Integer getPatientNum() {
        return patientNum;
    }

    public void setPatientNum(Integer patientNum) {
        this.patientNum = patientNum;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Integer getInstanceNum() {
        return instanceNum;
    }

    public void setInstanceNum(Integer instanceNum) {
        this.instanceNum = instanceNum;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getSourceSystemCode() {
        return sourceSystemCode;
    }

    public void setSourceSystemCode(String sourceSystemCode) {
        this.sourceSystemCode = sourceSystemCode;
    }

    public List<FactModifierDto> getModifiers() {
        return modifiers;
    }

    public void setModifiers(List<FactModifierDto> modifiers) {
        this.modifiers = modifiers;
    }

    public void addModifier(FactModifierDto modifier) {
        this.modifiers.add(modifier);
    }
}
