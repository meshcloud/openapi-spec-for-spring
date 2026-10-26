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
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;

/**
 * HAL allows a single link or an array of links per relation. Applications add relations at runtime, so
 * {@code HalLinks} does not list them.
 */
@RequiredArgsConstructor
public class TypeResolverForLinks implements InitialSchemaBuilder, TypeResolver {

    public static final int ORDER = DEFAULT_ORDER;

    private final OpenApiObjectMapperSupplier objectMapperSupplier;

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        Class<?> rawClass = initialType.getType().getRawClass();
        if (rawClass == Links.class) {
            return new HalLinksSchema();
        }
        if (rawClass == Link.class) {
            return buildHalLinkSchema();
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
        if (schema instanceof HalLinksSchema) {
            JavaType linkType = objectMapperSupplier.get(SCHEMA_BUILDING).constructType(Link.class);
            Schema linkArray = Schema.builder().type("array").build();
            List<Schema> singleLinkOrLinkArray = new ArrayList<>(Arrays.asList(new Schema(), linkArray));
            schemaBuilderFromType.buildSchemaReferenceFromType(
                    linkType, AnnotationsSupplier.EMPTY, link -> singleLinkOrLinkArray.set(0, link));
            schemaBuilderFromType.buildSchemaReferenceFromType(
                    linkType, AnnotationsSupplier.EMPTY, linkArray::setItems);
            schema.setAdditionalProperties(
                    Schema.builder().oneOf(singleLinkOrLinkArray).build());
        }
        return null;
    }

    private static Schema buildHalLinkSchema() {
        Schema schema = Schema.builder().type("object").build();
        schema.setName("HalLink");
        schema.setProperty("href", stringSchema());
        schema.setProperty("templated", Schema.builder().type("boolean").build());
        for (String property : List.of("type", "deprecation", "profile", "name", "title", "hreflang")) {
            schema.setProperty(property, stringSchema());
        }
        schema.addRequired("href");
        return schema;
    }

    private static Schema stringSchema() {
        return Schema.builder().type("string").build();
    }

    @Override
    public int getOrder() {
        return ORDER;
    }

    @EqualsAndHashCode(callSuper = true)
    private static class HalLinksSchema extends Schema {
        HalLinksSchema() {
            setType("object");
            setName("HalLinks");
            setReadOnly(true);
        }
    }
}
