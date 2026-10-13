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

import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.supplier.DefaultOpenAPIDefinitionAnnotationSupplier;
import io.meshcloud.openapispecforspring.common.supplier.DefaultOpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.common.supplier.DefaultOpenApiServersSupplier;
import io.meshcloud.openapispecforspring.common.supplier.DefaultOpenApiSpringBootApplicationAnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.supplier.DefaultOpenApiSpringBootApplicationClassSupplier;
import io.meshcloud.openapispecforspring.common.supplier.DefaultOpenApiYamlMapper;
import io.meshcloud.openapispecforspring.common.supplier.OpenAPIDefinitionAnnotationSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiBaseUriSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiServersSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiSpringBootApplicationAnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiSpringBootApplicationClassSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiYamlMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class OpenApiSpecSupplierAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public OpenApiObjectMapperSupplier defaultOpenApiObjectMapperSupplier() {
        return new DefaultOpenApiObjectMapperSupplier();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiSpringBootApplicationClassSupplier defaultOpenApiSpringBootApplicationClassSupplier() {
        return new DefaultOpenApiSpringBootApplicationClassSupplier();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiSpringBootApplicationAnnotationsSupplier defaultOpenApiSpringBootApplicationAnnotationsSupplier(
            OpenApiSpringBootApplicationClassSupplier springBootApplicationClassSupplier,
            AnnotationsSupplierFactory annotationsSupplierFactory) {
        return new DefaultOpenApiSpringBootApplicationAnnotationsSupplier(
                springBootApplicationClassSupplier, annotationsSupplierFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenAPIDefinitionAnnotationSupplier defaultOpenAPIDefinitionAnnotationSupplier(
            OpenApiSpringBootApplicationAnnotationsSupplier openApiSpringBootApplicationAnnotationsSupplier) {
        return new DefaultOpenAPIDefinitionAnnotationSupplier(openApiSpringBootApplicationAnnotationsSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiServersSupplier defaultOpenApiServersSupplier(
            OpenApiBaseUriSupplier openApiBaseUriSupplier // provided by WebMVC or WebFlux
            ) {
        return new DefaultOpenApiServersSupplier(openApiBaseUriSupplier);
    }

    @ConditionalOnClass(YAMLFactory.class)
    @Bean
    public OpenApiYamlMapper defaultOpenApiYamlMapper() {
        return new DefaultOpenApiYamlMapper();
    }
}
