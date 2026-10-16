/*-
 * #%L
 * OpenAPI Spec for Spring :: Data
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

package io.meshcloud.openapispecforspring.data;

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.SpringWebHandlerMethodParameterExpander;
import io.meshcloud.openapispecforspring.common.paths.method.AbstractSpringWebHandlerMethod.SpringWebParameter;
import io.meshcloud.openapispecforspring.common.paths.method.AbstractSpringWebHandlerMethod.SpringWebType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Follows the request parameters that the application's {@link PageableHandlerMethodArgumentResolver} reads. Its
 * settings are protected, so they are read through reflection; without such a resolver, they come from
 * {@code spring.data.web.*}.
 */
@RequiredArgsConstructor
public class PageableParameterExpander implements SpringWebHandlerMethodParameterExpander {

    private static final Type SORT_TYPE =
            ResolvableType.forClassWithGenerics(List.class, String.class).getType();

    private final SpringDataWebProperties springDataWebProperties;
    private final ObjectProvider<PageableHandlerMethodArgumentResolver> pageableResolver;
    private final ObjectProvider<SortHandlerMethodArgumentResolver> sortResolver;

    @Nullable
    @Override
    public List<HandlerMethod.Parameter> expand(MethodParameter methodParameter) {
        if (!Pageable.class.isAssignableFrom(methodParameter.getParameterType())) {
            return null;
        }
        PageableSettings settings = Optional.ofNullable(pageableResolver.getIfUnique())
                .map(resolver -> fromResolver(resolver, methodParameter))
                .orElseGet(() -> fromProperties(methodParameter));
        Map<String, Object> pageSchema = new LinkedHashMap<>();
        Map<String, Object> sizeSchema = new LinkedHashMap<>();
        if (settings.defaults().isPaged()) {
            pageSchema.put(
                    "defaultValue",
                    String.valueOf(settings.firstPage() + settings.defaults().getPageNumber()));
            sizeSchema.put("defaultValue", String.valueOf(settings.defaults().getPageSize()));
        }
        pageSchema.put("minimum", String.valueOf(settings.firstPage()));
        sizeSchema.put("minimum", "1");
        sizeSchema.put("maximum", String.valueOf(settings.maxPageSize()));
        return List.of(
                queryParameter(
                        settings.pageParameter(),
                        Integer.class,
                        (settings.firstPage() == 0 ? "Zero" : "One") + "-based index of the page to return.",
                        pageSchema),
                queryParameter(
                        settings.sizeParameter(),
                        Integer.class,
                        "Number of elements per page. A larger size is reduced to the maximum.",
                        sizeSchema),
                queryParameter(
                        settings.sortParameter(),
                        SORT_TYPE,
                        "Sort order as property,(asc|desc), ascending by default. Repeat it to sort by several"
                                + " properties.",
                        Map.of()));
    }

    private record PageableSettings(
            String pageParameter,
            String sizeParameter,
            String sortParameter,
            int firstPage,
            int maxPageSize,
            Pageable defaults) {}

    private PageableSettings fromResolver(PageableHandlerMethodArgumentResolver resolver, MethodParameter parameter) {
        SortHandlerMethodArgumentResolver sort = sortResolver.getIfUnique();
        return new PageableSettings(
                parameterName(resolver, invoke(resolver, "getPageParameterName", String.class), parameter),
                parameterName(resolver, invoke(resolver, "getSizeParameterName", String.class), parameter),
                sort == null
                        ? buildPrefix(parameter, springDataWebProperties.getPageable())
                                + springDataWebProperties.getSort().getSortParameter()
                        : invoke(sort, "getSortParameter", String.class, MethodParameter.class, parameter),
                Boolean.TRUE.equals(invoke(resolver, "isOneIndexedParameters", Boolean.class)) ? 1 : 0,
                invoke(resolver, "getMaxPageSize", Integer.class),
                // without request values, the resolver answers with the default of @PageableDefault or its fallback
                invoke(
                        resolver,
                        "getPageable",
                        Pageable.class,
                        new Class<?>[] {MethodParameter.class, String.class, String.class},
                        parameter,
                        null,
                        null));
    }

