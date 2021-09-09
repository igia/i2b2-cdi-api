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
DROP TABLE IF EXISTS derived_concept_job_details;
CREATE TABLE derived_concept_job_details
(
    id SERIAL NOT NULL,
    derived_concept_id integer,
    error_stack text,
    derived_concept_sql text,
    status character varying(20),
    started_on timestamp without time zone,
    completed_on timestamp without time zone
);