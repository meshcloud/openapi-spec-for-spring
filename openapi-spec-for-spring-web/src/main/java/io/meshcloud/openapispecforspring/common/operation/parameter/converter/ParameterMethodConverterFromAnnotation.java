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

package io.meshcloud.openapispecforspring.common.operation.parameter.converter;

import static io.meshcloud.openapispecforspring.common.util.OpenApiStringUtils.setStringIfNotBlank;

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import java.lang.annotation.Annotation;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.ValueConstants;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ParameterMethodConverterFromAnnotation<A extends Annotation> implements ParameterMethodConverter {

    private final Class<A> annotationClass;

    @Nullable
    @Override
    public Parameter convert(HandlerMethod.Parameter handlerMethodParameter) {
        return handlerMethodParameter
                .getAnnotationsSupplier()
                .findAnnotations(annotationClass)
                .findFirst()
                .map(this::buildParameter)
                .orElse(null);
    }

    @Nullable
    protected abstract Parameter buildParameter(A annotation);

    protected static Parameter.ParameterBuilder createDefaultParameterBuilder(String name) {
        val parameterBuilder = Parameter.builder();
        setStringIfNotBlank(name, parameterBuilder::name);
        return parameterBuilder;
    }

    protected static boolean getRequiredFlag(boolean required, String defaultValue) {
        if (!defaultValue.equals(ValueConstants.DEFAULT_NONE)) {
            return false;
        }
        return required;
    }
}
