/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
 * %%
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

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;

/**
 * Merges the parameters of handler methods that share one operation, such as handler methods that differ only in
 * the media types they produce, into one parameter per name and location.
 */
final class OperationParameterMerger {

    private static final String STRING = "string";

    private OperationParameterMerger() {}

    private record HandlerMethodParameter(HandlerMethod handlerMethod, Parameter parameter) {}

    static List<Parameter> merge(
            String operation, List<HandlerMethod> handlerMethods, Function<HandlerMethod, List<Parameter>> parameters) {
        return handlerMethods.stream()
                .flatMap(handlerMethod -> parameters.apply(handlerMethod).stream()
                        .map(parameter -> new HandlerMethodParameter(handlerMethod, parameter)))
                .collect(Collectors.groupingBy(
                        copy -> Pair.of(
                                copy.parameter().getIn(), copy.parameter().getName()),
                        LinkedHashMap::new,
                        Collectors.toList()))
                .values()
                .stream()
                .map(copies -> handlerMethods.size() == 1 && copies.size() == 1
                        ? copies.get(0).parameter()
                        : merge(operation, copies, handlerMethods.size()))
                .toList();
    }

    private static Parameter merge(String operation, List<HandlerMethodParameter> copies, int handlerMethodCount) {
        Parameter first = copies.get(0).parameter();
        List<Schema> schemas = copies.stream()
                .map(copy -> copy.parameter().getSchema())
                .distinct()
                .toList();
        Parameter merged;
        if (schemas.size() == 1) {
            // keeps the schema, which may still become a reference
            merged = first;
        } else if (schemas.contains(null)) {
            throw cannotMerge(operation, copies, "only some of them have a schema");
        } else {
            merged = Parameter.builder()
                    .name(first.getName())
                    .in(first.getIn())
                    .schema(buildUnion(schemas))
                    .build();
        }
        setAgreedValue(operation, copies, "description", Parameter::getDescription, merged::setDescription);
        setAgreedValue(operation, copies, "example", Parameter::getExample, merged::setExample);
        setAgreedValue(operation, copies, "examples", Parameter::getExamples, merged::setExamples);
        setAgreedValue(operation, copies, "style", Parameter::getStyle, merged::setStyle);
        setAgreedValue(operation, copies, "explode", Parameter::getExplode, merged::setExplode);
        setAgreedValue(operation, copies, "allowEmptyValue", Parameter::getAllowEmptyValue, merged::setAllowEmptyValue);
        setAgreedValue(operation, copies, "allowReserved", Parameter::getAllowReserved, merged::setAllowReserved);
        setAgreedValue(operation, copies, "$ref", Parameter::getRef, merged::setRef);
        setAgreedValue(operation, copies, "content", Parameter::getContent, merged::setContent);
        setAgreedValue(operation, copies, "extensions", Parameter::getExtensions, merged::setExtensions);
        boolean inEveryHandlerMethod = copies.stream()
                        .map(HandlerMethodParameter::handlerMethod)
                        .distinct()
                        .count()
                == handlerMethodCount;
        merged.setRequired(
                "path".equals(first.getIn()) || (inEveryHandlerMethod && allTrue(copies, Parameter::getRequired)));
        merged.setDeprecated(allTrue(copies, Parameter::getDeprecated) ? Boolean.TRUE : null);
        return merged;
    }

    private static boolean allTrue(List<HandlerMethodParameter> copies, Function<Parameter, @Nullable Boolean> flag) {
        return copies.stream().allMatch(copy -> Boolean.TRUE.equals(flag.apply(copy.parameter())));
    }

    private static <T> void setAgreedValue(
            String operation,
            List<HandlerMethodParameter> copies,
            String field,
            Function<Parameter, @Nullable T> getter,
            Consumer<@Nullable T> setter) {
        List<T> values = copies.stream()
                .map(copy -> getter.apply(copy.parameter()))
                .filter(OperationParameterMerger::isPresent)
                .distinct()
                .toList();
        if (values.size() > 1) {
            throw cannotMerge(operation, copies, "their values of " + field + " differ: " + values);
        }
        setter.accept(values.isEmpty() ? null : values.get(0));
    }

    private static boolean isPresent(@Nullable Object value) {
        if (value instanceof String string) {
            return !string.isBlank();
        }
        if (value instanceof Map<?, ?> map) {
            return !map.isEmpty();
        }
        return value != null;
    }

    private static Schema buildUnion(List<Schema> schemas) {
        if (schemas.stream().anyMatch(OperationParameterMerger::isUnconstrainedString)
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

    private static IllegalStateException cannotMerge(
            String operation, List<HandlerMethodParameter> copies, String reason) {
        Parameter parameter = copies.get(0).parameter();
        String handlerMethods = copies.stream()
                .map(copy -> copy.handlerMethod().getIdentifier())
                .distinct()
                .collect(Collectors.joining(", "));
        return new IllegalStateException("Cannot merge the parameters named " + parameter.getName() + " in "
                + parameter.getIn() + " of " + operation + " from the handler methods " + handlerMethods
                + ", because " + reason);
    }
}
