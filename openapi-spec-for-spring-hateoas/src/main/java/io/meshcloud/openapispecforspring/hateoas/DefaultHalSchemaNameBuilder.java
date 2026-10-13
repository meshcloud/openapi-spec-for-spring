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

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;

@RequiredArgsConstructor
public class DefaultHalSchemaNameBuilder implements HalSchemaNameBuilder {

    private final SchemaNameBuilder schemaNameBuilder;

    @Override
    public String buildFromType(SchemaResolver.Caller caller, JavaType modelType) {
        return prefix(modelType.getRawClass())
                + schemaNameBuilder.buildFromType(caller, HalModelTypes.relationTargetType(modelType));
    }

    private static String prefix(Class<?> modelClass) {
        if (modelClass == EntityModel.class) {
            return "HalEntityModel";
        }
        if (modelClass == PagedModel.class) {
            return "HalPage";
        }
        if (modelClass == CollectionModel.class) {
            return "HalCollection";
        }
        throw new IllegalArgumentException("No HAL schema name for " + modelClass);
    }
}
