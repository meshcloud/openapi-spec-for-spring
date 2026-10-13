/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
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

package io.meshcloud.openapispecforspring.common.mapper;

import static io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver.Caller.PARAMETER;
import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.setMapIfNotEmpty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiObjectUtils.setIf;
import static io.meshcloud.openapispecforspring.common.util.OpenApiObjectUtils.setIfNotEmpty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiStringUtils.setStringIfNotBlank;

import io.meshcloud.openapispecforspring.common.reference.component.example.ReferencedExamplesConsumer;
import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferencedSchemaConsumer;
import io.meshcloud.openapispecforspring.common.schema.mapper.SchemaAnnotationMapper;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class DefaultParameterAnnotationMapper implements ParameterAnnotationMapper {

    private final SchemaAnnotationMapper schemaAnnotationMapper;
    private final ContentAnnotationMapper contentAnnotationMapper;
    private final ExampleObjectAnnotationMapper exampleObjectAnnotationMapper;
    private final ExtensionAnnotationMapper extensionAnnotationMapper;

    @Override
    public Parameter buildFromAnnotation(
            io.swagger.v3.oas.annotations.Parameter parameterAnnotation, MapperContext mapperContext) {
        if (StringUtils.isBlank(parameterAnnotation.name())) {
            throw new IllegalStateException("Blank parameter name in parameter annotation");
        }
        Parameter parameter = new Parameter();
        parameter.setName(parameterAnnotation.name());
        applyFromAnnotation(parameter, parameterAnnotation, mapperContext);
        return parameter;
    }

    @Override
    public void applyFromAnnotation(
            Parameter parameter, io.swagger.v3.oas.annotations.Parameter annotation, MapperContext mapperContext) {
        setStringIfNotBlank(annotation.name(), parameter::setName);
        setStringIfNotBlank(annotation.in().toString(), parameter::setIn);
        setStringIfNotBlank(annotation.description(), parameter::setDescription);
        if (annotation.required()) {
            parameter.setRequired(true);
        }
        if (annotation.deprecated()) {
            parameter.setDeprecated(true);
        }
        if (annotation.allowEmptyValue()) {
            parameter.setAllowEmptyValue(true);
        }
        setIf(
                annotation.style(),
                style -> style != ParameterStyle.DEFAULT,
                style -> parameter.setStyle(style.toString()));
        setIf(
                annotation.explode(),
                explode -> explode != Explode.DEFAULT,
                explode -> parameter.setExplode(explode == Explode.TRUE));
        if (annotation.allowReserved()) {
            parameter.setAllowReserved(true);
        }
        ReferencedSchemaConsumer referencedSchemaConsumer =
                mapperContext.getReferencedItemConsumer(ReferencedSchemaConsumer.class);
        setIfNotEmpty(
                schemaAnnotationMapper.buildFromAnnotation(PARAMETER, annotation.schema(), referencedSchemaConsumer),
                schema -> referencedSchemaConsumer.maybeAsReference(schema, parameter::setSchema));
        setMapIfNotEmpty(
                contentAnnotationMapper.mapArray(annotation.content(), Parameter.class, PARAMETER, mapperContext),
                parameter::setContent);
        ReferencedExamplesConsumer referencedExamplesConsumer =
                mapperContext.getReferencedItemConsumer(ReferencedExamplesConsumer.class);
        setMapIfNotEmpty(
                exampleObjectAnnotationMapper.mapArray(annotation.examples()),
                examples -> referencedExamplesConsumer.maybeAsReference(examples, parameter::setExamples));
        setStringIfNotBlank(annotation.example(), parameter::setExample);
        setMapIfNotEmpty(extensionAnnotationMapper.mapArray(annotation.extensions()), parameter::setExtensions);
        setStringIfNotBlank(annotation.ref(), parameter::setRef);
    }
}
