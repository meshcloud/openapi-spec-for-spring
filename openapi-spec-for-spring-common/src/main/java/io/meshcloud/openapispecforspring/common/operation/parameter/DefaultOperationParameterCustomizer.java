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

package io.meshcloud.openapispecforspring.common.operation.parameter;

import static io.meshcloud.openapispecforspring.common.util.OpenApiCollectionUtils.firstNonNull;
import static io.meshcloud.openapispecforspring.common.util.OpenApiCollectionUtils.setCollectionIfNotEmpty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiObjectUtils.setIfNotEmpty;

import io.meshcloud.openapispecforspring.common.filter.operation.parameter.OperationParameterPostFilter;
import io.meshcloud.openapispecforspring.common.filter.operation.parameter.OperationParameterPreFilter;
import io.meshcloud.openapispecforspring.common.mapper.ParameterAnnotationMapper;
import io.meshcloud.openapispecforspring.common.operation.OperationBuilderContext;
import io.meshcloud.openapispecforspring.common.operation.OperationInfo;
import io.meshcloud.openapispecforspring.common.operation.customizer.OperationCustomizer;
import io.meshcloud.openapispecforspring.common.operation.parameter.converter.ParameterMethodConverter;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.OperationParameterCustomizer;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.OperationParameterCustomizerContext;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.reference.component.parameter.ReferencedParametersConsumer;
import io.meshcloud.openapispecforspring.common.util.OpenApiStreamUtils;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.meshcloud.openapispecforspring.model.operation.Operation;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
@Slf4j
public class DefaultOperationParameterCustomizer implements OperationCustomizer {

    public static final int ORDER = DEFAULT_ORDER;

    private static final String STRING = "string";

    private final List<OperationParameterPreFilter> parameterPreFilters;
    private final List<OperationParameterPostFilter> parameterPostFilters;
    private final List<ParameterMethodConverter> parameterMethodConverters;
    private final List<OperationParameterCustomizer> parameterCustomizers;
    private final ParameterAnnotationMapper parameterAnnotationMapper;

    @Override
    public void customize(Operation operation, OperationBuilderContext operationBuilderContext) {
        // this customizer explicitly overrides already present parameters built from the operation annotation,
        // as the order of parameters is important and should not be determined
        // by the order of information to build those parameters!
        // See GH issue #8
        List<Parameter> parameters = buildParameters(operationBuilderContext);
        setParametersToOperation(operation, parameters, operationBuilderContext);
    }

    private List<Parameter> buildParameters(OperationBuilderContext operationBuilderContext) {
        HandlerMethod handlerMethod = operationBuilderContext.getOperationInfo().getHandlerMethod();
        Map<String, List<ParameterAnnotation>> parameterAnnotationsByName = Stream.concat(
                        handlerMethod
                                .findAnnotationsWithContext(io.swagger.v3.oas.annotations.Operation.class)
                                .map((annotation, context) -> Pair.of(annotation.parameters(), context))
                                .flatMap(pair -> Arrays.stream(pair.getLeft())
                                        .map(annotation -> new ParameterAnnotation(annotation, pair.getRight()))),
                        handlerMethod
                                .findAnnotationsWithContext(io.swagger.v3.oas.annotations.Parameter.class)
                                .map(ParameterAnnotation::new))
                .collect(OpenApiStreamUtils.groupingByPairKeyAndCollectingValuesToList());

        List<Parameter> parameters = new ArrayList<>();

        // first start with the parameters from the handler method, keep the order!
        for (Parameter parameterFromMethod : getParametersFromHandlerMethod(operationBuilderContext)) {
            List<ParameterAnnotation> parameterAnnotations =
                    parameterAnnotationsByName.remove(parameterFromMethod.getName());
            if (parameterAnnotations != null) {
                parameterAnnotations.forEach(annotation -> parameterAnnotationMapper.applyFromAnnotation(
                        parameterFromMethod,
                        annotation.get(),
                        operationBuilderContext.getMapperContext(annotation.getContext())));
            }
            parameters.add(parameterFromMethod);
        }

        // add leftover parameters from annotation only afterwards
        parameterAnnotationsByName.forEach((parameterName, parameterAnnotations) -> {
            // there must be at least one parameter annotation, as the map is a result of a groupingBy collector
            Iterator<ParameterAnnotation> iterator = parameterAnnotations.iterator();
            ParameterAnnotation firstAnnotation = iterator.next();
            Parameter parameter = parameterAnnotationMapper.buildFromAnnotation(
                    firstAnnotation.get(), operationBuilderContext.getMapperContext(firstAnnotation.getContext()));
            iterator.forEachRemaining(annotation -> parameterAnnotationMapper.applyFromAnnotation(
                    parameter, annotation.get(), operationBuilderContext.getMapperContext(annotation.getContext())));
            // also customizers should be applied to parameters not coming from method parameters
            applyParameterCustomizers(
                    parameter, OperationParameterCustomizerContextImpl.of(null, operationBuilderContext));
            setIfNotEmpty(parameter, parameters::add);
        });

        return preferRequiredParameters(
                mergeParametersWithSameNameAndLocation(parameters, operationBuilderContext.getOperationInfo()));
    }

