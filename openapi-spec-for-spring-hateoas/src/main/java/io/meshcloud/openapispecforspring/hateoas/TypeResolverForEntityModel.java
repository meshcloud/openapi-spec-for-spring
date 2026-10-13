/*-
 * #%L
 * OpenAPI Spec for Spring :: HATEOAS
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

package io.meshcloud.openapispecforspring.hateoas;

import static io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier.Purpose.SCHEMA_BUILDING;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.TypeResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Links;

@RequiredArgsConstructor
public class TypeResolverForEntityModel implements InitialSchemaBuilder, TypeResolver {

    public static final int ORDER = DEFAULT_ORDER;

    private final HalSchemaNameBuilder halSchemaNameBuilder;
    private final AnnotationsSupplierFactory annotationsSupplierFactory;
    private final OpenApiObjectMapperSupplier objectMapperSupplier;

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        if (initialType.getType().getRawClass() == EntityModel.class) {
            return new EntityModelSchema(halSchemaNameBuilder.buildFromType(caller, initialType.getType()));
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
        if (schema instanceof EntityModelSchema) {
            JavaType contentType = HalModelTypes.contentType(initialType.getType());
            Schema linksObject = Schema.builder().type("object").build();
            List<Schema> allOf = new ArrayList<>(Arrays.asList(new Schema(), linksObject));
            schemaBuilderFromType.buildSchemaFromType(
                    contentType,
                    annotationsSupplierFactory.createFromAnnotatedElement(contentType.getRawClass()),
                    content -> allOf.set(0, content));
            schemaBuilderFromType.buildSchemaFromType(
                    objectMapperSupplier.get(SCHEMA_BUILDING).constructType(Links.class),
                    AnnotationsSupplier.EMPTY,
                    links -> linksObject.setProperty("_links", links));
            schema.setAllOf(allOf);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @EqualsAndHashCode(callSuper = true)
    private static class EntityModelSchema extends Schema {
        EntityModelSchema(String name) {
            setName(name);
        }
    }
}
