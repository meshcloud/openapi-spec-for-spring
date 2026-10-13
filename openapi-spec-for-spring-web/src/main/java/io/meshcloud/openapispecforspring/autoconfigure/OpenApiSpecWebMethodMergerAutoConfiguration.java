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

import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodContentTypesMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodRequestBodyParameterMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodResponseCodeMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodReturnTypeMapper;
import io.meshcloud.openapispecforspring.common.paths.method.merger.MergedSpringWebHandlerMethodMapper;
import io.meshcloud.openapispecforspring.common.paths.method.merger.SpringWebHandlerMethodIdentifierMerger;
import io.meshcloud.openapispecforspring.common.paths.method.merger.SpringWebHandlerMethodMerger;
import io.meshcloud.openapispecforspring.common.paths.method.merger.SpringWebHandlerMethodParameterMerger;
import io.meshcloud.openapispecforspring.common.paths.method.merger.SpringWebHandlerMethodRequestBodyMerger;
import io.meshcloud.openapispecforspring.common.paths.method.merger.SpringWebHandlerMethodResponseMerger;
import io.meshcloud.openapispecforspring.common.paths.method.merger.SpringWebHandlerMethodTypeMerger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class OpenApiSpecWebMethodMergerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodMerger defaultSpringWebHandlerMethodMerger(
            SpringWebHandlerMethodParameterMerger springWebHandlerMethodParameterMerger,
            SpringWebHandlerMethodIdentifierMerger springWebHandlerMethodIdentifierMerger) {
        return new SpringWebHandlerMethodMerger(
                springWebHandlerMethodParameterMerger, springWebHandlerMethodIdentifierMerger);
    }

    @Bean
    @ConditionalOnMissingBean
    public MergedSpringWebHandlerMethodMapper.ContextModifierMapper
            mergedSpringWebHandSpringWebContextModifierMapper() {
        return new MergedSpringWebHandlerMethodMapper.ContextModifierMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public MergedSpringWebHandlerMethodMapper.RequestBodyMapper mergedSpringWebHandSpringWebRequestBodyParameterMapper(
            SpringWebHandlerMethodRequestBodyMerger springWebHandlerMethodRequestBodyMerger) {
        return new MergedSpringWebHandlerMethodMapper.RequestBodyMapper(springWebHandlerMethodRequestBodyMerger);
    }

    @Bean
    @ConditionalOnMissingBean
    public MergedSpringWebHandlerMethodMapper.ResponseMapper mergedSpringWebHandlerMethodResponseMapper(
            SpringWebHandlerMethodResponseMerger springWebHandlerMethodResponseMerger) {
        return new MergedSpringWebHandlerMethodMapper.ResponseMapper(springWebHandlerMethodResponseMerger);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodIdentifierMerger springWebHandlerMethodIdentifierMerger() {
        return new SpringWebHandlerMethodIdentifierMerger();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodTypeMerger springWebHandlerMethodTypeMerger() {
        return new SpringWebHandlerMethodTypeMerger();
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodParameterMerger springWebHandlerMethodParameterMerger(
            SpringWebHandlerMethodTypeMerger springWebHandlerMethodTypeMerger) {
        return new SpringWebHandlerMethodParameterMerger(springWebHandlerMethodTypeMerger);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodRequestBodyMerger springWebHandlerMethodRequestBodyMerger(
            SpringWebHandlerMethodTypeMerger springWebHandlerMethodTypeMerger,
            SpringWebHandlerMethodContentTypesMapper springWebHandlerMethodContentTypesMapper,
            SpringWebHandlerMethodRequestBodyParameterMapper springWebHandlerMethodRequestBodyParameterMapper) {

        return new SpringWebHandlerMethodRequestBodyMerger(
                springWebHandlerMethodTypeMerger,
                springWebHandlerMethodContentTypesMapper,
                springWebHandlerMethodRequestBodyParameterMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHandlerMethodResponseMerger springWebHandlerMethodResponseMerger(
            SpringWebHandlerMethodTypeMerger springWebHandlerMethodTypeMerger,
            SpringWebHandlerMethodContentTypesMapper springWebHandlerMethodContentTypesMapper,
            SpringWebHandlerMethodResponseCodeMapper springWebHandlerMethodResponseCodeMapper,
            SpringWebHandlerMethodReturnTypeMapper springWebHandlerMethodReturnTypeMapper) {
        return new SpringWebHandlerMethodResponseMerger(
                springWebHandlerMethodTypeMerger,
                springWebHandlerMethodContentTypesMapper,
                springWebHandlerMethodResponseCodeMapper,
                springWebHandlerMethodReturnTypeMapper);
    }
}
