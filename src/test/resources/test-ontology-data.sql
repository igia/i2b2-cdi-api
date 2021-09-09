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

INSERT INTO i2b2(c_hlevel, c_fullname, c_name, c_basecode, m_applied_path, sourcesystem_cd, m_exclusion_cd) VALUES (5, '\i2b2\Diagnoses\Neoplasms (140-239)\Malignant neoplasms (140-208)\Respiratory and intrathorasic organs (160-165)\(160) Malignant neoplasm of nasal~\', 'Neoplasm, malignant, of nasal cavities, middle ear, and accessory sinuses', 'ICD9:160', '@', 'demo', null);
INSERT INTO i2b2(c_hlevel, c_fullname, c_name, c_basecode, m_applied_path, sourcesystem_cd, m_exclusion_cd) VALUES (1, '\Secondary Diagnosis\', 'Secondary Diagnosis', '2', '\i2b2\Diagnoses\Neoplasms (140-239)\%', 'demo', null);
INSERT INTO i2b2(c_hlevel, c_fullname, c_name, c_basecode, m_applied_path, sourcesystem_cd, m_exclusion_cd) VALUES (3, '\Lung\TNM\Stage\Occult\', 'Cancer found in lung fluids but its location cant be determined', 'TNM:STAGEOccult', '\i2b2\Diagnoses\Neoplasms (140-239)\Malignant neoplasms (140-208)\Respiratory and intrathorasic organs (160-165)\%', 'DEMO', null);
INSERT INTO i2b2(c_hlevel, c_fullname, c_name, c_basecode, m_applied_path, sourcesystem_cd, m_exclusion_cd) VALUES (3, '\Lung\TNM\Stage\I\', 'Cancer is localized', 'TNM:STAGEI', '\i2b2\Diagnoses\Neoplasms (140-239)\Malignant neoplasms (140-208)\Respiratory and intrathorasic organs (160-165)\%', 'DEMO', null);
INSERT INTO i2b2(c_hlevel, c_fullname, c_name, c_basecode, m_applied_path, sourcesystem_cd, m_exclusion_cd) VALUES (2, '\TNM\Tumor\', 'TNM Staging, Tumor Rating', 'TNM:TUMOR', '\i2b2\Diagnoses\Neoplasms (140-239)\Malignant neoplasms (140-208)\Respiratory and intrathorasic organs (160-165)\%', 'DEMO', 'X');



INSERT INTO TABLE_ACCESS(
	c_hlevel, 
	c_fullname,
	c_name,
	c_visualattributes,
	c_facttablecolumn,
	c_dimtablename,
	c_columnname,
	c_columndatatype,
	c_operator,
	c_dimcode,
	c_tooltip
)
VALUES
(
	0,
	'\TNM\Tumor\',
	'TNM Staging, Tumor Rating',
	'FA',
	'concept_cd',
	'concept_dimension',
	'concept_path',
	'T',
	'LIKE',
	'TNM:TUMOR',
	'TNM Tumor'
);