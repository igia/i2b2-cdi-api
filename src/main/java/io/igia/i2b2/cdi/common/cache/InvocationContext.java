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

import java.util.Arrays;
import java.util.Objects;

public class InvocationContext {

    private final Class targetClass;
    private final String targetMethod;
    private final Object[] args;

    protected InvocationContext(Class targetClass, String targetMethod, Object[] args) {
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
        this.args = args;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        InvocationContext that = (InvocationContext) other;
        return targetClass.equals(that.targetClass) &&
            targetMethod.equals(that.targetMethod) &&
            Arrays.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(targetClass, targetMethod);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }

    @Override
    public String toString() {
        return "InvocationContext{" +
            "targetClass=" + targetClass +
            ", targetMethod='" + targetMethod + '\'' +
            ", args=" + Arrays.toString(args) +
            '}';
    }
}
