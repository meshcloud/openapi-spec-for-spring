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
import com.fasterxml.jackson.databind.ObjectMapper;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.TypeResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.model.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.LinkRelationProvider;

/**
 * No property is required, because Spring HATEOAS leaves out {@code _embedded} for empty content, {@code _links}
 * without links and {@code page} without page metadata.
 */
@RequiredArgsConstructor
public class TypeResolverForCollectionModel implements InitialSchemaBuilder, TypeResolver {

    public static final int ORDER = DEFAULT_ORDER;

    private final HalSchemaNameBuilder halSchemaNameBuilder;
    private final LinkRelationProvider linkRelationProvider;
    private final AnnotationsSupplierFactory annotationsSupplierFactory;
    private final OpenApiObjectMapperSupplier objectMapperSupplier;

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        Class<?> rawClass = initialType.getType().getRawClass();
        if (rawClass == CollectionModel.class || rawClass == PagedModel.class) {
            return new CollectionModelSchema(halSchemaNameBuilder.buildFromType(caller, initialType.getType()));
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
        if (schema instanceof CollectionModelSchema) {
            JavaType modelType = initialType.getType();
            ObjectMapper objectMapper = objectMapperSupplier.get(SCHEMA_BUILDING);
            schema.setProperty("_embedded", buildEmbedded(modelType, schemaBuilderFromType));
            schemaBuilderFromType.buildSchemaFromType(
                    objectMapper.constructType(Links.class),
                    AnnotationsSupplier.EMPTY,
                    links -> schema.setProperty("_links", links));
            if (modelType.getRawClass() == PagedModel.class) {
                schemaBuilderFromType.buildSchemaFromType(
                        objectMapper.constructType(PagedModel.PageMetadata.class),
                        AnnotationsSupplier.EMPTY,
                        page -> schema.setProperty("page", page));
            }
        }
        return null;
    }

    private Schema buildEmbedded(JavaType modelType, SchemaBuilderFromType schemaBuilderFromType) {
        Schema embedded = Schema.builder().type("object").build();
        Class<?> relationTargetClass =
                HalModelTypes.relationTargetType(modelType).getRawClass();
        if (relationTargetClass == Object.class) {
            return embedded;
        }
        JavaType contentType = HalModelTypes.contentType(modelType);
        Schema contentArray = Schema.builder().type("array").build();
        schemaBuilderFromType.buildSchemaFromType(
                contentType,
                annotationsSupplierFactory.createFromAnnotatedElement(contentType.getRawClass()),
                contentArray::setItems);
        embedded.setProperty(
                linkRelationProvider
                        .getCollectionResourceRelFor(relationTargetClass)
                        .value(),
                contentArray);
        return embedded;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @EqualsAndHashCode(callSuper = true)
    private static class CollectionModelSchema extends Schema {
        CollectionModelSchema(String name) {
            setType("object");
            setName(name);
        }
    }
}
