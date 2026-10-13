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

import io.meshcloud.openapispecforspring.common.annotation.DeprecationDetector;
import io.meshcloud.openapispecforspring.common.mapper.CallbackAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.MapperContext;
import io.meshcloud.openapispecforspring.common.mapper.OperationAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.RequestBodyAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.SecurityRequirementAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.SecuritySchemeAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ServerAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.TagAnnotationMapper;
import io.meshcloud.openapispecforspring.common.operation.OperationBuilder;
import io.meshcloud.openapispecforspring.common.operation.customizer.DefaultDeprecatedOperationCustomizer;
import io.meshcloud.openapispecforspring.common.operation.customizer.DefaultOperationCallbackCustomizer;
import io.meshcloud.openapispecforspring.common.operation.customizer.DefaultOperationIdCustomizer;
import io.meshcloud.openapispecforspring.common.operation.customizer.DefaultOperationSecurityRequirementCustomizer;
import io.meshcloud.openapispecforspring.common.operation.customizer.DefaultOperationSecuritySchemesCustomizer;
import io.meshcloud.openapispecforspring.common.operation.customizer.DefaultOperationServersCustomizer;
import io.meshcloud.openapispecforspring.common.operation.customizer.DefaultOperationTagsCustomizer;
import io.meshcloud.openapispecforspring.common.operation.customizer.DefaultRequestBodyOperationCustomizer;
import io.meshcloud.openapispecforspring.common.operation.customizer.OperationCustomizer;
import io.meshcloud.openapispecforspring.common.operation.id.DefaultOperationIdConflictResolver;
import io.meshcloud.openapispecforspring.common.operation.id.DefaultOperationIdProvider;
import io.meshcloud.openapispecforspring.common.operation.id.OperationIdConflictResolver;
import io.meshcloud.openapispecforspring.common.operation.id.OperationIdProvider;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({OpenApiSpecOperationParameterAutoConfiguration.class, OpenApiSpecOperationResponseAutoConfiguration.class})
public class OpenApiSpecOperationAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OperationBuilder operationBuilder(
            OperationAnnotationMapper operationAnnotationMapper,
            List<OperationCustomizer> operationCustomizers,
            List<HandlerMethod.ContextModifierMapper<MapperContext>> contextModifierMappersForMapperContext) {
        return new OperationBuilder(
                operationAnnotationMapper, operationCustomizers, contextModifierMappersForMapperContext);
    }

    @Bean
    @ConditionalOnMissingBean
    public OperationIdProvider defaultOperationIdProvider() {
        return new DefaultOperationIdProvider();
    }

    @Bean
    @ConditionalOnMissingBean
    public OperationIdConflictResolver defaultOperationIdConflictResolver() {
        return new DefaultOperationIdConflictResolver();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultDeprecatedOperationCustomizer defaultDeprecatedOperationCustomizer(
            DeprecationDetector deprecationDetector) {
        return new DefaultDeprecatedOperationCustomizer(deprecationDetector);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationTagsCustomizer defaultOperationTagsCustomizer(TagAnnotationMapper tagAnnotationMapper) {
        return new DefaultOperationTagsCustomizer(tagAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationSecuritySchemesCustomizer defaultOperationSecuritySchemesCustomizer(
            SecuritySchemeAnnotationMapper securitySchemeAnnotationMapper) {
        return new DefaultOperationSecuritySchemesCustomizer(securitySchemeAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationServersCustomizer defaultOperationServersCustomizer(
            ServerAnnotationMapper serverAnnotationMapper) {
        return new DefaultOperationServersCustomizer(serverAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationSecurityRequirementCustomizer defaultOperationSecurityRequirementCustomizer(
            SecurityRequirementAnnotationMapper securityRequirementAnnotationMapper) {
        return new DefaultOperationSecurityRequirementCustomizer(securityRequirementAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationIdCustomizer defaultOperationIdCustomizer(OperationIdProvider operationIdProvider) {
        return new DefaultOperationIdCustomizer(operationIdProvider);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationCallbackCustomizer defaultOperationCallbackCustomizer(
            CallbackAnnotationMapper callbackAnnotationMapper) {
        return new DefaultOperationCallbackCustomizer(callbackAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultRequestBodyOperationCustomizer defaultRequestBodyOperationCustomizer(
            RequestBodyAnnotationMapper requestBodyAnnotationMapper,
            SchemaResolver schemaResolver,
            List<HandlerMethod.RequestBodyMapper> handlerMethodRequestBodyMappers) {
        return new DefaultRequestBodyOperationCustomizer(
                requestBodyAnnotationMapper, schemaResolver, handlerMethodRequestBodyMappers);
    }
}
