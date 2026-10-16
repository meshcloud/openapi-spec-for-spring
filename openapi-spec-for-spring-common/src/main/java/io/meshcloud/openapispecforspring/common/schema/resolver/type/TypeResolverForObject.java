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

package io.meshcloud.openapispecforspring.common.schema.resolver.type;

import static io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier.Purpose.SCHEMA_BUILDING;
import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.buildStringMapFromStream;
import static io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils.laterThan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaPropertiesCustomizer;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaPropertiesCustomizer.SchemaPropertyCustomizer;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.SchemaPropertiesResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.SchemaProperty;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
@Slf4j
public class TypeResolverForObject implements InitialSchemaBuilder, TypeResolver {

    // this resolver does not have any condition, so run this always later than the other resolvers as a fallback
    public static final int ORDER = laterThan(DEFAULT_ORDER);

    private final SchemaNameBuilder schemaNameBuilder;
    private final List<SchemaPropertiesResolver> schemaPropertiesResolvers;
    private final List<SchemaPropertiesCustomizer> schemaPropertiesCustomizers;
    private final OpenApiObjectMapperSupplier objectMapperSupplier;

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        return new ObjectSchema(schemaNameBuilder.buildFromType(caller, initialType.getType()));
    }

    @Override
    @Nullable
    public RecursionKey resolve(
            SchemaResolver.Caller caller,
            Schema schema,
            InitialType initialType,
            SchemaBuilderFromType schemaBuilderFromType) {
        if (schema instanceof ObjectSchema) {
            Map<String, SchemaProperty> properties = OpenApiMapUtils.buildStringMapFromStream(
                    schemaPropertiesResolvers.stream()
                            .flatMap(resolver -> resolver
                                    .findProperties(caller, initialType.getType(), initialType.getAnnotationsSupplier())
                                    .entrySet()
                                    .stream()),
                    Map.Entry::getKey,
                    Map.Entry::getValue);

            if (properties.isEmpty() && isConcrete(initialType.getType()) && schema.getAdditionalProperties() == null) {
                schema.setAdditionalProperties(false);
            }

            Map<String, PropertyCustomizer> propertyCustomizers =
                    buildPropertyCustomizers(schema, initialType, properties.keySet());

            properties.forEach((propertyName, property) -> {
                if (property.isRequired()) {
                    schema.addRequired(propertyName);
                }
                PropertyCustomizer propertyCustomizer = Objects.requireNonNull(propertyCustomizers.get(propertyName));
                schemaBuilderFromType.buildSchemaFromType(
                        property.getType(),
                        property.getAnnotationsSupplier(),
                        propertySchema -> schema.setProperty(
                                propertyName,
                                propertyCustomizer.customize(
                                        propertySchema, property.getType(), property.getAnnotationsSupplier())));
            });

            return new UniqueSchemaKey(initialType.getType(), getSchemaSnapshot(schema));
        }
        return null;
    }

    // Jackson writes an interface or abstract class with the properties of the actual class.
    private static boolean isConcrete(JavaType javaType) {
        return !javaType.isInterface() && !javaType.isAbstract();
    }

    private String getSchemaSnapshot(Schema schema) {
        try {
            return objectMapperSupplier.get(SCHEMA_BUILDING).writeValueAsString(schema);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialized schema for snapshot", e);
        }
    }

    private Map<String, PropertyCustomizer> buildPropertyCustomizers(
            Schema schema, InitialType initialType, Set<String> propertyNames) {
        Map<String, PropertyCustomizer> propertyCustomizers =
                buildStringMapFromStream(propertyNames.stream(), x -> x, ignored -> new PropertyCustomizer());
        // capture the customization callbacks already here
        schemaPropertiesCustomizers.forEach(customizer -> customizer.customize(
                schema, initialType.getType(), initialType.getAnnotationsSupplier(), propertyCustomizers));
        return propertyCustomizers;
    }

    @RequiredArgsConstructor
    private static class PropertyCustomizer implements SchemaPropertiesCustomizer.SchemaPropertyCallback {

        private final List<SchemaPropertyCustomizer> schemaPropertyCustomizers = new ArrayList<>();

        @Override
        public void customize(SchemaPropertyCustomizer propertySchemaCustomizer) {
            this.schemaPropertyCustomizers.add(propertySchemaCustomizer);
        }

        public Schema customize(Schema propertySchema, JavaType javaType, AnnotationsSupplier annotationsSupplier) {
            schemaPropertyCustomizers.forEach(
                    customizer -> customizer.customize(propertySchema, javaType, annotationsSupplier));
            // avoid running customizers multiple again when referenced schemas are consumed
            schemaPropertyCustomizers.clear();
            return propertySchema;
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode // important!
    private static class UniqueSchemaKey implements RecursionKey {
        private final JavaType javaType;
        private final String schemaSnapshot;
    }

    @EqualsAndHashCode(callSuper = true)
    private static class ObjectSchema extends Schema {
        public ObjectSchema(String name) {
            setType("object");
            setName(name);
        }
    }
}
