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

import io.meshcloud.openapispecforspring.common.mapper.ApiResponseAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ContentAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultApiResponseAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ExtensionAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.HeaderAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.LinkAnnotationMapper;
import io.meshcloud.openapispecforspring.common.operation.response.ApiResponseDefaultProvider;
import io.meshcloud.openapispecforspring.common.operation.response.DefaultApiResponseDefaultProvider;
import io.meshcloud.openapispecforspring.common.operation.response.DefaultOperationApiResponsesDescriptionCustomizer;
import io.meshcloud.openapispecforspring.common.operation.response.DefaultOperationApiResponsesFromMethodCustomizer;
import io.meshcloud.openapispecforspring.common.operation.response.DefaultOperationResponseCustomizer;
import io.meshcloud.openapispecforspring.common.operation.response.OperationApiResponsesCustomizer;
import io.meshcloud.openapispecforspring.common.operation.response.OperationApiResponsesDescriptionCustomizer;
import io.meshcloud.openapispecforspring.common.operation.response.OperationApiResponsesFromMethodCustomizer;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class OpenApiSpecOperationResponseAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationResponseCustomizer defaultOperationResponseCustomizer(
            ApiResponseAnnotationMapper apiResponseAnnotationMapper,
            List<OperationApiResponsesCustomizer> apiResponsesCustomizers) {
        return new DefaultOperationResponseCustomizer(apiResponseAnnotationMapper, apiResponsesCustomizers);
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiResponseAnnotationMapper defaultApiResponseAnnotationMapper(
            HeaderAnnotationMapper headerAnnotationMapper,
            ContentAnnotationMapper contentAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper,
            LinkAnnotationMapper linkAnnotationMapper) {
        return new DefaultApiResponseAnnotationMapper(
                headerAnnotationMapper, contentAnnotationMapper, extensionAnnotationMapper, linkAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public OperationApiResponsesFromMethodCustomizer defaultOperationApiResponsesFromMethodCustomizer(
            ApiResponseDefaultProvider apiResponseDefaultProvider,
            SchemaResolver schemaResolver,
            List<HandlerMethod.ResponseMapper> handlerMethodResponseMappers) {
        return new DefaultOperationApiResponsesFromMethodCustomizer(
                apiResponseDefaultProvider, schemaResolver, handlerMethodResponseMappers);
    }

    @Bean
    @ConditionalOnMissingBean
    public OperationApiResponsesDescriptionCustomizer operationApiResponsesDescriptionCustomizer() {
        return new DefaultOperationApiResponsesDescriptionCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApiResponseDefaultProvider defaultApiResponseDefaultProvider() {
        return new DefaultApiResponseDefaultProvider();
    }
}
