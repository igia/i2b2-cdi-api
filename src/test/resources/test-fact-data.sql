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

insert into observation_fact(encounter_num, patient_num, concept_cd, provider_id, start_date, modifier_cd, instance_num) values(1, 1, 'concept-1', 'provider-1', '2002-10-04 00:00:00', '@', 1);
insert into observation_fact(encounter_num, patient_num, concept_cd, provider_id, start_date, modifier_cd, instance_num) values(2, 1, 'concept-1', 'provider-1', '2002-10-04 00:00:00', '@', 1);
insert into observation_fact(encounter_num, patient_num, concept_cd, provider_id, start_date, modifier_cd, instance_num) values(2, 1, 'concept-1', 'provider-1', '2002-10-04 00:00:00', '@', 2);
insert into observation_fact(encounter_num, patient_num, concept_cd, provider_id, start_date, modifier_cd, instance_num) values(1, 2, 'concept-1', 'provider-2', '2002-10-04 00:00:00', '@', 1);
