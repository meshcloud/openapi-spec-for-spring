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

package io.meshcloud.openapispecforspring.common.schema.resolver.type.initial;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class InitialSchemaBuilderForEnum implements InitialSchemaBuilder {
    private final SchemaNameBuilder schemaNameBuilder;

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        JavaType javaType = initialType.getType();
        if (javaType.isEnumImplType()) {
            List<Object> enumValues = Arrays.asList(javaType.getRawClass().getEnumConstants());
            return Schema.builder()
                    .name(schemaNameBuilder.buildFromType(caller, javaType))
                    .type("string")
                    .enumValues(enumValues)
                    .build();
        }
        return null;
    }
}
