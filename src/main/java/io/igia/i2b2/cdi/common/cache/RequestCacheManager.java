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

package io.igia.i2b2.cdi.common.cache;

import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequestScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestCacheManager {

    private final Map<InvocationContext, Object> cache = new ConcurrentHashMap<>();

    Optional<Object> get(InvocationContext invocationContext) {
        return Optional.ofNullable(cache.get(invocationContext));
    }

    void put(InvocationContext invocationContext, Object result) {
        cache.put(invocationContext, result);
    }
}
