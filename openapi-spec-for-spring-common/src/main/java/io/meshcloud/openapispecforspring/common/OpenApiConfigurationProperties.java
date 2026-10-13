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

package io.meshcloud.openapispecforspring.common;

import static io.meshcloud.openapispecforspring.common.util.OpenApiConstants.CONFIG_PROPERTIES_PREFIX;

import io.meshcloud.openapispecforspring.common.util.OpenApiConfigurationPropertiesUtils.ConfigurationPropertyCondition;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = CONFIG_PROPERTIES_PREFIX)
@Getter
@Setter
public class OpenApiConfigurationProperties {
    /**
     * Flag to provide the OpenAPI spec as an accessible endpoint.
     */
    private boolean enabled = true;
    /**
     * Path to OpenAPI spec.
     */
    @SuppressWarnings("java:S1075") // suppress hard coded URL warning
    private String apiDocsPath = "/v3/api-docs";

    public static class EnabledCondition extends ConfigurationPropertyCondition<OpenApiConfigurationProperties> {
        public EnabledCondition() {
            super(OpenApiConfigurationProperties.class, OpenApiConfigurationProperties::isEnabled);
        }
    }
}
