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

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .groupName("I2B2 REST API")
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.ant("/api/**"))
            .paths(Predicates.not(PathSelectors.regex("/api/user-info")))
            .build()
            .apiInfo(apiInfo())
            .securityContexts(Arrays.asList(securityContext()))
            .securitySchemes(Arrays.asList(basicAuthScheme()));
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
            "I2B2 REST API",
            "RESTFul interface over i2b2 data mart.",
            "1.0",
            null,
            null,
            null, null, Collections.emptyList());
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(Arrays.asList(basicAuthReference()))
            .forPaths(PathSelectors.ant("/api/**"))
            .build();
    }

    private SecurityScheme basicAuthScheme() {
        return new BasicAuth("basicAuth");
    }

    private SecurityReference basicAuthReference() {
        return SecurityReference.builder()
            .reference("basicAuth")
            .scopes(new AuthorizationScope[0])
            .build();
    }
}
