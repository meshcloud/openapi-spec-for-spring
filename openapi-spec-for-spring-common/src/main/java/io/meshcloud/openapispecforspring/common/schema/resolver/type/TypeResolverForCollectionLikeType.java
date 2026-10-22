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

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialTypeBuilder;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class TypeResolverForCollectionLikeType implements InitialTypeBuilder, InitialSchemaBuilder, TypeResolver {

    public static final int ORDER = DEFAULT_ORDER;

    private final TypeResolverForCollectionLikeTypeSupport support;

    @Nullable
    @Override
    public InitialType build(
            SchemaResolver.Caller caller,
            JavaType javaType,
            AnnotationsSupplier annotationsSupplier,
            RecursiveBuilder recursiveBuilder) {
        return support.build(
                this, TypeResolverForCollectionLikeType::isCollectionOrArray, javaType, annotationsSupplier);
    }

    // Jackson writes byte[] as a Base64 string.
    private static boolean isCollectionOrArray(JavaType javaType) {
        return javaType.isCollectionLikeType() || (javaType.isArrayType() && !javaType.hasRawClass(byte[].class));
    }

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        Schema schema = support.buildFromType(this, initialType);
        setUniqueItemsIfAssignableToSet(schema, initialType.getType());
        return schema;
    }

    @Nullable
    @Override
    public RecursionKey resolve(
            SchemaResolver.Caller caller,
            Schema schema,
            InitialType initialType,
            SchemaBuilderFromType schemaBuilderFromType) {
        return support.resolve(
                this, schema, initialType, () -> initialType.getType().getContentType(), schemaBuilderFromType);
    }

    private void setUniqueItemsIfAssignableToSet(@Nullable Schema schema, JavaType type) {
        if (schema != null && Set.class.isAssignableFrom(type.getRawClass())) {
            schema.setUniqueItems(true);
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
