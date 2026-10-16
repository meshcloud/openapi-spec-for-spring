/*-
 * #%L
 * OpenAPI Spec for Spring :: API
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

package io.meshcloud.openapispecforspring.common.schema.resolver.properties;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;

/**
 * A resolved schema property.
 *
 * @see SchemaPropertiesResolver
 */
public interface SchemaProperty {
    /**
     * Type of the property.
     *
     * @return Jackson java type
     */
    JavaType getType();

    /**
     * Annotations associated with the type.
     *
     * @return annotations supplier
     */
    AnnotationsSupplier getAnnotationsSupplier();

    /**
     * Jackson's required marker, set by {@code @JsonProperty(required = true)} or by a module such as
     * jackson-module-kotlin.
     */
    default boolean isRequired() {
        return false;
    }
}
