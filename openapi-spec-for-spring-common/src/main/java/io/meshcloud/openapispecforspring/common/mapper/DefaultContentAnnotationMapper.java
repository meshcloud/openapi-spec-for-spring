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

import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.setMapIfNotEmpty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiObjectUtils.setIfNotEmpty;

import io.meshcloud.openapispecforspring.common.reference.component.example.ReferencedExamplesConsumer;
import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferencedSchemaConsumer;
import io.meshcloud.openapispecforspring.common.schema.mapper.ArraySchemaAnnotationFields;
import io.meshcloud.openapispecforspring.common.schema.mapper.SchemaAnnotationMapper;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.model.media.Content;
import io.meshcloud.openapispecforspring.model.media.MediaType;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.meshcloud.openapispecforspring.model.trait.HasContent;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

@RequiredArgsConstructor
public class DefaultContentAnnotationMapper implements ContentAnnotationMapper {

    private final EncodingAnnotationMapper encodingAnnotationMapper;
    private final SchemaAnnotationMapper schemaAnnotationMapper;
    private final ExtensionAnnotationMapper extensionAnnotationMapper;
    private final ExampleObjectAnnotationMapper exampleObjectAnnotationMapper;

    @Override
    public Content mapArray(
            io.swagger.v3.oas.annotations.media.Content[] contentAnnotations,
            Class<? extends HasContent> owningType,
            SchemaResolver.Caller caller,
            MapperContext mapperContext) {
        return Arrays.stream(contentAnnotations)
                .flatMap(contentAnnotation -> {
                    MediaType mediaTypeValue = map(caller, contentAnnotation, mapperContext);
                    if (StringUtils.isBlank(contentAnnotation.mediaType())) {
                        // if the mapperContext doesn't have any suggested media types,
                        // the mediaTypeValue is discarded!
                        Set<String> mediaTypes = mapperContext
                                .findMediaTypes(owningType)
                                .orElseThrow(() -> new IllegalStateException("No media types available in context for "
                                        + owningType.getSimpleName() + " and Content annotation has blank mediaType"));
                        return mediaTypes.stream().map(mediaType -> Pair.of(mediaType, mediaTypeValue));
                    }
                    return Stream.of(Pair.of(contentAnnotation.mediaType(), mediaTypeValue));
                })
                .collect(Collectors.toMap(
                        Pair::getKey,
                        Pair::getValue,
                        (a, b) -> {
                            if (!Objects.equals(a, b)) {
                                throw new IllegalStateException("Conflicting media type found for " + a + " vs. " + b);
                            }
                            return a;
                        },
                        Content::new));
    }

    private MediaType map(
            SchemaResolver.Caller caller,
            io.swagger.v3.oas.annotations.media.Content contentAnnotation,
            MapperContext mapperContext) {
        MediaType mediaType = new MediaType();
        setExampleOrExamples(mediaType, contentAnnotation.examples(), mapperContext);
        setMapIfNotEmpty(
                encodingAnnotationMapper.mapArray(contentAnnotation.encoding(), mapperContext), mediaType::setEncoding);
        ReferencedSchemaConsumer referencedSchemaConsumer =
                mapperContext.getReferencedItemConsumer(ReferencedSchemaConsumer.class);
        setIfNotEmpty(
                buildSchema(caller, contentAnnotation, referencedSchemaConsumer),
                schema -> referencedSchemaConsumer.maybeAsReference(schema, mediaType::setSchema));
        setMapIfNotEmpty(extensionAnnotationMapper.mapArray(contentAnnotation.extensions()), mediaType::setExtensions);
        return mediaType;
    }

    private Schema buildSchema(
            SchemaResolver.Caller caller,
            io.swagger.v3.oas.annotations.media.Content contentAnnotation,
            ReferencedSchemaConsumer referencedSchemaConsumer) {
        Schema schema = schemaAnnotationMapper.buildFromAnnotation(
                caller, contentAnnotation.schema(), referencedSchemaConsumer);
        Schema arraySchema = buildArraySchema(caller, contentAnnotation.array(), referencedSchemaConsumer);
        if (arraySchema.isEmpty()) {
            return schema;
        }
        if (!schema.isEmpty()) {
            throw new IllegalStateException("The @Content annotation with mediaType \"" + contentAnnotation.mediaType()
                    + "\" sets both schema and array, but only one of them is allowed");
        }
        return arraySchema;
    }

    private Schema buildArraySchema(
            SchemaResolver.Caller caller,
            ArraySchema arraySchemaAnnotation,
            ReferencedSchemaConsumer referencedSchemaConsumer) {
        Schema arraySchema = schemaAnnotationMapper.buildFromAnnotation(
                caller, arraySchemaAnnotation.arraySchema(), referencedSchemaConsumer);
        ArraySchemaAnnotationFields.apply(arraySchema, arraySchemaAnnotation, extensionAnnotationMapper);
        Schema itemsSchema = schemaAnnotationMapper.buildFromAnnotation(
                caller, arraySchemaAnnotation.schema(), referencedSchemaConsumer);
        if (arraySchema.isEmpty() && itemsSchema.isEmpty()) {
            return arraySchema;
        }
        arraySchema.setType("array");
        setIfNotEmpty(itemsSchema, items -> referencedSchemaConsumer.maybeAsReference(items, arraySchema::setItems));
        return arraySchema;
    }

    private void setExampleOrExamples(
            MediaType mediaType, ExampleObject[] exampleObjectAnnotations, MapperContext mapperContext) {
        if (exampleObjectAnnotations.length == 1 && StringUtils.isBlank(exampleObjectAnnotations[0].name())) {
            setIfNotEmpty(
                    exampleObjectAnnotationMapper.map(exampleObjectAnnotations[0]),
                    // one should not set the full example object here, just the value
                    // so no referencing is needed in this case
                    example -> mediaType.setExample(example.getValue()));
        } else {
            ReferencedExamplesConsumer referencedExamplesConsumer =
                    mapperContext.getReferencedItemConsumer(ReferencedExamplesConsumer.class);
            setMapIfNotEmpty(
                    exampleObjectAnnotationMapper.mapArray(exampleObjectAnnotations),
                    examples -> referencedExamplesConsumer.maybeAsReference(examples, mediaType::setExamples));
        }
    }
}
