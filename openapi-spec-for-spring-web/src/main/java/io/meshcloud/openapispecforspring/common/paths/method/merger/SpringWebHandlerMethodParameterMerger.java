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

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.method.AbstractSpringWebHandlerMethod.SpringWebParameter;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodRequestBodyParameterMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodRequestBodyParameterMapper.RequestBodyParameter;
import io.meshcloud.openapispecforspring.common.util.OpenApiStreamUtils;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;

@RequiredArgsConstructor
@Slf4j
public class SpringWebHandlerMethodParameterMerger {

    private final SpringWebHandlerMethodTypeMerger springWebHandlerMethodTypeMerger;
    private final SpringWebHandlerMethodRequestBodyParameterMapper requestBodyParameterMapper;

    public List<HandlerMethod.Parameter> mergeParameters(List<SpringWebHandlerMethod> handlerMethods) {
        return handlerMethods.stream()
                .flatMap(handlerMethod -> parametersWithoutRequestBody(handlerMethod)
                        .map(parameter -> buildExtendedParameterPair(handlerMethod, parameter)))
                .collect(OpenApiStreamUtils.groupingByPairKeyAndCollectingValuesToList())
                .entrySet()
                .stream()
                .map(entry -> entry.getKey()
                        .map(parameterName -> {
                            val parameters = entry.getValue().stream()
                                    .map(ExtendedParameter::getParameter)
                                    .toList();
                            val handlerMethodsForParameters = entry.getValue().stream()
                                    .map(ExtendedParameter::getOwningHandlerMethod)
                                    .toList();
                            return buildMergedParameter(parameterName, parameters, parameter -> {
                                if (!handlerMethodsForParameters.containsAll(handlerMethods)) {
                                    parameter.setRequired(false); // otherwise, the Swagger UI doesn't work
                                }
                            });
                        })
                        .orElseThrow(() -> new IllegalStateException(
                                "Cannot merge handler methods with unnamed parameters: " + entry.getValue())))
                .toList();
    }

    // SpringWebHandlerMethodRequestBodyMerger merges request bodies per consumed media type, so their types may differ.
    private Stream<HandlerMethod.Parameter> parametersWithoutRequestBody(SpringWebHandlerMethod handlerMethod) {
        Optional<HandlerMethod.Parameter> requestBody = requestBodyParameterMapper
                .findRequestBodyParameter(handlerMethod)
                .map(RequestBodyParameter::getParameter);
        return handlerMethod.getParameters().stream()
                .filter(parameter -> requestBody.filter(parameter::equals).isEmpty());
    }

    private Pair<Optional<String>, ExtendedParameter> buildExtendedParameterPair(
            SpringWebHandlerMethod handlerMethod, HandlerMethod.Parameter parameter) {
        return Pair.of(parameter.getName(), ExtendedParameter.of(parameter, handlerMethod));
    }

    private HandlerMethod.Parameter buildMergedParameter(
            String parameterName, List<HandlerMethod.Parameter> parameters, Consumer<Parameter> parameterCustomizer) {
        val mergedAnnotationsSupplier = AnnotationsSupplier.merge(parameters.stream());
        val mergedType = springWebHandlerMethodTypeMerger
                .mergeTypes(parameters.stream())
                .orElseThrow(() -> new IllegalStateException("Grouped parameters should contain at least one entry"));
        return new SpringWebParameter(parameterName, mergedType, mergedAnnotationsSupplier) {
            @Override
            public void customize(Parameter parameter) {
                parameterCustomizer.accept(parameter);
            }
        };
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    private static class ExtendedParameter {
        private final HandlerMethod.Parameter parameter;
        private final HandlerMethod owningHandlerMethod;
    }
}
