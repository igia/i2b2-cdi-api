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


INSERT INTO derived_concept_definition(concept_path, sql_query, unit_cd, description, update_date) VALUES ('\Derived\Test1\', 'Select * from table1', 'test-unit', 'Test description 1', '2010-10-10 00:00:00');
INSERT INTO derived_concept_definition(concept_path, sql_query, unit_cd, description, update_date) VALUES ('\Derived\Test2\', 'Select * from table2', 'test-unit', 'Test description 2', '2010-10-10 00:00:00');
INSERT INTO derived_concept_definition(concept_path, sql_query, unit_cd, description, update_date) VALUES ('\Derived\Test3\', 'Select * from table3', 'test-unit', 'Test description 3', '2010-10-10 00:00:00');

INSERT INTO concept_dimension(concept_cd, concept_path, name_char, sourcesystem_cd) values('derived:test1', '\Derived\Test1\', 'Test1', 'demo');
INSERT INTO concept_dimension(concept_cd, concept_path, name_char, sourcesystem_cd) values('derived:test2', '\Derived\Test2\', 'Test2', 'test');
INSERT INTO concept_dimension(concept_cd, concept_path, name_char, sourcesystem_cd) values('derived:test3', '\Derived\Test3\', 'Test3', 'DEMO');

INSERT INTO i2b2(c_hlevel, c_fullname, c_name, c_basecode, c_metadataxml, m_applied_path, m_exclusion_cd, sourcesystem_cd, c_columndatatype) VALUES (1, '\Derived\Test1\', 'Test1', 'derived:test1', 'MetaDataXml1', '@', '', 'demo', 'T');
INSERT INTO i2b2(c_hlevel, c_fullname, c_name, c_basecode, c_metadataxml, m_applied_path, m_exclusion_cd, sourcesystem_cd, c_columndatatype) VALUES (1, '\Derived\Test2\', 'Test2', 'derived:test2', 'MetaDataXml2', '@', '', 'test', 'T');
INSERT INTO i2b2(c_hlevel, c_fullname, c_name, c_basecode, c_metadataxml, m_applied_path, m_exclusion_cd, sourcesystem_cd, c_columndatatype) VALUES (1, '\Derived\Test3\', 'Test3', 'derived:test3', 'MetaDataXml3', '@', '', 'DEMO', 'T');


INSERT INTO TABLE_ACCESS 
(c_hlevel, c_fullname, c_name, c_visualattributes, c_facttablecolumn, c_dimtablename, c_columnname, c_columndatatype, c_operator, c_dimcode, c_tooltip) 
VALUES
(0, '\Derived\', 'derived', 'FA', 'concept_cd', 'concept_dimension', 'concept_path', 'T', 'LIKE', '\Derived\', 'derived concept');

INSERT INTO derived_concept_dependency (derived_concept_id, parent_concept_path) values (1,'\Derived\Test2\');
INSERT INTO derived_concept_dependency (derived_concept_id, parent_concept_path) values (1,'\Derived\Test3\');
INSERT INTO derived_concept_dependency (derived_concept_id, parent_concept_path) values (1,'\Derived\Test4\');
INSERT INTO derived_concept_dependency (derived_concept_id, parent_concept_path) values (2,'\Derived\Test3\');



