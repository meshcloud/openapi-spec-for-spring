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

package io.meshcloud.openapispecforspring.common.schema.customizer;

import static io.meshcloud.openapispecforspring.common.util.OpenApiCollectionUtils.setCollectionIfNotEmpty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.mergeWithExistingMap;
import static io.meshcloud.openapispecforspring.common.util.OpenApiObjectUtils.setIfNotEmpty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiStringUtils.setStringIfNotBlank;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.mapper.ExtensionAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ExternalDocumentationAnnotationMapper;
import io.meshcloud.openapispecforspring.common.mapper.ParsableValueMapper;
import io.meshcloud.openapispecforspring.model.media.Discriminator;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class SchemaCustomizerForSchemaAnnotation implements SchemaCustomizer {

    private final ParsableValueMapper parsableValueMapper;
    private final ExternalDocumentationAnnotationMapper externalDocumentationAnnotationMapper;
    private final ExtensionAnnotationMapper extensionAnnotationMapper;

    @Override
    public void customize(
            Schema schema,
            JavaType javaType,
            AnnotationsSupplier annotationsSupplier,
            RecursiveResolver recursiveResolver) {
        annotationsSupplier
                .findAnnotations(io.swagger.v3.oas.annotations.media.Schema.class)
                // apply in reverse order
                .collect(Collectors.toCollection(ArrayDeque::new))
                .descendingIterator()
                .forEachRemaining(schemaAnnotation -> applyFromAnnotation(schema, schemaAnnotation, recursiveResolver));
    }

    public void applyFromAnnotation(
            Schema schema, io.swagger.v3.oas.annotations.media.Schema annotation, RecursiveResolver recursiveResolver) {

        setStringIfNotBlank(annotation.name(), schema::setName);
        setStringIfNotBlank(annotation.title(), schema::setTitle);
        setStringIfNotBlank(annotation.description(), schema::setDescription);
        setStringIfNotBlank(annotation.format(), schema::setFormat);
        setStringIfNotBlank(annotation.pattern(), schema::setPattern);
        setStringIfNotBlank(annotation.ref(), schema::setRef);

        if (annotation.nullable()) {
            schema.setNullable(true);
        }
        setCollectionIfNotEmpty(Arrays.asList(annotation.requiredProperties()), schema::setRequired);
        setAccessMode(annotation.accessMode(), schema);
        setStringIfNotBlank(annotation.type(), schema::setType);
        setStringIfNotBlank(annotation.example(), example -> schema.setExample(parseExample(schema, example)));
        setIfNotEmpty(externalDocumentationAnnotationMapper.map(annotation.externalDocs()), schema::setExternalDocs);
        if (annotation.deprecated()) {
            schema.setDeprecated(true);
        }
        setStringIfNotBlank(annotation.minimum(), minimum -> schema.setMinimum(new BigDecimal(minimum)));
        if (annotation.exclusiveMinimum()) {
            schema.setExclusiveMinimum(true);
        }
        setStringIfNotBlank(annotation.maximum(), maximum -> schema.setMaximum(new BigDecimal(maximum)));
        if (annotation.exclusiveMaximum()) {
            schema.setExclusiveMaximum(true);
        }

        List<Object> allowableValues = Stream.of(annotation.allowableValues())
                .map(parsableValueMapper::parse)
                .toList();
        setCollectionIfNotEmpty(allowableValues, schema::setEnumValues);

        setStringIfNotBlank(
                annotation.defaultValue(), value -> schema.setDefaultValue(parsableValueMapper.parse(value)));

        setDiscriminator(schema, annotation, recursiveResolver);
        setAdditionalProperties(annotation.additionalProperties(), schema);

        mergeWithExistingMap(
                schema::getExtensions,
                schema::setExtensions,
                extensionAnnotationMapper.mapArray(annotation.extensions()));
    }

    private Object parseExample(Schema schema, String example) {
        return "string".equals(schema.getType()) ? example : parsableValueMapper.parse(example);
    }

    private void setDiscriminator(
            Schema schema, io.swagger.v3.oas.annotations.media.Schema annotation, RecursiveResolver recursiveResolver) {
        String propertyName = annotation.discriminatorProperty();
        DiscriminatorMapping[] mappings = annotation.discriminatorMapping();
        if (StringUtils.isBlank(propertyName) || ArrayUtils.isEmpty(mappings)) {
            return;
        }

        Map<String, String> schemaReferenceMapping = new LinkedHashMap<>();
        schema.setDiscriminator(Discriminator.builder()
                .propertyName(propertyName)
                .mapping(schemaReferenceMapping)
                .build());

        for (DiscriminatorMapping mapping : mappings) {
            recursiveResolver.alwaysAsReference(
                    mapping.schema(),
                    schemaReference -> schemaReferenceMapping.put(mapping.value(), schemaReference.getRef()));
        }
    }

    private void setAdditionalProperties(
            io.swagger.v3.oas.annotations.media.Schema.AdditionalPropertiesValue value, Schema schema) {
        switch (value) {
            case TRUE -> schema.setAdditionalProperties(true);
            case FALSE -> schema.setAdditionalProperties(false);
            case USE_ADDITIONAL_PROPERTIES_ANNOTATION -> {
                return;
            }
        }
        if (schema.getType() == null) {
            schema.setType("object");
        }
    }

    private void setAccessMode(io.swagger.v3.oas.annotations.media.Schema.AccessMode accessMode, Schema schema) {
        switch (accessMode) {
            case READ_ONLY -> schema.setReadOnly(true);
            case WRITE_ONLY -> schema.setWriteOnly(true);
            case READ_WRITE, AUTO -> {}
        }
    }
}
