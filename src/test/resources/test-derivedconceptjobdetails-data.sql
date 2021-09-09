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


INSERT INTO derived_concept_job_details(derived_concept_id, error_stack, derived_concept_sql, status, started_on, completed_on) VALUES (1,'', 'Select * from table1', 'COMPLETED', '2010-10-10 00:00:00', '2010-10-10 00:00:00');
INSERT INTO derived_concept_job_details(derived_concept_id, error_stack, derived_concept_sql, status, started_on, completed_on) VALUES (2,'', 'Select * from table2', 'COMPLETED', '2010-10-10 00:00:00', '2010-10-10 00:00:00');
INSERT INTO derived_concept_job_details(derived_concept_id, error_stack, derived_concept_sql, status, started_on, completed_on) VALUES (3,'error stack1', 'Select * from table3', 'ERROR', '2010-10-10 00:00:00', '2010-10-10 00:00:00');
INSERT INTO derived_concept_job_details(derived_concept_id, error_stack, derived_concept_sql, status, started_on, completed_on) VALUES (1,'', 'Select * from table1', 'COMPLETED', '2010-10-10 00:00:00', '2010-10-10 00:00:00');
INSERT INTO derived_concept_job_details(derived_concept_id, error_stack, derived_concept_sql, status, started_on, completed_on) VALUES (2,'', 'Select * from table2', 'COMPLETED', '2010-10-10 00:00:00', '2010-10-10 00:00:00');
INSERT INTO derived_concept_job_details(derived_concept_id, error_stack, derived_concept_sql, status, started_on, completed_on) VALUES (3,'error stack1', 'Select * from table3', 'ERROR', '2010-10-10 00:00:00', '2010-10-10 00:00:00');