    private static String parameterName(
            PageableHandlerMethodArgumentResolver resolver, String source, MethodParameter parameter) {
        return invoke(
                resolver,
                "getParameterNameToUse",
                String.class,
                new Class<?>[] {String.class, MethodParameter.class},
                source,
                parameter);
    }

    private PageableSettings fromProperties(MethodParameter methodParameter) {
        SpringDataWebProperties.Pageable pageable = springDataWebProperties.getPageable();
        Optional<PageableDefault> pageableDefault =
                Optional.ofNullable(methodParameter.getParameterAnnotation(PageableDefault.class));
        int defaultSize = pageableDefault
                .map(annotation -> annotation.size() == 10 ? annotation.value() : annotation.size())
                .orElse(pageable.getDefaultPageSize());
        String prefix = buildPrefix(methodParameter, pageable);
        return new PageableSettings(
                prefix + pageable.getPageParameter(),
                prefix + pageable.getSizeParameter(),
                prefix + springDataWebProperties.getSort().getSortParameter(),
                pageable.isOneIndexedParameters() ? 1 : 0,
                pageable.getMaxPageSize(),
                PageRequest.of(pageableDefault.map(PageableDefault::page).orElse(0), defaultSize));
    }

    private static <T> T invoke(Object target, String methodName, Class<T> resultType) {
        return invoke(target, methodName, resultType, new Class<?>[0]);
    }

    private static <T> T invoke(
            Object target, String methodName, Class<T> resultType, Class<?> parameterType, Object argument) {
        return invoke(target, methodName, resultType, new Class<?>[] {parameterType}, argument);
    }

    private static <T> T invoke(
            Object target,
            String methodName,
            Class<T> resultType,
            Class<?>[] parameterTypes,
            @Nullable Object... arguments) {
        Method method = ReflectionUtils.findMethod(target.getClass(), methodName, parameterTypes);
        if (method == null) {
            throw new IllegalStateException(
                    "Cannot find " + methodName + " in " + target.getClass().getName());
        }
        ReflectionUtils.makeAccessible(method);
        Object result = ReflectionUtils.invokeMethod(method, target, arguments);
        if (result == null) {
            throw new IllegalStateException(
                    methodName + " of " + target.getClass().getName() + " returned null");
        }
        return resultType.cast(result);
    }

    private static String buildPrefix(MethodParameter methodParameter, SpringDataWebProperties.Pageable pageable) {
        Qualifier qualifier = methodParameter.getParameterAnnotation(Qualifier.class);
        String qualifierPrefix = qualifier == null ? "" : qualifier.value() + pageable.getQualifierDelimiter();
        return pageable.getPrefix() + qualifierPrefix;
    }

    private static HandlerMethod.Parameter queryParameter(
            String name, Type type, String description, Map<String, Object> schemaAttributes) {
        List<Annotation> annotations = Stream.of(
                        AnnotationUtils.synthesizeAnnotation(
                                Map.of("name", name, "required", false), RequestParam.class, null),
                        AnnotationUtils.synthesizeAnnotation(Map.of("description", description), Parameter.class, null),
                        schemaAttributes.isEmpty()
                                ? null
                                : AnnotationUtils.synthesizeAnnotation(schemaAttributes, Schema.class, null))
                .filter(Objects::nonNull)
                .map(Annotation.class::cast)
                .toList();
        return new SpringWebParameter(
                name, SpringWebType.of(type, AnnotationsSupplier.EMPTY), new AnnotationsSupplier() {
                    @Override
                    public <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
                        return annotations.stream()
                                .filter(annotationType::isInstance)
                                .map(annotationType::cast);
                    }
                });
    }
}
