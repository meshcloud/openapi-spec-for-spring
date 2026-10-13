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

package io.meshcloud.openapispecforspring.common.schema.resolver.properties;

import static io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils.earlierThan;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import org.apache.commons.lang3.StringUtils;

public class SchemaPropertyFilterForNamelessMembers implements SchemaPropertyFilter {
    // Run a little earlier to get rid of weird properties early
    public static final int ORDER = earlierThan(DEFAULT_ORDER);

    @Override
    public boolean accept(
            SchemaResolver.Caller caller,
            BeanPropertyDefinition property,
            BeanDescription beanDescriptionForType,
            AnnotationsSupplier annotationsSupplierForType,
            MapperConfig<?> mapperConfig) {
        // properties must have a name, otherwise: ignore them
        return StringUtils.isNotBlank(property.getName());
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
