-- 
--  This Source Code Form is subject to the terms of the Mozilla Public License, v.
--  2.0 with a Healthcare Disclaimer.
--  A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
--  be found under the top level directory, named LICENSE.
--  If a copy of the MPL was not distributed with this file, You can obtain one at
--  http://mozilla.org/MPL/2.0/.
--  If a copy of the Healthcare Disclaimer was not distributed with this file, You
--  can obtain one at the project website https://github.com/igia.
-- 
--  Copyright (C) 2021-2022 Persistent Systems, Inc.
--
DROP TABLE IF EXISTS provider_dimension;
CREATE TABLE provider_dimension (
    provider_id character varying(50) NOT NULL,
    provider_path character varying(700) NOT NULL,
    name_char character varying(850),
    sourcesystem_cd character varying(50),
    CONSTRAINT provider_dimension_pk PRIMARY KEY (provider_path, provider_id)
);

DROP TABLE IF EXISTS concept_dimension;
CREATE TABLE concept_dimension
(
    concept_path character varying(700) NOT NULL,
    concept_cd character varying(50),
    name_char character varying(2000),
    sourcesystem_cd character varying(50),
    update_date timestamp without time zone,
    CONSTRAINT concept_dimension_pk PRIMARY KEY (concept_path)
);

DROP TABLE IF EXISTS modifier_dimension;
CREATE TABLE modifier_dimension
(
    modifier_path character varying(700) NOT NULL,
    modifier_cd character varying(50),
    name_char character varying(2000),
    sourcesystem_cd character varying(50) ,
    CONSTRAINT modifier_dimension_pk PRIMARY KEY (modifier_path)
);

DROP TABLE IF EXISTS observation_fact;
CREATE TABLE observation_fact
(
    encounter_num integer NOT NULL,
    patient_num integer NOT NULL,
    concept_cd character varying(50) NOT NULL,
    provider_id character varying(50) NOT NULL,
    start_date timestamp without time zone NOT NULL,
    modifier_cd character varying(100) NOT NULL DEFAULT '@'::character varying,
    instance_num integer NOT NULL DEFAULT 1,
    valtype_cd character varying(50),
    tval_char character varying(255),
    nval_num numeric(18,5),
    valueflag_cd character varying(50),
    quantity_num numeric(18,5),
    units_cd character varying(50),
    end_date timestamp without time zone,
    location_cd character varying(50),
    observation_blob text,
    confidence_num numeric(18,5),
    update_date timestamp without time zone,
    download_date timestamp without time zone,
    import_date timestamp without time zone,
    sourcesystem_cd character varying(50),
    CONSTRAINT observation_fact_pk PRIMARY KEY (patient_num, concept_cd, modifier_cd, start_date, encounter_num, instance_num, provider_id)
);

DROP TABLE IF EXISTS patient_mapping;
CREATE TABLE patient_mapping
(
    patient_ide character varying(200) NOT NULL,
    patient_ide_source character varying(50) NOT NULL,
    patient_num integer NOT NULL,
    patient_ide_status character varying(50),
    project_id character varying(50) NOT NULL,
    update_date timestamp without time zone,
    sourcesystem_cd character varying(50),
    CONSTRAINT patient_mapping_pk PRIMARY KEY (patient_ide, patient_ide_source, project_id)
);

DROP TABLE IF EXISTS patient_dimension;
CREATE TABLE patient_dimension
(
    patient_num integer NOT NULL,
    update_date timestamp without time zone,
    sourcesystem_cd character varying(50),
    CONSTRAINT patient_dimension_pk PRIMARY KEY (patient_num)
);

DROP TABLE IF EXISTS encounter_mapping;
CREATE TABLE encounter_mapping
(
    encounter_ide character varying(200) NOT NULL,
    encounter_ide_source character varying(50) NOT NULL,
    project_id character varying(50) NOT NULL,
    encounter_num integer NOT NULL,
    patient_ide character varying(200) NOT NULL,
    patient_ide_source character varying(50) NOT NULL,
    encounter_ide_status character varying(50),
    update_date timestamp without time zone,
    sourcesystem_cd character varying(50),
    CONSTRAINT encounter_mapping_pk PRIMARY KEY (encounter_ide, encounter_ide_source, project_id, patient_ide, patient_ide_source)
);

DROP TABLE IF EXISTS visit_dimension;
CREATE TABLE visit_dimension
(
    encounter_num integer NOT NULL,
    patient_num integer NOT NULL,
    update_date timestamp without time zone,
    sourcesystem_cd character varying(50),
    CONSTRAINT visit_dimension_pk PRIMARY KEY (encounter_num, patient_num)
);


