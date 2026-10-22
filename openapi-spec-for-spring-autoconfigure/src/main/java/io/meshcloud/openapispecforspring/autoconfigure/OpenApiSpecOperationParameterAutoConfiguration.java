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

import io.meshcloud.openapispecforspring.common.annotation.DeprecationDetector;
import io.meshcloud.openapispecforspring.common.annotation.NullnessDetector;
import io.meshcloud.openapispecforspring.common.filter.operation.parameter.OperationParameterPostFilter;
import io.meshcloud.openapispecforspring.common.filter.operation.parameter.OperationParameterPreFilter;
import io.meshcloud.openapispecforspring.common.mapper.ContentAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.DefaultParameterAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ExampleObjectAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ExtensionAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ParameterAnnotationMapper;
import io.meshcloud.openapispecforspring.common.operation.parameter.DefaultOperationParameterCustomizer;
import io.meshcloud.openapispecforspring.common.operation.parameter.converter.DefaultParameterMethodConverterFromParameterAnnotation;
import io.meshcloud.openapispecforspring.common.operation.parameter.converter.ParameterMethodConverter;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.DefaultOperationParameterAnnotationCustomizer;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.DefaultOperationParameterDeprecatedCustomizer;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.DefaultOperationParameterNameCustomizer;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.DefaultOperationParameterNullableCustomizer;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.DefaultOperationParameterPathPatternCustomizer;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.DefaultOperationParameterSchemaCustomizer;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.OperationParameterCustomizer;
import io.meshcloud.openapispecforspring.common.schema.mapper.SchemaAnnotationMapper;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class OpenApiSpecOperationParameterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationParameterCustomizer defaultOperationParameterCustomizer(
            List<OperationParameterPreFilter> operationParameterPreFilters,
            List<OperationParameterPostFilter> operationParameterPostFilters,
            List<ParameterMethodConverter> parameterMethodConverters,
            List<OperationParameterCustomizer> operationParameterCustomizers,
            ParameterAnnotationMapper parameterAnnotationMapper) {
        return new DefaultOperationParameterCustomizer(
                operationParameterPreFilters,
                operationParameterPostFilters,
                parameterMethodConverters,
                operationParameterCustomizers,
                parameterAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultParameterAnnotationMapper defaultParameterAnnotationMapper(
            SchemaAnnotationMapper schemaAnnotationMapper,
            ContentAnnotationMapper contentAnnotationMapper,
            ExampleObjectAnnotationMapper exampleObjectAnnotationMapper,
            ExtensionAnnotationMapper extensionAnnotationMapper) {
        return new DefaultParameterAnnotationMapper(
                schemaAnnotationMapper,
                contentAnnotationMapper,
                exampleObjectAnnotationMapper,
                extensionAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultParameterMethodConverterFromParameterAnnotation
            defaultParameterMethodConverterFromParameterAnnotation() {
        return new DefaultParameterMethodConverterFromParameterAnnotation();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationParameterAnnotationCustomizer defaultOperationParameterAnnotationCustomizer(
            ParameterAnnotationMapper parameterAnnotationMapper) {
        return new DefaultOperationParameterAnnotationCustomizer(parameterAnnotationMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationParameterDeprecatedCustomizer defaultOperationParameterDeprecatedCustomizer(
            DeprecationDetector deprecationDetector) {
        return new DefaultOperationParameterDeprecatedCustomizer(deprecationDetector);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationParameterNameCustomizer defaultOperationParameterNameCustomizer() {
        return new DefaultOperationParameterNameCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationParameterNullableCustomizer defaultOperationParameterNullableCustomizer(
            NullnessDetector nullnessDetector) {
        return new DefaultOperationParameterNullableCustomizer(nullnessDetector);
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationParameterPathPatternCustomizer defaultOperationParameterPathPatternCustomizer() {
        return new DefaultOperationParameterPathPatternCustomizer();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOperationParameterSchemaCustomizer defaultOperationParameterSchemaCustomizer(
            SchemaResolver schemaResolver) {
        return new DefaultOperationParameterSchemaCustomizer(schemaResolver);
    }
}
