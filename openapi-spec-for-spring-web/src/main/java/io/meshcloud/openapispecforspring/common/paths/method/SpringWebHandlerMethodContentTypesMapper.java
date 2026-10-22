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

package io.meshcloud.openapispecforspring.common.paths.method;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.springframework.http.MediaType.ALL_VALUE;

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodRequestBodyParameterMapper.RequestBodyParameter;
import io.meshcloud.openapispecforspring.common.util.OpenApiVoidUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.SmartHttpMessageConverter;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
public class SpringWebHandlerMethodContentTypesMapper {
    private static final Set<String> SINGLE_ALL_VALUE = singleton(ALL_VALUE);

    private final SpringWebHttpMessageConvertersSupplier httpMessageConvertersSupplier;
    private final SpringWebHandlerMethodRequestBodyParameterMapper requestBodyParameterMapper;

    public Set<String> findConsumesContentTypes(SpringWebHandlerMethod handlerMethod) {
        return fromRequestMappingAnnotation(handlerMethod, RequestMapping::consumes)
                .or(() -> requestBodyParameterMapper
                        .findRequestBodyParameter(handlerMethod)
                        .map(requestBodyParameter -> fromReadingConverters(requestBodyParameter, handlerMethod)))
                .orElse(emptySet());
    }

    public Set<String> findProducesContentTypes(SpringWebHandlerMethod handlerMethod) {
        return fromRequestMappingAnnotation(handlerMethod, RequestMapping::produces)
                .orElseGet(() -> fromWritingConverters(handlerMethod));
    }

    private static Optional<Set<String>> fromRequestMappingAnnotation(
            HandlerMethod handlerMethod, Function<RequestMapping, String[]> annotationMapper) {
        return handlerMethod
                .findAnnotations(RequestMapping.class)
                .map(annotationMapper)
                .filter(contentTypes -> !StringUtils.isAllBlank(contentTypes))
                // Spring doc says the first one should win,
                // ie. annotation on class level is overridden by method level
                .findFirst()
                .map(contentTypes -> Stream.of(contentTypes).collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    private Set<String> fromReadingConverters(
            RequestBodyParameter requestBodyParameter, SpringWebHandlerMethod handlerMethod) {
        HandlerMethod.Parameter parameter = requestBodyParameter.getParameter();
        boolean readByConverters = parameter
                .getAnnotationsSupplier()
                .findAnnotations(RequestBody.class)
                .findAny()
                .isPresent();
        if (!readByConverters) {
            return SINGLE_ALL_VALUE;
        }
        Class<?> beanType = handlerMethod.getMethod().getBeanType();
        return findSchemaImplementation(parameter.getAnnotationsSupplier())
                .or(() -> parameter
                        .getType()
                        .map(type -> ResolvableType.forType(GenericTypeResolver.resolveType(type.getType(), beanType)))
                        .map(type -> Optional.class.equals(type.toClass()) ? type.getGeneric(0) : type))
                .map(bodyType ->
                        supportedConcreteMediaTypes(bodyType, converter -> canRead(converter, bodyType, beanType)))
                .orElse(SINGLE_ALL_VALUE);
    }

    private Set<String> fromWritingConverters(SpringWebHandlerMethod handlerMethod) {
        org.springframework.web.method.HandlerMethod method = handlerMethod.getMethod();
        ResolvableType returnType = ResolvableType.forMethodReturnType(method.getMethod(), method.getBeanType());
        boolean isHttpEntity = HttpEntity.class.isAssignableFrom(returnType.toClass());
        boolean writtenByConverters = isHttpEntity
                || method.hasMethodAnnotation(ResponseBody.class)
                || AnnotatedElementUtils.hasAnnotation(method.getBeanType(), ResponseBody.class);
        if (!writtenByConverters) {
            return SINGLE_ALL_VALUE;
        }
        ResolvableType bodyType = findSchemaImplementation(handlerMethod::findAnnotations)
                .orElseGet(() -> isHttpEntity ? returnType.as(HttpEntity.class).getGeneric(0) : returnType);
        return supportedConcreteMediaTypes(bodyType, converter -> canWrite(converter, bodyType));
    }

    private static Optional<ResolvableType> findSchemaImplementation(AnnotationsSupplier annotationsSupplier) {
        return annotationsSupplier
                .findAnnotations(Schema.class)
                .<Class<?>>map(Schema::implementation)
                .filter(implementation -> !Void.class.equals(implementation))
                .findFirst()
                .map(ResolvableType::forClass);
    }

    private Set<String> supportedConcreteMediaTypes(
            ResolvableType bodyType, Predicate<HttpMessageConverter<?>> canReadOrWrite) {
        Class<?> bodyClass = bodyType.toClass();
        if (OpenApiVoidUtils.isVoid(bodyClass)) {
            return SINGLE_ALL_VALUE;
        }
        Set<String> mediaTypes = httpMessageConvertersSupplier.getHttpMessageConverters().stream()
                .filter(canReadOrWrite)
                .flatMap(converter -> converter.getSupportedMediaTypes(bodyClass).stream())
                .filter(MediaType::isConcrete)
                .map(mediaType -> new MimeType(mediaType.getType(), mediaType.getSubtype()).toString())
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return mediaTypes.isEmpty() ? SINGLE_ALL_VALUE : mediaTypes;
    }

    private static boolean canRead(HttpMessageConverter<?> converter, ResolvableType type, Class<?> contextClass) {
        if (converter instanceof SmartHttpMessageConverter<?> smartConverter) {
            return smartConverter.canRead(type, null);
        }
        if (converter instanceof GenericHttpMessageConverter<?> genericConverter) {
            return genericConverter.canRead(type.getType(), contextClass, null);
        }
        return converter.canRead(type.toClass(), null);
    }

    private static boolean canWrite(HttpMessageConverter<?> converter, ResolvableType type) {
        if (converter instanceof SmartHttpMessageConverter<?> smartConverter) {
            return smartConverter.canWrite(type, type.toClass(), null);
        }
        if (converter instanceof GenericHttpMessageConverter<?> genericConverter) {
            return genericConverter.canWrite(type.getType(), type.toClass(), null);
        }
        return converter.canWrite(type.toClass(), null);
    }
}
