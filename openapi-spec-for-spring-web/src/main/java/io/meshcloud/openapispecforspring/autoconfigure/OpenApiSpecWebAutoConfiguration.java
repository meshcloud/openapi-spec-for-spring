/*-
 * #%L
 * OpenAPI Spec for Spring :: Web
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

import io.meshcloud.openapispecforspring.common.OpenApiConfigurationProperties;
import io.meshcloud.openapispecforspring.common.OpenApiSpecGenerator;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.operation.parameter.DefaultOpenApiSpringWebParameterNameDiscoverer;
import io.meshcloud.openapispecforspring.common.operation.parameter.OpenApiSpringWebParameterNameDiscoverer;
import io.meshcloud.openapispecforspring.common.operation.parameter.converter.DefaultParameterMethodConverterFromMatrixVariableAnnotation;
import io.meshcloud.openapispecforspring.common.operation.parameter.converter.DefaultParameterMethodConverterFromPathVariableAnnotation;
import io.meshcloud.openapispecforspring.common.operation.parameter.converter.DefaultParameterMethodConverterFromRequestHeaderAnnotation;
import io.meshcloud.openapispecforspring.common.operation.parameter.converter.DefaultParameterMethodConverterFromRequestParamAnnotation;
import io.meshcloud.openapispecforspring.common.paths.DefaultSpringWebHandlerMethodBuilder;
import io.meshcloud.openapispecforspring.common.paths.SpringWebHandlerMethodBuilder;
import io.meshcloud.openapispecforspring.common.paths.SpringWebRequestMethodEnumMapper;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.spring.SpringWebResponseEntityInitialTypeBuilder;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiYamlMapper;
import io.meshcloud.openapispecforspring.common.web.OpenApiResource;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.DefaultParameterNameDiscoverer;

@Import({OpenApiSpecWebMethodMergerAutoConfiguration.class, OpenApiSpecWebMethodAutoConfiguration.class})
public class OpenApiSpecWebAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenApiResource openApiResource(
            OpenApiSpecGenerator openApiSpecGenerator,
            OpenApiConfigurationProperties openApiConfigurationProperties,
            OpenApiObjectMapperSupplier openApiObjectMapperSupplier,
            Optional<OpenApiYamlMapper> openApiYamlMapper) {
        return new OpenApiResource(
                openApiSpecGenerator,
                openApiConfigurationProperties,
                openApiObjectMapperSupplier,
                openApiYamlMapper.orElse(null));
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodBuilder springWebHandlerMethodBuilder(
            AnnotationsSupplierFactory annotationsSupplierFactory,
            OpenApiSpringWebParameterNameDiscoverer openApiSpringWebParameterNameDiscoverer) {
        return new DefaultSpringWebHandlerMethodBuilder(
                annotationsSupplierFactory, openApiSpringWebParameterNameDiscoverer);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiSpringWebParameterNameDiscoverer defaultOpenApiSpringWebParameterNameDiscoverer() {
        return new DefaultOpenApiSpringWebParameterNameDiscoverer(new DefaultParameterNameDiscoverer());
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultParameterMethodConverterFromPathVariableAnnotation
            defaultParameterMethodConverterFromPathVariableAnnotation() {
        return new DefaultParameterMethodConverterFromPathVariableAnnotation();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultParameterMethodConverterFromRequestHeaderAnnotation
            defaultParameterMethodConverterFromRequestHeaderAnnotation() {
        return new DefaultParameterMethodConverterFromRequestHeaderAnnotation();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultParameterMethodConverterFromRequestParamAnnotation
            defaultParameterMethodConverterFromRequestParamAnnotation() {
        return new DefaultParameterMethodConverterFromRequestParamAnnotation();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultParameterMethodConverterFromMatrixVariableAnnotation
            defaultParameterMethodConverterFromMatrixVariableAnnotation() {
        return new DefaultParameterMethodConverterFromMatrixVariableAnnotation();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebResponseEntityInitialTypeBuilder springWebResponseEntityInitialTypeBuilder() {
        return new SpringWebResponseEntityInitialTypeBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebRequestMethodEnumMapper springWebRequestMethodEnumMapper() {
        return new SpringWebRequestMethodEnumMapper();
    }
}
