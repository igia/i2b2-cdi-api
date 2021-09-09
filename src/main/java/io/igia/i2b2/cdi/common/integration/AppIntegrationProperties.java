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

package io.igia.i2b2.cdi.common.integration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.integration.sftp")
@Configuration("integrationProperties")
public class AppIntegrationProperties {

	private String host;
	private Integer port;
	private String user;
	private String password;
	private String remoteDirPath;
	private String localDirPath;
	
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRemoteDirPath() {
        return remoteDirPath;
    }
    public void setRemoteDirPath(String remoteDirPath) {
        this.remoteDirPath = remoteDirPath;
    }
    public String getLocalDirPath() {
        return localDirPath;
    }
    public void setLocalDirPath(String localDirPath) {
        this.localDirPath = localDirPath;
    }
}
