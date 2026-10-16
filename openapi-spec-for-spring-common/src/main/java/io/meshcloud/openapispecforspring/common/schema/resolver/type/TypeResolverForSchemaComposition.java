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
import static io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils.earlierThan;

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * Resolves the classes of {@code @Schema(oneOf = ...)} and {@code @Schema(anyOf = ...)} like any other type, so each
 * member gets its request or response view.
 */
@RequiredArgsConstructor
public class TypeResolverForSchemaComposition implements TypeResolver {

    public static final int ORDER = earlierThan(DEFAULT_ORDER);

    private final AnnotationsSupplierFactory annotationsSupplierFactory;
    private final OpenApiObjectMapperSupplier objectMapperSupplier;

    @Nullable
    @Override
    public RecursionKey resolve(
            SchemaResolver.Caller caller,
            Schema schema,
            InitialType initialType,
            SchemaBuilderFromType schemaBuilderFromType) {
        io.swagger.v3.oas.annotations.media.Schema annotation = initialType
                .getAnnotationsSupplier()
                .findAnnotations(io.swagger.v3.oas.annotations.media.Schema.class)
                .filter(candidate -> candidate.oneOf().length > 0 || candidate.anyOf().length > 0)
                .findFirst()
                .orElse(null);
        Class<?> rawClass = initialType.getType().getRawClass();
        if (annotation != null
                && !Arrays.asList(annotation.oneOf()).contains(rawClass)
                && !Arrays.asList(annotation.anyOf()).contains(rawClass)) {
            schema.setType(null);
            schema.setProperties(null);
            schema.setRequired(null);
            resolveMembers(annotation.oneOf(), schema::setOneOf, schemaBuilderFromType);
            resolveMembers(annotation.anyOf(), schema::setAnyOf, schemaBuilderFromType);
        }
        return null;
    }

    private void resolveMembers(
            Class<?>[] memberClasses, Consumer<List<Schema>> setter, SchemaBuilderFromType schemaBuilderFromType) {
        if (memberClasses.length == 0) {
            return;
        }
        List<Schema> members = new ArrayList<>(
                Arrays.stream(memberClasses).map(ignored -> new Schema()).toList());
        for (int index = 0; index < memberClasses.length; index++) {
            int memberIndex = index;
            Class<?> memberClass = memberClasses[index];
            schemaBuilderFromType.buildSchemaFromType(
                    objectMapperSupplier.get(SCHEMA_BUILDING).constructType(memberClass),
                    annotationsSupplierFactory.createFromAnnotatedElement(memberClass),
                    member -> members.set(memberIndex, member));
        }
        setter.accept(members);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
