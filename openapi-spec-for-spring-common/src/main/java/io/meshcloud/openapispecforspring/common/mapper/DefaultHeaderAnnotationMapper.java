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

import static io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver.Caller.HEADER;
import static io.meshcloud.openapispecforspring.common.util.OpenApiObjectUtils.setIfNotEmpty;

import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferencedSchemaConsumer;
import io.meshcloud.openapispecforspring.common.schema.mapper.SchemaAnnotationMapper;
import io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils;
import io.meshcloud.openapispecforspring.common.util.OpenApiStringUtils;
import io.meshcloud.openapispecforspring.model.header.Header;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import java.util.Arrays;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultHeaderAnnotationMapper implements HeaderAnnotationMapper {
    private final SchemaAnnotationMapper schemaAnnotationMapper;

    @Override
    public Map<String, Header> mapArray(
            io.swagger.v3.oas.annotations.headers.Header[] headerAnnotations, MapperContext mapperContext) {
        return OpenApiMapUtils.buildStringMapFromStream(
                Arrays.stream(headerAnnotations),
                io.swagger.v3.oas.annotations.headers.Header::name,
                headerAnnotation -> map(headerAnnotation, mapperContext));
    }

    @Override
    public Header map(
            io.swagger.v3.oas.annotations.headers.Header headerAnnotation,
            MapperContext referencedItemConsumerSupplier) {
        Header header = new Header();
        OpenApiStringUtils.setStringIfNotBlank(headerAnnotation.description(), header::setDescription);
        if (headerAnnotation.deprecated()) {
            header.setDeprecated(true);
        }
        if (headerAnnotation.required()) {
            header.setRequired(true);
        }
        ReferencedSchemaConsumer referencedSchemaConsumer =
                referencedItemConsumerSupplier.getReferencedItemConsumer(ReferencedSchemaConsumer.class);
        setIfNotEmpty(
                schemaAnnotationMapper.buildFromAnnotation(HEADER, headerAnnotation.schema(), referencedSchemaConsumer),
                schema -> {
                    // when schema is not empty, we specify simple style according to spec,
                    // the alternative would be to set the content property, which is not supported by @Header
                    // annotation
                    header.setStyle(ParameterStyle.SIMPLE.toString());
                    referencedSchemaConsumer.maybeAsReference(schema, header::setSchema);
                });

        // content is specified, but @Header annotation is missing support for it
        // example(s) are specified, but @Header annotation is missing support for it

        return header;
    }
}
