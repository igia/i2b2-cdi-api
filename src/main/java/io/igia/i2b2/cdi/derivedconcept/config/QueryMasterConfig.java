/**
* This Source Code Form is subject to the terms of the Mozilla Public License, v.
* 2.0 with a Healthcare Disclaimer.
* A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
* be found under the top level directory, named LICENSE.
* If a copy of the MPL was not distributed with this file, You can obtain one at
* http://mozilla.org/MPL/2.0/.
* If a copy of the Healthcare Disclaimer was not distributed with this file, You
* can obtain one at the project website https://github.com/igia.
*
* Copyright (C) 2021-2022 Persistent Systems, Inc.
*/


package io.igia.i2b2.cdi.derivedconcept.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "query-master")
public class QueryMasterConfig {
    private String prefix;
    private String postfix;
    private String conceptCodeTemplate;

    public String getPrefix() {
	return prefix;
    }

    public void setPrefix(String prefix) {
	this.prefix = prefix;
    }

    public String getPostfix() {
	return postfix;
    }

    public void setPostfix(String postfix) {
	this.postfix = postfix;
    }

    public String getConceptCodeTemplate() {
	return conceptCodeTemplate;
    }

    public void setConceptCodeTemplate(String conceptCodeTemplate) {
	this.conceptCodeTemplate = conceptCodeTemplate;
    }

}
