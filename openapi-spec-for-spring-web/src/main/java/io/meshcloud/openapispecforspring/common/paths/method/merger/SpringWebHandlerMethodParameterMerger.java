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
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
@Slf4j
public class SpringWebHandlerMethodParameterMerger {

    private final SpringWebHandlerMethodTypeMerger springWebHandlerMethodTypeMerger;
    private final SpringWebHandlerMethodRequestBodyParameterMapper requestBodyParameterMapper;

    public List<HandlerMethod.Parameter> mergeParameters(List<SpringWebHandlerMethod> handlerMethods) {
        Map<Optional<String>, Set<HandlerMethod>> handlerMethodsByParameterName = handlerMethods.stream()
                .flatMap(handlerMethod -> parametersWithoutRequestBody(handlerMethod)
                        .map(parameter -> Pair.of(parameter.getName(), (HandlerMethod) handlerMethod)))
                .collect(OpenApiStreamUtils.groupingByPairKeyAndCollectingValuesTo(Collectors.toSet()));
        // parameters whose types differ stay apart, the operation builder merges their schemas
        return handlerMethods.stream()
                .flatMap(handlerMethod -> parametersWithoutRequestBody(handlerMethod)
                        .map(parameter -> buildExtendedParameterPair(handlerMethod, parameter)))
                .collect(OpenApiStreamUtils.groupingByPairKeyAndCollectingValuesToList())
                .entrySet()
                .stream()
                .map(entry -> entry.getKey()
                        .name()
                        .map(parameterName -> {
                            val parameters = entry.getValue().stream()
                                    .map(ExtendedParameter::getParameter)
                                    .toList();
                            boolean inEveryHandlerMethod = handlerMethodsByParameterName
                                    .getOrDefault(entry.getKey().name(), Set.of())
                                    .containsAll(handlerMethods);
                            return buildMergedParameter(parameterName, parameters, parameter -> {
                                if (!inEveryHandlerMethod) {
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

    private Pair<NameAndType, ExtendedParameter> buildExtendedParameterPair(
            SpringWebHandlerMethod handlerMethod, HandlerMethod.Parameter parameter) {
        return Pair.of(
                new NameAndType(
                        parameter.getName(),
                        parameter.getType().map(HandlerMethod.Type::getType).orElse(null)),
                ExtendedParameter.of(parameter, handlerMethod));
    }

    private record NameAndType(Optional<String> name, java.lang.reflect.@Nullable Type type) {}

    private HandlerMethod.Parameter buildMergedParameter(
            String parameterName, List<HandlerMethod.Parameter> parameters, Consumer<Parameter> parameterCustomizer) {
        val mergedAnnotationsSupplier = AnnotationsSupplier.merge(parameters.stream());
        val mergedType = springWebHandlerMethodTypeMerger
                .mergeTypes(parameters.stream())
                .orElseThrow(() -> new IllegalStateException("Grouped parameters should contain at least one entry"));
        boolean optional = parameters.stream()
                .anyMatch(parameter ->
                        parameter instanceof SpringWebParameter springWebParameter && springWebParameter.isOptional());
        return new SpringWebParameter(parameterName, mergedType, mergedAnnotationsSupplier, optional) {
            @Override
            public void customize(Parameter parameter) {
                super.customize(parameter);
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
