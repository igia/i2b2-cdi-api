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




package io.igia.i2b2.cdi.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class DatabaseConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties i2b2DataStoreProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.configuration")
    public HikariDataSource i2b2DataSource() {
        return i2b2DataStoreProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "ontology.datasource")
    public DataSourceProperties i2b2OntologyDataStoreProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "ontology")
    @ConfigurationProperties(prefix = "ontology.datasource.configuration")
    public HikariDataSource i2b2OntologyDataSource() {
        return i2b2OntologyDataStoreProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(i2b2DataSource());
    }

    @Bean
    @Qualifier("i2b2OntologyTransactionManager")
    public PlatformTransactionManager i2b2OntologyTransactionManager() {
        return new DataSourceTransactionManager(i2b2OntologyDataSource());
    }
}
