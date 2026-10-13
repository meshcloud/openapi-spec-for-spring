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

import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.springframework.hateoas.PagedModel;

public class InitialSchemaBuilderForPageMetadata implements InitialSchemaBuilder {

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        if (initialType.getType().getRawClass() != PagedModel.PageMetadata.class) {
            return null;
        }
        Schema schema = Schema.builder().type("object").build();
        schema.setName("HalPageMetadata");
        schema.setReadOnly(true);
        for (String property : List.of("size", "totalElements", "totalPages", "number")) {
            schema.setProperty(
                    property, Schema.builder().type("integer").format("int64").build());
            schema.addRequired(property);
        }
        return schema;
    }
}
