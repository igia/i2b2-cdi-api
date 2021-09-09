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

DROP TABLE IF EXISTS i2b2;
CREATE TABLE i2b2
(
    c_hlevel integer NOT NULL,
    c_fullname character varying(700) NOT NULL,
    c_name character varying(2000) NOT NULL,
    c_basecode character varying(50),
    m_applied_path character varying(700) NOT NULL,
    sourcesystem_cd character varying(50),
    m_exclusion_cd character varying(25),
    c_metadataxml text,
    c_synonym_cd character varying(50),
    c_visualattributes character varying(50),
    c_facttablecolumn character varying(50),
    c_tablename character varying(50),
    c_columnname character varying(50),
    c_columndatatype character varying(50),
    c_operator character varying(50),
    c_dimcode character varying(50),
    c_tooltip character varying(50),
    update_date timestamp without time zone
);

DROP TABLE IF EXISTS TABLE_ACCESS;
CREATE TABLE TABLE_ACCESS
(
	c_table_cd character varying(50),
	c_table_name character varying(50),
	c_protected_access character varying(50),
	c_hlevel integer, 
	c_fullname character varying(700),
	c_name character varying(50),
	c_synonym_cd character varying(50),
	c_visualattributes character varying(50),
	c_facttablecolumn character varying(50),
	c_dimtablename character varying(50),
	c_columnname character varying(50),
	c_columndatatype character varying(50),
	c_operator character varying(50),
	c_dimcode character varying(50),
	c_tooltip  character varying(50)
);