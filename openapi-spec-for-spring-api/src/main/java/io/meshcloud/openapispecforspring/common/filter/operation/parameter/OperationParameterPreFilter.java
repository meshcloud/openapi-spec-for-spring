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

package io.meshcloud.openapispecforspring.common.filter.operation.parameter;

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;

/**
 * Filter for {@link Parameter Parameters}. Is evaluated BEFORE the parameter is constructed.
 *
 * @see OperationParameterPostFilter
 */
@FunctionalInterface
public interface OperationParameterPreFilter {
    /**
     * Accept the given parameter of the handler method to build an operation parameter from.
     *
     * @param handlerMethodParameter handler method parameter
     * @return true if parameter shall be built, false otherwise
     */
    boolean preAccept(HandlerMethod.Parameter handlerMethodParameter);
}
