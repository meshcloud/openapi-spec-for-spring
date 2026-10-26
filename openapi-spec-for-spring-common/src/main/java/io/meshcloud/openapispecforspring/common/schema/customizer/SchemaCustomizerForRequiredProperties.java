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

package io.meshcloud.openapispecforspring.common.schema.customizer;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.Map;

public class SchemaCustomizerForRequiredProperties implements SchemaPropertiesCustomizer {
    @Override
    @SuppressWarnings("deprecation")
    public void customize(
            Schema schema,
            JavaType javaType,
            AnnotationsSupplier annotationsSupplier,
            Map<String, ? extends SchemaPropertyCallback> properties) {
        properties.forEach((propertyName, customizer) -> customizer.customize(
                (propertySchema, propertyJavaType, propertyAnnotationsSupplier) -> propertyAnnotationsSupplier
                        .findAnnotations(io.swagger.v3.oas.annotations.media.Schema.class)
                        .findFirst()
                        .filter(schemaAnnotation -> {
                            if (schemaAnnotation.requiredMode() == REQUIRED) {
                                return true;
                            }
                            return schemaAnnotation.required();
                        })
                        .ifPresent(ignored -> schema.addRequired(propertyName))));
    }
}
