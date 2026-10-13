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

package io.meshcloud.openapispecforspring.common.operation.customizer;

import io.meshcloud.openapispecforspring.common.operation.OperationBuilderContext;
import io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils;
import io.meshcloud.openapispecforspring.model.operation.Operation;
import org.springframework.core.Ordered;

/**
 * Customizer for {@link Operation}. Run AFTER the {@link
 * io.swagger.v3.oas.annotations.Operation operation annotation} was applied.
 *
 * <p>{@link Ordered} can be used to resolve of conflicting customizations.
 */
@FunctionalInterface
public interface OperationCustomizer extends OpenApiOrderedUtils.DefaultOrdered {
    /**
     * Customize the given operation by reference.
     *
     * @param operation               operation
     * @param operationBuilderContext context for operation building
     */
    void customize(Operation operation, OperationBuilderContext operationBuilderContext);
}
