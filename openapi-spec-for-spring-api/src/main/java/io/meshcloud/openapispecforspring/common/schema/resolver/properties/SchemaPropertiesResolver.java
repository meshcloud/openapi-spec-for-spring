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
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils;
import java.util.Map;

/**
 * Properties resolver for Jackson type.
 *
 * <p>Prefer using {@link SchemaPropertyFilter} whenever possible.
 */
public interface SchemaPropertiesResolver extends OpenApiOrderedUtils.DefaultOrdered {
    /**
     * Find properties for given java type.
     *
     * @param caller              caller of the schema resolver
     * @param javaType            Jackson java type
     * @param annotationsSupplier annotations supplier for given java type
     * @return map of properties (key is property name)
     */
    Map<String, SchemaProperty> findProperties(
            SchemaResolver.Caller caller, JavaType javaType, AnnotationsSupplier annotationsSupplier);
}
