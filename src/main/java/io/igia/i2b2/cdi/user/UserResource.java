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



package io.igia.i2b2.cdi.user;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserResource {

    public UserResource() {
    }

    @GetMapping(value = "/user-info", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public User getUserInfo(Authentication authentication) {
        return Optional.of(authentication)
            .filter(UsernamePasswordAuthenticationToken.class::isInstance)
            .map(UsernamePasswordAuthenticationToken.class::cast)
            .map(auth -> auth.getPrincipal())
            .filter(User.class::isInstance)
            .map(User.class::cast)
            .get();
    }
}