    // Handler methods merged by their produced media types can each bring the same request parameter, for example
    // a path variable bound to differently named method parameters.
    private static List<Parameter> mergeParametersWithSameNameAndLocation(
            List<Parameter> parameters, OperationInfo operationInfo) {
        return parameters.stream()
                .collect(Collectors.groupingBy(
                        parameter -> Pair.of(parameter.getIn(), parameter.getName()),
                        LinkedHashMap::new,
                        Collectors.toList()))
                .values()
                .stream()
                .map(sameParameters -> sameParameters.size() == 1
                        ? sameParameters.get(0)
                        : mergeParameters(sameParameters, operationInfo))
                .toList();
    }

    private static Parameter mergeParameters(List<Parameter> parameters, OperationInfo operationInfo) {
        Parameter first = parameters.get(0);
        if (parameters.stream()
                        .map(DefaultOperationParameterCustomizer::copyWithoutMergedFields)
                        .distinct()
                        .count()
                > 1) {
            throw cannotMerge(
                    first, operationInfo, "they differ in more than description, required, deprecated and schema");
        }
        Parameter merged;
        List<Schema> schemas =
                parameters.stream().map(Parameter::getSchema).distinct().toList();
        if (schemas.size() == 1) {
            // keeps the schema, which may still become a reference
            merged = first;
        } else if (schemas.contains(null)) {
            throw cannotMerge(first, operationInfo, "only some of them have a schema");
        } else {
            merged = copyWithoutMergedFields(first);
            merged.setSchema(buildUnion(schemas));
        }
        merged.setDescription(mergeDescriptions(parameters));
        merged.setRequired(
                "path".equals(first.getIn()) || parameters.stream().allMatch(p -> Boolean.TRUE.equals(p.getRequired()))
                        ? Boolean.TRUE
                        : null);
        merged.setDeprecated(
                parameters.stream().allMatch(p -> Boolean.TRUE.equals(p.getDeprecated())) ? Boolean.TRUE : null);
        return merged;
    }

    private static Parameter copyWithoutMergedFields(Parameter parameter) {
        return Parameter.builder()
                .name(parameter.getName())
                .in(parameter.getIn())
                .allowEmptyValue(parameter.getAllowEmptyValue())
                .ref(parameter.getRef())
                .style(parameter.getStyle())
                .explode(parameter.getExplode())
                .allowReserved(parameter.getAllowReserved())
                .examples(parameter.getExamples())
                .example(parameter.getExample())
                .content(parameter.getContent())
                .extensions(parameter.getExtensions())
                .build();
    }

    private static Schema buildUnion(List<Schema> schemas) {
        if (schemas.stream().anyMatch(DefaultOperationParameterCustomizer::isUnconstrainedString)
                && schemas.stream().allMatch(schema -> STRING.equals(schema.getType()))) {
            return Schema.builder().type(STRING).build();
        }
        return Schema.builder().anyOf(schemas).build();
    }

