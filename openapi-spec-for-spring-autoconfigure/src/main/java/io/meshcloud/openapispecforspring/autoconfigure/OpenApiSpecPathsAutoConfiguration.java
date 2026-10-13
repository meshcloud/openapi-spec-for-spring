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

import io.meshcloud.openapispecforspring.common.filter.handlermethod.HandlerMethodFilter;
import io.meshcloud.openapispecforspring.common.filter.operation.OperationPostFilter;
import io.meshcloud.openapispecforspring.common.filter.operation.OperationPreFilter;
import io.meshcloud.openapispecforspring.common.filter.pathitem.PathItemFilter;
import io.meshcloud.openapispecforspring.common.operation.OperationBuilder;
import io.meshcloud.openapispecforspring.common.operation.id.OperationIdConflictResolver;
import io.meshcloud.openapispecforspring.common.paths.DefaultPathItemSharedParametersCustomizer;
import io.meshcloud.openapispecforspring.common.paths.DefaultPathItemSharedServersCustomizer;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethodsProvider;
import io.meshcloud.openapispecforspring.common.paths.PathItemBuilderFactory;
import io.meshcloud.openapispecforspring.common.paths.PathItemCustomizer;
import io.meshcloud.openapispecforspring.common.paths.PathsBuilder;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class OpenApiSpecPathsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public PathsBuilder pathsBuilder(
            HandlerMethodsProvider handlerMethodsProvider,
            List<HandlerMethod.Merger> handlerMethodMergers,
            PathItemBuilderFactory pathItemBuilderFactory,
            List<PathItemFilter> pathItemFilters,
            List<HandlerMethodFilter> handlerMethodFilters,
            OperationIdConflictResolver operationIdConflictResolver) {
        return new PathsBuilder(
                handlerMethodsProvider,
                handlerMethodMergers,
                pathItemBuilderFactory,
                pathItemFilters,
                handlerMethodFilters,
                operationIdConflictResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public PathItemBuilderFactory pathItemBuilderFactory(
            OperationBuilder operationBuilder,
            List<OperationPreFilter> operationPreFilters,
            List<OperationPostFilter> operationPostFilters,
            List<PathItemCustomizer> pathItemCustomizers) {
        return new PathItemBuilderFactory(
                operationBuilder, operationPreFilters, operationPostFilters, pathItemCustomizers);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultPathItemSharedParametersCustomizer defaultPathItemSharedParametersCustomizer() {
        return new DefaultPathItemSharedParametersCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultPathItemSharedServersCustomizer defaultPathItemSharedServersCustomizer() {
        return new DefaultPathItemSharedServersCustomizer();
    }
}
