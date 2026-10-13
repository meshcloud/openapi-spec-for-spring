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

import io.meshcloud.openapispecforspring.common.reference.component.header.ReferenceDeciderForHeader;
import io.meshcloud.openapispecforspring.common.reference.component.header.ReferenceIdentifierBuilderForHeader;
import io.meshcloud.openapispecforspring.common.reference.component.header.ReferenceIdentifierConflictResolverForHeader;
import io.meshcloud.openapispecforspring.common.reference.component.header.ReferencedHeadersHandlerFactory;
import io.meshcloud.openapispecforspring.common.reference.fortype.DefaultReferenceDeciderFactory;
import io.meshcloud.openapispecforspring.common.reference.fortype.DefaultReferenceIdentifierBuilderFactory;
import io.meshcloud.openapispecforspring.common.reference.fortype.DefaultReferenceIdentifierConflictResolverFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class OpenApiSpecReferenceHeaderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ReferencedHeadersHandlerFactory referencedHeadersHandlerFactory(
            ReferenceDeciderForHeader referenceDecider,
            ReferenceIdentifierBuilderForHeader referenceIdentifierFactory,
            ReferenceIdentifierConflictResolverForHeader referenceIdentifierConflictResolver) {
        return new ReferencedHeadersHandlerFactory(
                referenceDecider, referenceIdentifierFactory, referenceIdentifierConflictResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReferenceIdentifierBuilderForHeader defaultReferenceIdentifierFactoryForHeader(
            DefaultReferenceIdentifierBuilderFactory factory) {
        return factory.create(defaultImpl -> defaultImpl::buildIdentifier);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReferenceIdentifierConflictResolverForHeader defaultReferenceIdentifierConflictResolverForHeader(
            DefaultReferenceIdentifierConflictResolverFactory factory) {
        return factory.create(defaultImpl -> defaultImpl::resolveConflict);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReferenceDeciderForHeader defaultReferenceDeciderForHeader(DefaultReferenceDeciderFactory factory) {
        return factory.create(defaultImpl -> defaultImpl::turnIntoReference);
    }
}
