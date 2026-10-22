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

package io.meshcloud.openapispecforspring.common.schema.resolver.type;

import static io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier.Purpose.SCHEMA_BUILDING;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.common.util.OpenApiSealedClassUtils;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * Without {@code @JsonTypeInfo}, Jackson writes a sealed type as its concrete subclass, so the schema is anyOf the
 * permitted subclasses: without a discriminator, a value may match several of them, which oneOf forbids.
 */
@RequiredArgsConstructor
public class TypeResolverForSealedClass implements InitialSchemaBuilder, TypeResolver {

    public static final int ORDER = DEFAULT_ORDER;

    private final SchemaNameBuilder schemaNameBuilder;
    private final AnnotationsSupplierFactory annotationsSupplierFactory;
    private final OpenApiObjectMapperSupplier objectMapperSupplier;

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        Class<?> rawClass = initialType.getType().getRawClass();
        if (!rawClass.isSealed()
                || initialType
                        .getAnnotationsSupplier()
                        .findAnnotations(JsonTypeInfo.class)
                        .findAny()
                        .isPresent()) {
            return null;
        }
        return new SealedClassSchema(schemaNameBuilder.buildFromType(caller, initialType.getType()));
    }

    @Nullable
    @Override
    public RecursionKey resolve(
            SchemaResolver.Caller caller,
            Schema schema,
            InitialType initialType,
            SchemaBuilderFromType schemaBuilderFromType) {
        if (!(schema instanceof SealedClassSchema)) {
            return null;
        }
        Class<?> sealedClass = initialType.getType().getRawClass();
        List<Class<?>> permittedSubclasses = OpenApiSealedClassUtils.permittedSubclasses(sealedClass);
        List<Schema> anyOf = new ArrayList<>(
                permittedSubclasses.stream().map(ignored -> new Schema()).toList());
        for (int index = 0; index < permittedSubclasses.size(); index++) {
            int anyOfIndex = index;
            Class<?> subclass = permittedSubclasses.get(index);
            JavaType subclassType = objectMapperSupplier.get(SCHEMA_BUILDING).constructType(subclass);
            schemaBuilderFromType.buildSchemaReferenceFromType(
                    subclassType,
                    annotationsSupplierFactory.createFromAnnotatedElement(subclass),
                    reference -> anyOf.set(anyOfIndex, reference));
        }
        schema.setAnyOf(anyOf);
        return new UniqueKey(sealedClass);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor
    private static class UniqueKey implements RecursionKey {
        private final Class<?> sealedClass;
    }

    private static class SealedClassSchema extends Schema {
        SealedClassSchema(String name) {
            setName(name);
        }
    }
}
