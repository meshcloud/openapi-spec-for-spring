/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
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

import io.meshcloud.openapispecforspring.common.annotation.DeprecationDetector;
import io.meshcloud.openapispecforspring.common.operation.OperationBuilderContext;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.model.operation.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultDeprecatedOperationCustomizer implements OperationCustomizer {

    public static final int ORDER = DEFAULT_ORDER;

    private final DeprecationDetector deprecationDetector;

    @Override
    public void customize(Operation operation, OperationBuilderContext operationBuilderContext) {
        HandlerMethod handlerMethod = operationBuilderContext.getOperationInfo().getHandlerMethod();
        List<HandlerMethod> mergedHandlerMethods = handlerMethod.getMergedHandlerMethods();
        // A merged handler method has the annotations of all its handler methods, so each one is checked on its own.
        if (mergedHandlerMethods.size() > 1) {
            boolean deprecated = mergedHandlerMethods.stream().allMatch(this::isDeprecated);
            operation.setDeprecated(deprecated ? Boolean.TRUE : null);
        } else if (operation.getDeprecated() == null
                && deprecationDetector.isDeprecated(handlerMethod::findAnnotations)) {
            operation.setDeprecated(true);
        }
    }

    private boolean isDeprecated(HandlerMethod handlerMethod) {
        return deprecationDetector.isDeprecated(handlerMethod::findAnnotations)
                || handlerMethod
                        .findAnnotations(io.swagger.v3.oas.annotations.Operation.class)
                        .anyMatch(io.swagger.v3.oas.annotations.Operation::deprecated);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
