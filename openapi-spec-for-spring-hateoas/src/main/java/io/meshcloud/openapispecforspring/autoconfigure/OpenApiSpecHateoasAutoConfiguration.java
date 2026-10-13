/*-
 * #%L
 * OpenAPI Spec for Spring :: HATEOAS
 * %%
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
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.hateoas.DefaultHalSchemaNameBuilder;
import io.meshcloud.openapispecforspring.hateoas.HalSchemaNameBuilder;
import io.meshcloud.openapispecforspring.hateoas.InitialSchemaBuilderForPageMetadata;
import io.meshcloud.openapispecforspring.hateoas.OpenApiHateoasConfigurationProperties;
import io.meshcloud.openapispecforspring.hateoas.SchemaPropertiesResolverForRepresentationModel;
import io.meshcloud.openapispecforspring.hateoas.TypeResolverForCollectionModel;
import io.meshcloud.openapispecforspring.hateoas.TypeResolverForEntityModel;
import io.meshcloud.openapispecforspring.hateoas.TypeResolverForLinks;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.LinkRelationProvider;
import org.springframework.hateoas.server.core.AnnotationLinkRelationProvider;
import org.springframework.hateoas.server.core.DefaultLinkRelationProvider;
import org.springframework.hateoas.server.core.DelegatingLinkRelationProvider;

@ConditionalOnClass(RepresentationModel.class)
@Conditional(OpenApiHateoasConfigurationProperties.EnabledCondition.class)
@EnableConfigurationProperties(OpenApiHateoasConfigurationProperties.class)
public class OpenApiSpecHateoasAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public HalSchemaNameBuilder defaultHalSchemaNameBuilder(SchemaNameBuilder schemaNameBuilder) {
        return new DefaultHalSchemaNameBuilder(schemaNameBuilder);
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeResolverForLinks defaultTypeResolverForLinks(OpenApiObjectMapperSupplier openApiObjectMapperSupplier) {
        return new TypeResolverForLinks(openApiObjectMapperSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public InitialSchemaBuilderForPageMetadata defaultInitialSchemaBuilderForPageMetadata() {
        return new InitialSchemaBuilderForPageMetadata();
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeResolverForEntityModel defaultTypeResolverForEntityModel(
            HalSchemaNameBuilder halSchemaNameBuilder,
            AnnotationsSupplierFactory annotationsSupplierFactory,
            OpenApiObjectMapperSupplier openApiObjectMapperSupplier) {
        return new TypeResolverForEntityModel(
                halSchemaNameBuilder, annotationsSupplierFactory, openApiObjectMapperSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeResolverForCollectionModel defaultTypeResolverForCollectionModel(
            HalSchemaNameBuilder halSchemaNameBuilder,
            ObjectProvider<LinkRelationProvider> linkRelationProvider,
            AnnotationsSupplierFactory annotationsSupplierFactory,
            OpenApiObjectMapperSupplier openApiObjectMapperSupplier) {
        return new TypeResolverForCollectionModel(
                halSchemaNameBuilder,
                linkRelationProvider.getIfUnique(() -> new DelegatingLinkRelationProvider(
                        new AnnotationLinkRelationProvider(), new DefaultLinkRelationProvider())),
                annotationsSupplierFactory,
                openApiObjectMapperSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public SchemaPropertiesResolverForRepresentationModel defaultSchemaPropertiesResolverForRepresentationModel(
            OpenApiObjectMapperSupplier openApiObjectMapperSupplier) {
        return new SchemaPropertiesResolverForRepresentationModel(openApiObjectMapperSupplier);
    }
}
