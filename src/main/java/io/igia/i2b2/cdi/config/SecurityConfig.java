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

import io.igia.i2b2.cdi.config.security.I2b2AuthenticationFailureHandler;
import io.igia.i2b2.cdi.config.security.I2b2AuthenticationSuccessHandler;
import io.igia.i2b2.cdi.config.security.I2b2LogoutSuccessHandler;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.zalando.problem.spring.web.advice.security.SecurityProblemSupport;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Import(SecurityProblemSupport.class)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final SecurityProblemSupport problemSupport;

    public SecurityConfig(SecurityProblemSupport problemSupport) {
        this.problemSupport = problemSupport;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf()
                .disable()
            .exceptionHandling()
                .authenticationEntryPoint(problemSupport)
                .accessDeniedHandler(problemSupport)
            .and()
                .headers()
                .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            .and()
                .frameOptions().deny()
            .and()
                .authorizeRequests()
                    .mvcMatchers("/api/**", "/actuator/**")
                    .authenticated()
            .and()
                .httpBasic()
            .and()
                .formLogin()
                    .successHandler(authenticationSuccessHandler())
                    .failureHandler(authenticationFailureHandler())
            .and()
                .logout()
                    .logoutSuccessHandler(logoutSuccessHandler());
        // @formatter:on
    }

    private I2b2AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new I2b2AuthenticationSuccessHandler();
    }

    private I2b2AuthenticationFailureHandler authenticationFailureHandler() {
        return new I2b2AuthenticationFailureHandler();
    }

    private I2b2LogoutSuccessHandler logoutSuccessHandler() {
        return new I2b2LogoutSuccessHandler();
    }
}
