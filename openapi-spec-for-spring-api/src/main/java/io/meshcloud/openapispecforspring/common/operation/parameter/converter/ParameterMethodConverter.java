/*-
 * #%L
 * OpenAPI Spec for Spring :: API
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

package io.meshcloud.openapispecforspring.common.operation.parameter.converter;

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import org.jspecify.annotations.Nullable;

/**
 * Converter for {@link  HandlerMethod.Parameter handler method
 * parameter}. Only if such a converter exists, the handler
 * method parameter will become part of the operation parameters.
 */
@FunctionalInterface
public interface ParameterMethodConverter extends OpenApiOrderedUtils.DefaultOrdered {
    /**
     * Convert the given {@link  HandlerMethod.Parameter handler
     * method parameter} into an {@link Parameter operation parameter}.
     *
     * @param handlerMethodParameter handler method parameter
     * @return parameter, or null if nothing could be converted
     */
    @Nullable
    Parameter convert(HandlerMethod.Parameter handlerMethodParameter);
}
