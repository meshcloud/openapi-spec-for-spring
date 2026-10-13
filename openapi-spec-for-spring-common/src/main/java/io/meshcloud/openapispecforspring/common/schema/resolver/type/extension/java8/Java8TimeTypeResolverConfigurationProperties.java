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

package io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.java8;

import static io.meshcloud.openapispecforspring.common.util.OpenApiConstants.CONFIG_PROPERTIES_PREFIX;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(CONFIG_PROPERTIES_PREFIX + ".extension.java8.time")
@Getter
@Setter
public class Java8TimeTypeResolverConfigurationProperties {

    /**
     * Format to use in schema type.
     */
    private Format format = Format.INFER_FROM_OBJECT_MAPPER;

    @RequiredArgsConstructor
    public enum Format {
        INFER_FROM_OBJECT_MAPPER,
        ISO8601,
        UNIX_EPOCH_SECONDS;
    }
}
