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

import java.lang.annotation.Annotation;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

@Slf4j
public class DefaultOperationParameterNullableCustomizer implements OperationParameterCustomizer {
    private static final String JSR305_NULLABLE_CLASS_NAME = "javax.annotation.Nullable";

    @Override
    public void customize(
            io.meshcloud.openapispecforspring.model.parameter.Parameter parameter,
            OperationParameterCustomizerContext context) {
        findJsr305Nullable()
                .flatMap(nullable -> context.getHandlerMethodParameter()
                        .flatMap(handlerMethodParameter -> handlerMethodParameter
                                .getAnnotationsSupplier()
                                .findAnnotations(nullable)
                                .findFirst()))
                .ifPresent(ignored ->
                        // always set the parameter as "not required",
                        // as Spring is smart enough that if the parameter is marked as @Nullable,
                        // the parameter can be omitted
                        parameter.setRequired(false));
    }

    @SuppressWarnings("unchecked")
    private static Optional<Class<? extends Annotation>> findJsr305Nullable() {
        if (!ClassUtils.isPresent(JSR305_NULLABLE_CLASS_NAME, null)) {
            return Optional.empty();
        }
        return Optional.of((Class<? extends Annotation>) ClassUtils.resolveClassName(JSR305_NULLABLE_CLASS_NAME, null));
    }
}
