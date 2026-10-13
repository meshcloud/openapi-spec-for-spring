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

package io.meshcloud.openapispecforspring.common.operation.parameter.customizer;

import io.meshcloud.openapispecforspring.common.annotation.HasAnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.annotation.NullnessDetector;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultOperationParameterNullableCustomizer implements OperationParameterCustomizer {
    private final NullnessDetector nullnessDetector;

    @Override
    public void customize(
            io.meshcloud.openapispecforspring.model.parameter.Parameter parameter,
            OperationParameterCustomizerContext context) {
        context.getHandlerMethodParameter()
                .map(HasAnnotationsSupplier::getAnnotationsSupplier)
                .filter(nullnessDetector::isNullable)
                // Spring lets the client omit a nullable parameter.
                .ifPresent(ignored -> parameter.setRequired(false));
    }
}
