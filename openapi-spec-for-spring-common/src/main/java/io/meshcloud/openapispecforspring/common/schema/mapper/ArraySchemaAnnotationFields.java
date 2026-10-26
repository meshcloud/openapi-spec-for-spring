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

import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.mergeWithExistingMap;

import io.meshcloud.openapispecforspring.common.mapper.ExtensionAnnotationMapper;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ArraySchemaAnnotationFields {

    public static void apply(
            Schema schema, ArraySchema annotation, ExtensionAnnotationMapper extensionAnnotationMapper) {
        if (annotation.uniqueItems()) {
            schema.setUniqueItems(true);
        }
        if (annotation.minItems() != Integer.MAX_VALUE) {
            schema.setMinItems(annotation.minItems());
        }
        if (annotation.maxItems() != Integer.MIN_VALUE) {
            schema.setMaxItems(annotation.maxItems());
        }
        mergeWithExistingMap(
                schema::getExtensions,
                schema::setExtensions,
                extensionAnnotationMapper.mapArray(annotation.extensions()));
    }
}
