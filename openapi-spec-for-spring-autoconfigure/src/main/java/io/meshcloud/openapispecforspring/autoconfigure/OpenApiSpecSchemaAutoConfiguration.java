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

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.annotation.DeprecationDetector;
import io.meshcloud.openapispecforspring.common.annotation.NullnessDetector;
import io.meshcloud.openapispecforspring.common.mapper.ExtensionAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ExternalDocumentationAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ParsableValueMapper;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaCustomizer;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaCustomizerForDeprecated;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaCustomizerForNullable;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaCustomizerForRequiredProperties;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaCustomizerForSchemaAnnotation;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaPropertiesCustomizer;
import io.meshcloud.openapispecforspring.common.schema.resolver.DefaultSchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.DefaultSchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.DefaultSchemaPropertiesResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.SchemaPropertiesResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.SchemaPropertyFilter;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.SchemaPropertyFilterForIgnoredMembers;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.SchemaPropertyFilterForNamelessMembers;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.TypeResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.TypeResolverForCollectionLikeType;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.TypeResolverForCollectionLikeTypeSupport;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.TypeResolverForMapLikeType;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.TypeResolverForObject;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilderForAnyValue;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilderForEnum;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilderForPrimitiveTypes;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilderForVoid;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialTypeBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialTypeBuilderForReferenceType;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialTypeBuilderForSchemaAnnotation;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import({OpenApiSpecSchemaExtensionAutoConfiguration.class})
public class OpenApiSpecSchemaAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SchemaResolver defaultSchemaResolver(
            OpenApiObjectMapperSupplier openApiObjectMapperSupplier,
            AnnotationsSupplierFactory annotationsSupplierFactory,
            List<InitialTypeBuilder> initialTypeBuilders,
            List<InitialSchemaBuilder> initialSchemaBuilders,
            List<SchemaCustomizer> schemaCustomizers,
            List<TypeResolver> typeResolvers) {
        return new DefaultSchemaResolver(
                openApiObjectMapperSupplier,
                annotationsSupplierFactory,
                initialTypeBuilders,
                initialSchemaBuilders,
                schemaCustomizers,
                typeResolvers);
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeResolverForCollectionLikeTypeSupport defaultTypeResolverForCollectionLikeTypeSupport(
            AnnotationsSupplierFactory annotationsSupplierFactory,
            ExtensionAnnotationMapper extensionAnnotationMapper,
            OpenApiObjectMapperSupplier openApiObjectMapperSupplier) {
        return new TypeResolverForCollectionLikeTypeSupport(
                annotationsSupplierFactory, extensionAnnotationMapper, openApiObjectMapperSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeResolverForCollectionLikeType defaultTypeResolverForCollectionLikeType(
            TypeResolverForCollectionLikeTypeSupport typeResolverForCollectionLikeTypeSupport) {
        return new TypeResolverForCollectionLikeType(typeResolverForCollectionLikeTypeSupport);
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeResolverForMapLikeType defaultTypeResolverForMapLikeType(SchemaNameBuilder schemaNameBuilder) {
        return new TypeResolverForMapLikeType(schemaNameBuilder);
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeResolverForObject defaultTypeResolverForObject(
            SchemaNameBuilder schemaNameBuilder,
            List<SchemaPropertiesResolver> schemaPropertiesResolvers,
            List<SchemaPropertiesCustomizer> schemaPropertiesCustomizers,
            OpenApiObjectMapperSupplier openApiObjectMapperSupplier) {
        return new TypeResolverForObject(
                schemaNameBuilder, schemaPropertiesResolvers, schemaPropertiesCustomizers, openApiObjectMapperSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public InitialTypeBuilderForReferenceType defaultInitialTypeBuilderForReferenceType(
            AnnotationsSupplierFactory annotationsSupplierFactory) {
        return new InitialTypeBuilderForReferenceType(annotationsSupplierFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public InitialTypeBuilderForSchemaAnnotation defaultInitialTypeBuilderForSchemaAnnotation(
            OpenApiObjectMapperSupplier openApiObjectMapperSupplier,
            AnnotationsSupplierFactory annotationsSupplierFactory) {
        return new InitialTypeBuilderForSchemaAnnotation(openApiObjectMapperSupplier, annotationsSupplierFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public InitialSchemaBuilderForPrimitiveTypes defaultInitialSchemaBuilderForPrimitiveTypes() {
        return new InitialSchemaBuilderForPrimitiveTypes();
    }

    @Bean
    @ConditionalOnMissingBean
    public InitialSchemaBuilderForAnyValue defaultInitialSchemaBuilderForAnyValue() {
        return new InitialSchemaBuilderForAnyValue();
    }

    @Bean
    @ConditionalOnMissingBean
    public InitialSchemaBuilderForVoid defaultInitialSchemaBuilderForVoid() {
        return new InitialSchemaBuilderForVoid();
    }

    @Bean
    @ConditionalOnMissingBean
    public InitialSchemaBuilderForEnum defaultInitialSchemaBuilderForEnum(SchemaNameBuilder schemaNameBuilder) {
        return new InitialSchemaBuilderForEnum(schemaNameBuilder);
    }

    @Bean
    @ConditionalOnMissingBean
    public SchemaCustomizerForSchemaAnnotation defaultSchemaCustomizerForSchemaAnnotation(
            ParsableValueMapper parsableValueMapper,
            ExternalDocumentationAnnotationMapper externalDocumentationAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new SchemaCustomizerForSchemaAnnotation(
                parsableValueMapper, externalDocumentationAnnotationMapper, extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SchemaCustomizerForNullable defaultSchemaCustomizerForNullable(NullnessDetector nullnessDetector) {
        return new SchemaCustomizerForNullable(nullnessDetector);
    }

    @Bean
    @ConditionalOnMissingBean
    public SchemaCustomizerForDeprecated defaultSchemaCustomizerForDeprecated(DeprecationDetector deprecationDetector) {
        return new SchemaCustomizerForDeprecated(deprecationDetector);
    }

    @Bean
    @ConditionalOnMissingBean
    public SchemaCustomizerForRequiredProperties defaultSchemaCustomizerForRequiredProperties() {
        return new SchemaCustomizerForRequiredProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public SchemaNameBuilder defaultSchemaNameBuilder() {
        return new DefaultSchemaNameBuilder();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultSchemaPropertiesResolver defaultSchemaPropertiesResolver(
            OpenApiObjectMapperSupplier objectMapperSupplier,
            List<SchemaPropertyFilter> schemaPropertyFilters,
            AnnotationsSupplierFactory annotationsSupplierFactory) {
        return new DefaultSchemaPropertiesResolver(
                objectMapperSupplier, schemaPropertyFilters, annotationsSupplierFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public SchemaPropertyFilterForIgnoredMembers defaultSchemaPropertyFilterForIgnoredMembers() {
        return new SchemaPropertyFilterForIgnoredMembers();
    }

    @Bean
    @ConditionalOnMissingBean
    public SchemaPropertyFilterForNamelessMembers defaultSchemaPropertyFilterForNamelessMembers() {
        return new SchemaPropertyFilterForNamelessMembers();
    }
}
