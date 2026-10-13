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

package io.meshcloud.openapispecforspring.common.filter.operation;

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.model.operation.Operation;

/**
 * Filter for {@link Operation Operations}. Is evaluated AFTER the operation is constructed.
 *
 * <p> This filter does not remove possibly referenced items due to operation
 * building. Prefer using {@link OperationPreFilter} whenever possible.
 *
 * @see OperationPreFilter
 */
@FunctionalInterface
public interface OperationPostFilter {
    /**
     * Accept the given operation.
     *
     * @param operation     built operation
     * @param handlerMethod handler method used to build this operation
     * @return true if it shall be included, false otherwise
     */
    boolean postAccept(Operation operation, HandlerMethod handlerMethod);
}
