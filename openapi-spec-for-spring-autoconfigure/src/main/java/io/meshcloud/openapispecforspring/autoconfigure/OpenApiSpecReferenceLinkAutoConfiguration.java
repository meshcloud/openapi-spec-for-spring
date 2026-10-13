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

import io.meshcloud.openapispecforspring.common.reference.component.link.ReferenceDeciderForLink;
import io.meshcloud.openapispecforspring.common.reference.component.link.ReferenceIdentifierBuilderForLink;
import io.meshcloud.openapispecforspring.common.reference.component.link.ReferenceIdentifierConflictResolverForLink;
import io.meshcloud.openapispecforspring.common.reference.component.link.ReferencedLinksHandlerFactory;
import io.meshcloud.openapispecforspring.common.reference.fortype.DefaultReferenceDeciderFactory;
import io.meshcloud.openapispecforspring.common.reference.fortype.DefaultReferenceIdentifierBuilderFactory;
import io.meshcloud.openapispecforspring.common.reference.fortype.DefaultReferenceIdentifierConflictResolverFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class OpenApiSpecReferenceLinkAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ReferencedLinksHandlerFactory referencedLinksHandlerFactory(
            ReferenceDeciderForLink referenceDecider,
            ReferenceIdentifierBuilderForLink referenceIdentifierFactory,
            ReferenceIdentifierConflictResolverForLink referenceIdentifierConflictResolver) {
        return new ReferencedLinksHandlerFactory(
                referenceDecider, referenceIdentifierFactory, referenceIdentifierConflictResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReferenceIdentifierBuilderForLink defaultReferenceIdentifierFactoryForLink(
            DefaultReferenceIdentifierBuilderFactory factory) {
        return factory.create(defaultImpl -> defaultImpl::buildIdentifier);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReferenceIdentifierConflictResolverForLink defaultReferenceIdentifierConflictResolverForLink(
            DefaultReferenceIdentifierConflictResolverFactory factory) {
        return factory.create(defaultImpl -> defaultImpl::resolveConflict);
    }

    @Bean
    @ConditionalOnMissingBean
    public ReferenceDeciderForLink defaultReferenceDeciderForLink(DefaultReferenceDeciderFactory factory) {
        return factory.create(defaultImpl -> defaultImpl::turnIntoReference);
    }
}
