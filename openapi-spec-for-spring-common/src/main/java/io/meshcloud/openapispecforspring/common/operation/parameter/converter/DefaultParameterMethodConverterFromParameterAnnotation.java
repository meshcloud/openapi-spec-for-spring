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

package io.meshcloud.openapispecforspring.common.operation.parameter.converter;

import static io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils.muchLaterThan;

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import java.util.Optional;
import javax.annotation.Nullable;

public class DefaultParameterMethodConverterFromParameterAnnotation implements ParameterMethodConverter {

    public static final int ORDER = muchLaterThan(DEFAULT_ORDER);

    @Nullable
    @Override
    public Parameter convert(HandlerMethod.Parameter handlerMethodParameter) {
        return handlerMethodParameter
                .getAnnotationsSupplier()
                .findAnnotations(io.swagger.v3.oas.annotations.Parameter.class)
                .findFirst()
                .flatMap(parameterAnnotation ->
                        parameterAnnotation.hidden() ? Optional.empty() : Optional.of(new Parameter()))
                .orElse(null);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
