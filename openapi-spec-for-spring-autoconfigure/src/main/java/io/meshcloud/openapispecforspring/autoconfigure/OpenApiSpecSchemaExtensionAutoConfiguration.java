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

import com.fasterxml.jackson.databind.ObjectMapper;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaCustomizerForValidation;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.jackson.DefaultJacksonPolymorphismTypeSchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.jackson.JacksonPolymorphismTypeSchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.jackson.TypeResolverForJacksonPolymorphism;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.java8.DefaultJava8TimeInitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.java8.Java8TimeInitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.java8.Java8TimeTypeResolverConfigurationProperties;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import jakarta.validation.constraints.Min;
import java.util.Optional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties(Java8TimeTypeResolverConfigurationProperties.class)
public class OpenApiSpecSchemaExtensionAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public Java8TimeInitialSchemaBuilder defaultJava8TimeInitialSchemaBuilder(
            Java8TimeTypeResolverConfigurationProperties properties,
            // Use Spring Web's object mapper bean
            Optional<ObjectMapper> objectMapper) {
        return new DefaultJava8TimeInitialSchemaBuilder(properties, objectMapper.orElse(null));
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(Min.class)
    public SchemaCustomizerForValidation defaultSchemaCustomizerForValidation() {
        return new SchemaCustomizerForValidation();
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeResolverForJacksonPolymorphism defaultTypeResolverForJacksonPolymorphism(
            SchemaNameBuilder schemaNameBuilder,
            AnnotationsSupplierFactory annotationsSupplierFactory,
            OpenApiObjectMapperSupplier openApiObjectMapperSupplier,
            JacksonPolymorphismTypeSchemaNameBuilder jacksonPolymorphismTypeSchemaNameBuilder) {
        return new TypeResolverForJacksonPolymorphism(
                schemaNameBuilder,
                annotationsSupplierFactory,
                openApiObjectMapperSupplier,
                jacksonPolymorphismTypeSchemaNameBuilder);
    }

    @Bean
    @ConditionalOnMissingBean
    public JacksonPolymorphismTypeSchemaNameBuilder defaultJacksonPolymorphismTypeSchemaNameBuilder(
            SchemaNameBuilder schemaNameBuilder) {
        return new DefaultJacksonPolymorphismTypeSchemaNameBuilder(schemaNameBuilder);
    }
}
