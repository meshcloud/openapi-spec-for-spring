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

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialTypeBuilder;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class TypeResolverForMapLikeType implements InitialTypeBuilder, InitialSchemaBuilder, TypeResolver {

    public static final int ORDER = DEFAULT_ORDER;

    private final SchemaNameBuilder schemaNameBuilder;
    private final AnnotationsSupplierFactory annotationsSupplierFactory;
    private final OpenApiObjectMapperSupplier objectMapperSupplier;

    @Nullable
    @Override
    public InitialType build(
            SchemaResolver.Caller caller,
            JavaType javaType,
            AnnotationsSupplier annotationsSupplier,
            RecursiveBuilder recursiveBuilder) {
        if (javaType.isMapLikeType()) {
            return new MapLikeInitialType(javaType, annotationsSupplier);
        }
        return null;
    }

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        if (initialType instanceof MapLikeInitialType) {
            return new MapLikeSchema("MapOf" + schemaNameBuilder.buildFromType(caller, valueType(initialType)));
        }
        return null;
    }

    @Nullable
    @Override
    public RecursionKey resolve(
            SchemaResolver.Caller caller,
            Schema schema,
            InitialType initialType,
            SchemaBuilderFromType schemaBuilderFromType) {
        if (initialType instanceof MapLikeInitialType) {
            JavaType valueType = valueType(initialType);
            AnnotationsSupplier valueAnnotations =
                    annotationsSupplierFactory.createFromAnnotatedElement(valueType.getRawClass());
            schemaBuilderFromType.buildSchemaFromType(valueType, valueAnnotations, schema::setAdditionalProperties);
        }
        return null;
    }

    private JavaType valueType(InitialType mapInitialType) {
        return findValueSchemaClass(mapInitialType.getAnnotationsSupplier())
                .map(valueClass -> objectMapperSupplier.get(SCHEMA_BUILDING).constructType(valueClass))
                .orElse(mapInitialType.getType().getContentType());
    }

    private static Optional<Class<?>> findValueSchemaClass(AnnotationsSupplier annotationsSupplier) {
        return annotationsSupplier
                .findAnnotations(io.swagger.v3.oas.annotations.media.Schema.class)
                .map(io.swagger.v3.oas.annotations.media.Schema::additionalPropertiesSchema)
                .filter(valueClass -> valueClass != Void.class)
                .findFirst()
                .map(valueClass -> (Class<?>) valueClass);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @EqualsAndHashCode(callSuper = true)
    private static class MapLikeSchema extends Schema {
        public MapLikeSchema(String name) {
            setName(name);
            setType("object");
        }
    }

    private static class MapLikeInitialType extends InitialType {

        protected MapLikeInitialType(JavaType type, AnnotationsSupplier annotationsSupplier) {
            super(type, annotationsSupplier);
        }
    }
}
