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
import io.meshcloud.openapispecforspring.common.mapper.CallbackAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ContactAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ContentAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultCallbackAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultContactAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultContentAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultEncodingAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultExampleObjectAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultExtensionAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultExternalDocumentationAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultHeaderAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultInfoAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultLicenseAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultLinkAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultLinkParameterAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultOAuthFlowAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultOAuthFlowsAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultOperationAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultParsableValueMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultRequestBodyAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultSecurityRequirementAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultSecuritySchemeAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultServerAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultServerVariableAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultTagAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.EncodingAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ExampleObjectAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ExtensionAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ExternalDocumentationAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.HeaderAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.InfoAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.LicenseAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.LinkAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.LinkParameterAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.OAuthFlowAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.OAuthFlowsAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.OperationAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ParameterAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ParsableValueMapper;
import io.meshcloud.openapispecforspring.common.mapper.RequestBodyAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.SecurityRequirementAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.SecuritySchemeAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ServerAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ServerVariableAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.TagAnnotationMapper;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaCustomizerForSchemaAnnotation;
import io.meshcloud.openapispecforspring.common.schema.mapper.DefaultSchemaAnnotationMapper;
import io.meshcloud.openapispecforspring.common.schema.mapper.SchemaAnnotationMapper;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class OpenApiSpecMapperAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SchemaAnnotationMapper defaultSchemaAnnotationMapper(
            SchemaCustomizerForSchemaAnnotation schemaCustomizerForSchemaAnnotation, SchemaResolver schemaResolver) {
        return new DefaultSchemaAnnotationMapper(schemaCustomizerForSchemaAnnotation, schemaResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public ParsableValueMapper defaultParsableValueMapper(OpenApiObjectMapperSupplier objectMapperSupplier) {
        return new DefaultParsableValueMapper(objectMapperSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExtensionAnnotationMapper defaultExtensionAnnotationMapper(ParsableValueMapper parsableValueMapper) {
        return new DefaultExtensionAnnotationMapper(parsableValueMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExternalDocumentationAnnotationMapper defaultExternalDocumentationAnnotationMapper(
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultExternalDocumentationAnnotationMapper(extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public OperationAnnotationMapper defaultOperationAnnotationMapper(
            RequestBodyAnnotationMapper requestBodyAnnotationMapper,
            ExternalDocumentationAnnotationMapper externalDocumentationAnnotationMapper,
            ParameterAnnotationMapper parameterAnnotationMapper,
            ApiResponseAnnotationMapper apiResponseAnnotationMapper,
            SecurityRequirementAnnotationMapper securityRequirementAnnotationMapper,
            ServerAnnotationMapper serverAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultOperationAnnotationMapper(
                requestBodyAnnotationMapper,
                externalDocumentationAnnotationMapper,
                parameterAnnotationMapper,
                apiResponseAnnotationMapper,
                securityRequirementAnnotationMapper,
                serverAnnotationMapper,
                extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public HeaderAnnotationMapper defaultHeaderAnnotationMapper(SchemaAnnotationMapper schemaAnnotationMapper) {
        return new DefaultHeaderAnnotationMapper(schemaAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public LinkAnnotationMapper defaultLinkAnnotationMapper(
            ParsableValueMapper parsableValueMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper,
            LinkParameterAnnotationMapper linkParameterAnnotationMapper,
            ServerAnnotationMapper serverAnnotationMapper) {
        return new DefaultLinkAnnotationMapper(
                parsableValueMapper, extensionAnnotationMapper, linkParameterAnnotationMapper, serverAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public LinkParameterAnnotationMapper defaultLinkParameterAnnotationMapper() {
        return new DefaultLinkParameterAnnotationMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerAnnotationMapper defaultServerAnnotationMapper(
            ServerVariableAnnotationMapper serverVariableAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultServerAnnotationMapper(serverVariableAnnotationMapper, extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ServerVariableAnnotationMapper defaultServerVariableAnnotationMapper(
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultServerVariableAnnotationMapper(extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ContentAnnotationMapper defaultContentAnnotationMapper(
            EncodingAnnotationMapper encodingAnnotationMapper,
            SchemaAnnotationMapper schemaAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper,
            ExampleObjectAnnotationMapper exampleObjectAnnotationMapper) {
        return new DefaultContentAnnotationMapper(
                encodingAnnotationMapper, schemaAnnotationMapper,
                extensionAnnotationMapper, exampleObjectAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ExampleObjectAnnotationMapper defaultExampleObjectAnnotationMapper(
            ParsableValueMapper parsableValueMapper, ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultExampleObjectAnnotationMapper(parsableValueMapper, extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public EncodingAnnotationMapper defaultEncodingAnnotationMapper(
            HeaderAnnotationMapper headerAnnotationMapper, ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultEncodingAnnotationMapper(headerAnnotationMapper, extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public InfoAnnotationMapper defaultInfoAnnotationMapper(
            ContactAnnotationMapper contactAnnotationMapper,
            LicenseAnnotationMapper licenseAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultInfoAnnotationMapper(
                contactAnnotationMapper, licenseAnnotationMapper, extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public ContactAnnotationMapper defaultContactAnnotationMapper(ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultContactAnnotationMapper(extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public LicenseAnnotationMapper defaultLicenseAnnotationMapper(ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultLicenseAnnotationMapper(extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestBodyAnnotationMapper defaultRequestBodyAnnotationMapper(
            ContentAnnotationMapper contentAnnotationMapper, ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultRequestBodyAnnotationMapper(contentAnnotationMapper, extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public TagAnnotationMapper defaultTagAnnotationMapper(
            ExternalDocumentationAnnotationMapper externalDocumentationAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultTagAnnotationMapper(externalDocumentationAnnotationMapper, extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public CallbackAnnotationMapper defaultCallbackAnnotationMapper(
            OperationAnnotationMapper operationAnnotationMapper) {
        return new DefaultCallbackAnnotationMapper(operationAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecuritySchemeAnnotationMapper defaultSecurityAnnotationMapper(
            OAuthFlowsAnnotationMapper oAuthFlowsAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultSecuritySchemeAnnotationMapper(oAuthFlowsAnnotationMapper, extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuthFlowsAnnotationMapper defaultOAuthFlowsAnnotationMapper(
            OAuthFlowAnnotationMapper oAuthFlowAnnotationMapper, ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultOAuthFlowsAnnotationMapper(oAuthFlowAnnotationMapper, extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuthFlowAnnotationMapper defaultOAuthFlowAnnotationMapper(
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultOAuthFlowAnnotationMapper(extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityRequirementAnnotationMapper defaultSecurityRequirementAnnotationMapper() {
        return new DefaultSecurityRequirementAnnotationMapper();
    }
}
