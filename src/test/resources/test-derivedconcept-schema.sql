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

DROP TABLE IF EXISTS derived_concept_definition;
CREATE TABLE derived_concept_definition
(
    id SERIAL NOT NULL,
    concept_path character varying(700),
    sql_query text,
    unit_cd character varying(50),
    description text,
    update_date timestamp without time zone,
    CONSTRAINT pk_derived_fact_definition PRIMARY KEY (id),
    CONSTRAINT uk_concept_path UNIQUE (concept_path)
);

DROP TABLE IF EXISTS derived_concept_dependency;
CREATE TABLE derived_concept_dependency
(
    id SERIAL NOT NULL,
    derived_concept_id integer,
    parent_concept_path  character varying(700)
);