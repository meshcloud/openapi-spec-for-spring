/*-
 * #%L
 * OpenAPI Spec for Spring :: Web
 * %%
 * Copyright (C) 2020-2023 QAware GmbH
 * Copyright (C) 2026 meshcloud GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package io.meshcloud.openapispecforspring.common.paths.method.merger;

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.method.AbstractSpringWebHandlerMethod;
import io.meshcloud.openapispecforspring.common.util.OpenApiVoidUtils;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

public class SpringWebHandlerMethodTypeMerger {

    public <T extends HandlerMethod.HasType> Optional<HandlerMethod.Type> mergeTypes(Stream<T> hasTypes) {
        return hasTypes
                // flatMap might be an alternative to throwing exception
                .map(hasType -> hasType.getType()
                        .orElseThrow(() -> new IllegalStateException(
                                "No type present on " + hasType.getClass().getSimpleName())))
                .reduce(this::mergeType);
    }

    public HandlerMethod.Type mergeType(HandlerMethod.Type a, HandlerMethod.Type b) {
        if (isNotVoid(a) && isNotVoid(b) && !a.getType().equals(b.getType())) {
            throw new IllegalStateException("Cannot merge conflicting types: " + a + " vs. " + b);
        }
        return AbstractSpringWebHandlerMethod.SpringWebType.of(
                chooseParameterType(a, b), a.getAnnotationsSupplier().andThen(b.getAnnotationsSupplier()));
    }

    private static Type chooseParameterType(HandlerMethod.Type a, HandlerMethod.Type b) {
        return isNotVoid(a) ? a.getType() : b.getType();
    }

    private static boolean isNotVoid(HandlerMethod.Type type) {
        return !OpenApiVoidUtils.isVoid(type.getType());
    }
}
