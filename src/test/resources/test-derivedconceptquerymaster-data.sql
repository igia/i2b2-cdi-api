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


INSERT INTO qt_query_master (name,create_date,generated_sql)
VALUES ('derived:lastldl','2010-10-10 00:00:00','select * from dx');

INSERT INTO qt_query_master (name,create_date,generated_sql)
VALUES ('derived:maxldl','2011-10-10 00:00:00','select * from dx');

INSERT INTO qt_query_master (name,create_date,generated_sql)
VALUES ('derived:maxbpc','2011-12-10 00:00:00','select * from dx');

INSERT INTO qt_query_master (name,create_date,generated_sql)
VALUES ('derived:avgbpc','2011-10-10 00:00:00','select * from dx');

INSERT INTO qt_query_master (name,create_date,generated_sql)
VALUES ('derived:lastbpc','2019-10-10 00:00:00','select * from dx');