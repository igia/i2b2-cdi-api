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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Aspect
@Component
public final class RequestCacheAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestCacheAspect.class);

    private final RequestCacheManager requestCacheManager;

    public RequestCacheAspect(RequestCacheManager requestCacheManager) {
        this.requestCacheManager = requestCacheManager;
    }

    @Around("@annotation(io.igia.i2b2.cdi.common.cache.RequestCache)")
    public Object processRequestCache(ProceedingJoinPoint joinPoint) throws Throwable {
        InvocationContext invocationContext = new InvocationContext(
            joinPoint.getSignature().getDeclaringType(),
            joinPoint.getSignature().getName(),
            joinPoint.getArgs());
        Optional<Object> cachedResult = requestCacheManager.get(invocationContext);
        if (cachedResult.isPresent()) {
            Object result = cachedResult.get();
            LOGGER.debug("Cache hit for request {}", invocationContext);
            return result;
        } else {
            Object methodResult = joinPoint.proceed();
            LOGGER.debug("Caching result: for request {}", invocationContext);
            requestCacheManager.put(invocationContext, methodResult);
            return methodResult;
        }
    }
}
