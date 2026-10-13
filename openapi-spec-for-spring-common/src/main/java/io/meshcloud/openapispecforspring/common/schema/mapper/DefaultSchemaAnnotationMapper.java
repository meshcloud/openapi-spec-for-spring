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

package io.meshcloud.openapispecforspring.common.schema.mapper;

import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferencedSchemaConsumer;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaCustomizerForSchemaAnnotation;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.model.media.Schema;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultSchemaAnnotationMapper implements SchemaAnnotationMapper {

    private final SchemaCustomizerForSchemaAnnotation schemaCustomizerForSchemaAnnotation;
    private final SchemaResolver schemaResolver;

    @Override
    public Schema buildFromAnnotation(
            SchemaResolver.Caller caller,
            io.swagger.v3.oas.annotations.media.Schema schemaAnnotation,
            ReferencedSchemaConsumer referencedSchemaConsumer) {
        Schema schema = schemaResolver.resolveFromClassWithoutReference(
                caller, schemaAnnotation.implementation(), referencedSchemaConsumer);
        schemaCustomizerForSchemaAnnotation.applyFromAnnotation(
                schema,
                schemaAnnotation,
                (clazz, schemaConsumer) -> referencedSchemaConsumer.alwaysAsReference(
                        schemaResolver.resolveFromClassWithoutReference(caller, clazz, referencedSchemaConsumer),
                        schemaConsumer));
        return schema;
    }
}
