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

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodContentTypesMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodRequestBodyParameterMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodResponseCodeMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodReturnTypeMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHttpMessageConvertersSupplier;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(OpenApiSpecWebMethodMergerAutoConfiguration.class)
public class OpenApiSpecWebMethodAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodMapper.ContextModifierMapper springWebHandlerMethodContextAwareMapper(
            SpringWebHandlerMethodContentTypesMapper springWebHandlerMethodContentTypesMapper) {
        return new SpringWebHandlerMethodMapper.ContextModifierMapper(springWebHandlerMethodContentTypesMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodMapper.RequestBodyMapper springWebHandlerMethodRequestBodyMapper(
            SpringWebHandlerMethodContentTypesMapper springWebHandlerMethodContentTypesMapper,
            SpringWebHandlerMethodRequestBodyParameterMapper springWebHandlerMethodRequestBodyParameterMapper) {
        return new SpringWebHandlerMethodMapper.RequestBodyMapper(
                springWebHandlerMethodContentTypesMapper, springWebHandlerMethodRequestBodyParameterMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodMapper.ResponseMapper springWebHandlerMethodResponseMapper(
            SpringWebHandlerMethodContentTypesMapper springWebHandlerMethodContentTypesMapper,
            SpringWebHandlerMethodResponseCodeMapper springWebHandlerMethodResponseCodeMapper,
            SpringWebHandlerMethodReturnTypeMapper springWebHandlerMethodReturnTypeMapper) {
        return new SpringWebHandlerMethodMapper.ResponseMapper(
                springWebHandlerMethodContentTypesMapper,
                springWebHandlerMethodResponseCodeMapper,
                springWebHandlerMethodReturnTypeMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodRequestBodyParameterMapper springWebHandlerMethodRequestBodyParameterMapper() {
        return new SpringWebHandlerMethodRequestBodyParameterMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodReturnTypeMapper springWebHandlerMethodReturnTypeMapper(
            AnnotationsSupplierFactory annotationsSupplierFactory) {
        return new SpringWebHandlerMethodReturnTypeMapper(annotationsSupplierFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodResponseCodeMapper springWebHandlerMethodResponseCodeMapper() {
        return new SpringWebHandlerMethodResponseCodeMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodContentTypesMapper springWebHandlerMethodContentTypesMapper(
            ObjectProvider<SpringWebHttpMessageConvertersSupplier> httpMessageConvertersSupplier,
            SpringWebHandlerMethodRequestBodyParameterMapper springWebHandlerMethodRequestBodyParameterMapper) {
        return new SpringWebHandlerMethodContentTypesMapper(
                httpMessageConvertersSupplier.getIfAvailable(() -> List::of),
                springWebHandlerMethodRequestBodyParameterMapper);
    }
}
