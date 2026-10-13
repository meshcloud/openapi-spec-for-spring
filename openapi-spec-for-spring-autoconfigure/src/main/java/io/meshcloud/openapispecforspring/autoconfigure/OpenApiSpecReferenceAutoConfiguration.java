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

import io.meshcloud.openapispecforspring.common.reference.ReferencedItemSupportFactory;
import io.meshcloud.openapispecforspring.common.reference.fortype.DefaultReferenceDeciderFactory;
import io.meshcloud.openapispecforspring.common.reference.fortype.DefaultReferenceIdentifierBuilderFactory;
import io.meshcloud.openapispecforspring.common.reference.fortype.DefaultReferenceIdentifierConflictResolverFactory;
import io.meshcloud.openapispecforspring.common.reference.handler.ReferencedItemHandlerFactory;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({
    OpenApiSpecReferenceCallbackAutoConfiguration.class,
    OpenApiSpecReferenceExampleAutoConfiguration.class,
    OpenApiSpecReferenceHeaderAutoConfiguration.class,
    OpenApiSpecReferenceLinkAutoConfiguration.class,
    OpenApiSpecReferenceParameterAutoConfiguration.class,
    OpenApiSpecReferenceRequestBodyAutoConfiguration.class,
    OpenApiSpecReferenceResponseAutoConfiguration.class,
    OpenApiSpecReferenceSchemaAutoConfiguration.class,
    OpenApiSpecReferenceSecuritySchemeAutoConfiguration.class,
    OpenApiSpecReferenceTagAutoConfiguration.class
})
public class OpenApiSpecReferenceAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ReferencedItemSupportFactory referencedItemSupportFactory(
            List<ReferencedItemHandlerFactory> referencedItemHandlerFactories) {
        return new ReferencedItemSupportFactory(referencedItemHandlerFactories);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultReferenceIdentifierConflictResolverFactory
            defaultReferenceIdentifierConflictResolverForTypeFactory() {
        return new DefaultReferenceIdentifierConflictResolverFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultReferenceDeciderFactory defaultReferenceDeciderFactory() {
        return new DefaultReferenceDeciderFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultReferenceIdentifierBuilderFactory defaultReferenceIdentifierBuilderFactory() {
        return new DefaultReferenceIdentifierBuilderFactory();
    }
}
