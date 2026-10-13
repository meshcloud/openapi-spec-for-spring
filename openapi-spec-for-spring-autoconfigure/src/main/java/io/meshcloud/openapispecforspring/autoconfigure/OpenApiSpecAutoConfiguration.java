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

import io.meshcloud.openapispecforspring.common.DefaultOpenApiCustomizer;
import io.meshcloud.openapispecforspring.common.OpenApiConfigurationProperties;
import io.meshcloud.openapispecforspring.common.OpenApiCustomizer;
import io.meshcloud.openapispecforspring.common.OpenApiSpecBuilder;
import io.meshcloud.openapispecforspring.common.info.OpenApiInfoSupplier;
import io.meshcloud.openapispecforspring.common.mapper.ExtensionAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ExternalDocumentationAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ServerAnnotationMapper;
import io.meshcloud.openapispecforspring.common.paths.PathsBuilder;
import io.meshcloud.openapispecforspring.common.reference.ReferencedItemSupportFactory;
import io.meshcloud.openapispecforspring.common.supplier.OpenAPIDefinitionAnnotationSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiServersSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiSpringBootApplicationAnnotationsSupplier;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
    OpenApiSpecAnnotationAutoConfiguration.class,
    OpenApiSpecFilterAutoConfiguration.class,
    OpenApiSpecInfoAutoConfiguration.class,
    OpenApiSpecMapperAutoConfiguration.class,
    OpenApiSpecOperationAutoConfiguration.class,
    OpenApiSpecPathsAutoConfiguration.class,
    OpenApiSpecReferenceAutoConfiguration.class,
    OpenApiSpecSchemaAutoConfiguration.class,
    OpenApiSpecSupplierAutoConfiguration.class,
})
@EnableConfigurationProperties(OpenApiConfigurationProperties.class)
public class OpenApiSpecAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenApiSpecBuilder openApiSpecBuilder(
            PathsBuilder pathsBuilder,
            ReferencedItemSupportFactory referencedItemSupportFactory,
            List<OpenApiCustomizer> openApiCustomizers) {
        return new OpenApiSpecBuilder(pathsBuilder, referencedItemSupportFactory, openApiCustomizers);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOpenApiCustomizer defaultOpenApiCustomizer(
            OpenApiInfoSupplier openApiInfoSupplier,
            ServerAnnotationMapper serverAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper,
            ExternalDocumentationAnnotationMapper externalDocumentationAnnotationMapper,
            List<OpenApiServersSupplier> openApiServersSuppliers,
            OpenApiSpringBootApplicationAnnotationsSupplier springBootApplicationAnnotationsSupplier,
            OpenAPIDefinitionAnnotationSupplier openAPIDefinitionAnnotationSupplier) {
        return new DefaultOpenApiCustomizer(
                serverAnnotationMapper,
                externalDocumentationAnnotationMapper,
                extensionAnnotationMapper,
                openApiInfoSupplier,
                openApiServersSuppliers,
                springBootApplicationAnnotationsSupplier,
                openAPIDefinitionAnnotationSupplier);
    }
}
