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

package io.meshcloud.openapispecforspring.common.schema.resolver.properties;

import static io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier.Purpose.SCHEMA_BUILDING;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaBuildMemo;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.jspecify.annotations.Nullable;
import org.springframework.core.annotation.AnnotationUtils;

@RequiredArgsConstructor
@Slf4j
public class DefaultSchemaPropertiesResolver implements SchemaPropertiesResolver {

    public static final int ORDER = DEFAULT_ORDER;

    private final OpenApiObjectMapperSupplier objectMapperSupplier;
    private final List<SchemaPropertyFilter> propertyFilters;
    private final AnnotationsSupplierFactory annotationsSupplierFactory;
    private final KotlinNullability kotlinNullability = new KotlinNullability();

    @Override
    public Map<String, SchemaProperty> findProperties(
            SchemaResolver.Caller caller, JavaType javaType, AnnotationsSupplier annotationsSupplier) {
        return SchemaBuildMemo.computeIfAbsent(
                new PropertiesKey(javaType, caller.getMode()), () -> findPropertiesOfType(caller, javaType));
    }

    private Map<String, SchemaProperty> findPropertiesOfType(SchemaResolver.Caller caller, JavaType javaType) {
        AnnotationsSupplier annotationsSupplier =
                annotationsSupplierFactory.createFromAnnotatedElement(javaType.getRawClass());
        SchemaResolver.Mode otherMode = caller.getMode() == SchemaResolver.Mode.FOR_SERIALIZATION
                ? SchemaResolver.Mode.FOR_DESERIALIZATION
                : SchemaResolver.Mode.FOR_SERIALIZATION;
        Map<String, SchemaProperty> properties = findPropertiesForMode(caller, javaType, annotationsSupplier);
        Map<String, SchemaProperty> otherModeProperties =
                findPropertiesForMode(SchemaResolver.Caller.of(otherMode), javaType, annotationsSupplier);
        Map<String, SchemaProperty> allProperties = new LinkedHashMap<>();
        properties.forEach((name, property) -> allProperties.put(
                name, otherModeProperties.containsKey(name) ? property : withOnlyMode(property, caller.getMode())));
        otherModeProperties.forEach((name, property) -> {
            if (!properties.containsKey(name)) {
                allProperties.put(name, withOnlyMode(property, otherMode));
            }
        });
        return Collections.unmodifiableMap(allProperties);
    }

