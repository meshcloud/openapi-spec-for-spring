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

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils;

/**
 * Filter for properties, used by default implementation of {@link SchemaPropertiesResolver}.
 */
@FunctionalInterface
public interface SchemaPropertyFilter extends OpenApiOrderedUtils.DefaultOrdered {

    /**
     * Filter given property with Jackson bean description.
     *
     * @param caller                     caller of the schema resolver
     * @param property                   property definition
     * @param beanDescriptionForType     bean description from Jackson for type owning given property
     * @param annotationsSupplierForType annotations supplier for type owning given property
     * @param mapperConfig               object mapper config from Jackson
     * @return true if property shall be used, false otherwise
     */
    boolean accept(
            SchemaResolver.Caller caller,
            BeanPropertyDefinition property,
            BeanDescription beanDescriptionForType,
            AnnotationsSupplier annotationsSupplierForType,
            MapperConfig<?> mapperConfig);
}
