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


insert into patient_dimension(patient_num, sourcesystem_cd) values(1, 'demo');
insert into patient_dimension(patient_num, sourcesystem_cd) values(2, 'DEMO');
insert into patient_dimension(patient_num, sourcesystem_cd) values(3, 'test');

insert into patient_mapping(patient_ide, patient_ide_source, patient_num, patient_ide_status, project_id, sourcesystem_cd) values('1', 'i2b2', 1, 'A', 'pr1', 'demo');
insert into patient_mapping(patient_ide, patient_ide_source, patient_num, patient_ide_status, project_id, sourcesystem_cd) values('2', 'i2b2', 2, 'A', 'pr1', 'DEMO');
insert into patient_mapping(patient_ide, patient_ide_source, patient_num, patient_ide_status, project_id, sourcesystem_cd) values('3', 'i2b2', 3, 'A', 'pr1', 'test');
