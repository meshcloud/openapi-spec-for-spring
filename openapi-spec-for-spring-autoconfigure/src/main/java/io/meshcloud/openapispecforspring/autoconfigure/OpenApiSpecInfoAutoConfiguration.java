/*-
 * #%L
 * OpenAPI Spec for Spring :: Autoconfigure
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

package io.meshcloud.openapispecforspring.autoconfigure;

import io.meshcloud.openapispecforspring.common.info.DefaultOpenApiInfoSupplier;
import io.meshcloud.openapispecforspring.common.info.DefaultOpenApiVersionSupplier;
import io.meshcloud.openapispecforspring.common.info.OpenApiInfoConfigurationProperties;
import io.meshcloud.openapispecforspring.common.info.OpenApiInfoSupplier;
import io.meshcloud.openapispecforspring.common.info.OpenApiVersionSupplier;
import io.meshcloud.openapispecforspring.common.mapper.InfoAnnotationMapper;
import io.meshcloud.openapispecforspring.common.supplier.OpenAPIDefinitionAnnotationSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiSpringBootApplicationClassSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(OpenApiInfoConfigurationProperties.class)
public class OpenApiSpecInfoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenApiInfoSupplier defaultOpenApiInfoSupplier(
            OpenApiInfoConfigurationProperties infoProperties,
            InfoAnnotationMapper infoAnnotationMapper,
            OpenApiVersionSupplier openApiVersionSupplier,
            OpenApiSpringBootApplicationClassSupplier springBootApplicationClassSupplier,
            OpenAPIDefinitionAnnotationSupplier openAPIDefinitionAnnotationSupplier) {
        return new DefaultOpenApiInfoSupplier(
                infoProperties,
                infoAnnotationMapper,
                openApiVersionSupplier,
                springBootApplicationClassSupplier,
                openAPIDefinitionAnnotationSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiVersionSupplier defaultOpenApiVersionSupplier() {
        return new DefaultOpenApiVersionSupplier();
    }
}