    private static boolean isUnconstrainedString(Schema schema) {
        return STRING.equals(schema.getType())
                && schema.getRef() == null
                && schema.getFormat() == null
                && schema.getPattern() == null
                && schema.getEnumValues() == null
                && schema.getMinLength() == null
                && schema.getMaxLength() == null;
    }

    @Nullable
    private static String mergeDescriptions(List<Parameter> parameters) {
        String descriptions = parameters.stream()
                .map(Parameter::getDescription)
                .filter(description -> description != null && !description.isBlank())
                .distinct()
                .collect(Collectors.joining("\n\n"));
        return descriptions.isEmpty() ? null : descriptions;
    }

    private static IllegalStateException cannotMerge(Parameter parameter, OperationInfo operationInfo, String reason) {
        return new IllegalStateException("Cannot merge the parameters named " + parameter.getName() + " in "
                + parameter.getIn() + " of " + operationInfo.getRequestMethod() + " " + operationInfo.getPathPattern()
                + ", because " + reason);
    }

    private static List<Parameter> preferRequiredParameters(List<Parameter> parameters) {
        return parameters.stream()
                .sorted(Comparator.comparing(parameter -> !Boolean.TRUE.equals(parameter.getRequired())))
                .toList();
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    private static class ParameterAnnotation extends Pair<String, ParameterAnnotation> {
        private final transient io.swagger.v3.oas.annotations.Parameter annotation;

        @Getter
        private final transient HandlerMethod.Context context;

        public io.swagger.v3.oas.annotations.Parameter get() {
            return annotation;
        }

        @Override
        public String getLeft() {
            return annotation.name();
        }

        @Override
        public ParameterAnnotation getRight() {
            return this;
        }

        @Override
        public ParameterAnnotation setValue(ParameterAnnotation value) {
            throw new UnsupportedOperationException();
        }
    }

    private void setParametersToOperation(
            Operation operation, List<Parameter> parameters, OperationBuilderContext operationBuilderContext) {
        List<Parameter> filteredParameters = parameters.stream()
                .filter(parameter -> parameterPostFilters.stream().allMatch(filter -> filter.postAccept(parameter)))
                .toList();
        ReferencedParametersConsumer referencedParametersConsumer =
                operationBuilderContext.getReferencedItemConsumer(ReferencedParametersConsumer.class);
        setCollectionIfNotEmpty(
                filteredParameters,
                p -> referencedParametersConsumer.withOwner(operation).maybeAsReference(p, operation::setParameters));
    }

    private List<Parameter> getParametersFromHandlerMethod(OperationBuilderContext operationBuilderContext) {
        return operationBuilderContext.getOperationInfo().getHandlerMethod().getParameters().stream()
                .filter(methodParameter ->
                        parameterPreFilters.stream().allMatch(filter -> filter.preAccept(methodParameter)))
                .flatMap(methodParameter -> {
                    OperationParameterCustomizerContext parameterCustomizerContext =
                            OperationParameterCustomizerContextImpl.of(methodParameter, operationBuilderContext);
                    // converters are ordered and the first one not returning null will be used
                    return firstNonNull(parameterMethodConverters, converter -> converter.convert(methodParameter))
                            // apply customizers after conversion,
                            // first callback of specific parameter implementation,
                            .map(parameter -> applyCustomizeCallback(parameter, methodParameter))
                            // then parameter customizers applying to any implementation
                            .map(parameter -> applyParameterCustomizers(parameter, parameterCustomizerContext))
                            .stream();
                })
                .filter(parameter -> !parameter.isEmpty())
                .toList();
    }

    private Parameter applyCustomizeCallback(Parameter parameter, HandlerMethod.Parameter methodParameter) {
        methodParameter.customize(parameter);
        // return value for convenience in .map
        return parameter;
    }

    private Parameter applyParameterCustomizers(
            Parameter parameter, OperationParameterCustomizerContext operationParameterCustomizerContext) {
        parameterCustomizers.forEach(
                customizer -> customizer.customize(parameter, operationParameterCustomizerContext));
        // return value for convenience in .map
        return parameter;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