    private static final AnnotationsSupplier KOTLIN_NULLABLE = new AnnotationsSupplier() {
        private final Nullable nullable = AnnotationUtils.synthesizeAnnotation(Nullable.class);

        @Override
        public <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
            return Stream.of(nullable).filter(annotationType::isInstance).map(annotationType::cast);
        }
    };

    // Both directions share one schema per type, so a property Jackson only writes or only reads is marked as such.
    private static SchemaProperty withOnlyMode(SchemaProperty property, SchemaResolver.Mode onlyMode) {
        AccessMode accessMode =
                onlyMode == SchemaResolver.Mode.FOR_SERIALIZATION ? AccessMode.READ_ONLY : AccessMode.WRITE_ONLY;
        Schema accessModeAnnotation =
                AnnotationUtils.synthesizeAnnotation(Map.of("accessMode", accessMode), Schema.class, null);
        AnnotationsSupplier accessModeSupplier = new AnnotationsSupplier() {
            @Override
            public <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
                return Stream.of(accessModeAnnotation)
                        .filter(annotationType::isInstance)
                        .map(annotationType::cast);
            }
        };
        return SchemaPropertyImpl.of(
                property.getType(),
                accessModeSupplier.andThen(property.getAnnotationsSupplier()),
                property.isRequired());
    }

    private Map<String, SchemaProperty> findPropertiesForMode(
            SchemaResolver.Caller caller, JavaType javaType, AnnotationsSupplier annotationsSupplier) {
        ObjectMapper objectMapper = objectMapperSupplier.get(SCHEMA_BUILDING);
        BeanDescription beanDescriptionForType = introspectJavaType(objectMapper, caller.getMode(), javaType);
        MapperConfig<?> mapperConfig = getMapperConfig(objectMapper, caller.getMode());
        return OpenApiMapUtils.buildStringMapFromStream(
                inStableOrder(beanDescriptionForType, mapperConfig).stream()
                        .filter(property -> isAcceptedByAllPropertyFilters(
                                caller, property, beanDescriptionForType, annotationsSupplier, mapperConfig))
                        .map(property -> buildSchemaPropertyPair(property, caller.getMode()))
                        .filter(Objects::nonNull),
                Pair::getKey,
                Pair::getValue);
    }

    // The JVM returns a class's methods in no fixed order, so Jackson's order of properties that have neither a field
    // nor a constructor parameter changes between runs.
    private static List<BeanPropertyDefinition> inStableOrder(
            BeanDescription beanDescription, MapperConfig<?> mapperConfig) {
        List<BeanPropertyDefinition> properties = new ArrayList<>(beanDescription.findProperties());
        if (hasExplicitPropertyOrder(beanDescription, mapperConfig)) {
            return properties;
        }
        List<Integer> methodOnlySlots = IntStream.range(0, properties.size())
                .filter(i -> !properties.get(i).hasField() && !properties.get(i).hasConstructorParameter())
                .boxed()
                .toList();
        List<BeanPropertyDefinition> methodOnlyByName = methodOnlySlots.stream()
                .map(properties::get)
                .sorted(Comparator.comparing(BeanPropertyDefinition::getName))
                .toList();
        for (int i = 0; i < methodOnlySlots.size(); i++) {
            properties.set(methodOnlySlots.get(i), methodOnlyByName.get(i));
        }
        return properties;
    }

    private static boolean hasExplicitPropertyOrder(BeanDescription beanDescription, MapperConfig<?> mapperConfig) {
        AnnotationIntrospector annotationIntrospector = mapperConfig.getAnnotationIntrospector();
        String[] declaredOrder = annotationIntrospector.findSerializationPropertyOrder(beanDescription.getClassInfo());
        return (declaredOrder != null && declaredOrder.length > 0)
                || Boolean.TRUE.equals(
                        annotationIntrospector.findSerializationSortAlphabetically(beanDescription.getClassInfo()))
                || mapperConfig.isEnabled(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }

    @Nullable
    private Pair<String, SchemaProperty> buildSchemaPropertyPair(
            BeanPropertyDefinition property, SchemaResolver.Mode mode) {
        List<AnnotatedMember> annotatedMembers = findAnnotatedMembers(property, mode);
        return annotatedMembers.stream()
                .map(annotationsSupplierFactory::createFromMember)
                .reduce(AnnotationsSupplier::andThen)
                .map(annotationsSupplier -> annotatedMembers.stream().anyMatch(kotlinNullability::isNullable)
                        ? KOTLIN_NULLABLE.andThen(annotationsSupplier)
                        : annotationsSupplier)
                .map(annotationsSupplier -> {
                    JavaType propertyType = property.getPrimaryType();
                    // include more annotations from the actual type of the property
                    AnnotationsSupplier propertyAnnotationsSupplier = annotationsSupplier.andThen(
                            annotationsSupplierFactory.createFromAnnotatedElement(propertyType.getRawClass()));
                    return Pair.<String, SchemaProperty>of(
                            property.getName(),
                            SchemaPropertyImpl.of(propertyType, propertyAnnotationsSupplier, property.isRequired()));
                })
                .orElseGet(() -> {
                    LOGGER.debug("Ignoring property without any annotated members for mode {}: {}", mode, property);
                    return null;
                });
    }

    private record PropertiesKey(JavaType javaType, SchemaResolver.Mode mode) {}

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    private static class SchemaPropertyImpl implements SchemaProperty {
        private final JavaType type;
        private final AnnotationsSupplier annotationsSupplier;
        private final boolean required;
    }

    private static List<AnnotatedMember> findAnnotatedMembers(
            BeanPropertyDefinition property, SchemaResolver.Mode mode) {
        // follows the algorithm of com.fasterxml.jackson.databind.introspect.POJOPropertyBuilder.getPrimaryType
        // but enables finding annotations from any place (and not just finding a type)
        // if no annotated members are found, that property should be ignored for serialization/deserialization
        Stream<Optional<AnnotatedMember>> stream;
        if (mode == SchemaResolver.Mode.FOR_DESERIALIZATION) {
            AnnotatedMethod setter = property.getSetter();
            AnnotatedMember setterParameter =
                    setter != null && setter.getParameterCount() == 1 ? setter.getParameter(0) : null;
            stream = Stream.of(
                    // the earlier the annotated member is in this stream, the higher is its precedence when finding
                    // annotations
                    Optional.ofNullable(property.getConstructorParameter()),
                    Optional.ofNullable(setter),
                    Optional.ofNullable(setterParameter),
                    Optional.ofNullable(property.getField()));
        } else if (mode == SchemaResolver.Mode.FOR_SERIALIZATION) {
            stream = Stream.of(
                    // the earlier the annotated member is in this stream, the higher is its precedence when finding
                    // annotations
                    Optional.ofNullable(property.getGetter()), Optional.ofNullable(property.getField()));
        } else {
            throw new IllegalStateException("Unknown schema resolver mode " + mode);
        }
        return stream.flatMap(Optional::stream).toList();
    }

    private static MapperConfig<?> getMapperConfig(ObjectMapper objectMapper, SchemaResolver.Mode mode) {
        if (mode == SchemaResolver.Mode.FOR_DESERIALIZATION) {
            return objectMapper.getDeserializationConfig();
        } else if (mode == SchemaResolver.Mode.FOR_SERIALIZATION) {
            return objectMapper.getSerializationConfig();
        }
        throw new IllegalStateException("Cannot find mapper config for schema resolver mode " + mode);
    }

    private static BeanDescription introspectJavaType(
            ObjectMapper objectMapper, SchemaResolver.Mode mode, JavaType javaType) {
        if (mode == SchemaResolver.Mode.FOR_DESERIALIZATION) {
            return objectMapper.getDeserializationConfig().introspect(javaType);
        } else if (mode == SchemaResolver.Mode.FOR_SERIALIZATION) {
            return objectMapper.getSerializationConfig().introspect(javaType);
        } else {
            throw new IllegalStateException("Unknown schema resolver mode " + mode);
        }
    }

    private boolean isAcceptedByAllPropertyFilters(
            SchemaResolver.Caller caller,
            BeanPropertyDefinition property,
            BeanDescription beanDescriptionForType,
            AnnotationsSupplier annotationsSupplierForType,
            MapperConfig<?> mapperConfig) {
        return propertyFilters.stream()
                .allMatch(filter -> filter.accept(
                        caller, property, beanDescriptionForType, annotationsSupplierForType, mapperConfig));
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
