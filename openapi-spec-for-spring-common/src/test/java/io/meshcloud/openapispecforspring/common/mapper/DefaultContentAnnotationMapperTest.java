/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
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

package io.meshcloud.openapispecforspring.common.mapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferencedSchemaConsumer;
import io.meshcloud.openapispecforspring.common.schema.mapper.SchemaAnnotationMapper;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.meshcloud.openapispecforspring.model.response.ApiResponse;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import org.junit.jupiter.api.Test;

class DefaultContentAnnotationMapperTest {

    @Test
    void failsOnSchemaAndArray() throws NoSuchMethodException {
        SchemaAnnotationMapper schemaAnnotationMapper = mock(SchemaAnnotationMapper.class);
        when(schemaAnnotationMapper.buildFromAnnotation(any(), any(), any()))
                .thenAnswer(invocation -> Schema.builder().type("string").build());
        ReferencedSchemaConsumer referencedSchemaConsumer = mock(ReferencedSchemaConsumer.class);
        MapperContext mapperContext = mock(MapperContext.class);
        when(mapperContext.getReferencedItemConsumer(ReferencedSchemaConsumer.class))
                .thenReturn(referencedSchemaConsumer);
        DefaultContentAnnotationMapper mapper = new DefaultContentAnnotationMapper(
                mock(EncodingAnnotationMapper.class),
                schemaAnnotationMapper,
                mock(ExtensionAnnotationMapper.class),
                mock(ExampleObjectAnnotationMapper.class));
        Content[] contentAnnotations = Handler.class
                .getDeclaredMethod("schemaAndArray")
                .getAnnotation(io.swagger.v3.oas.annotations.responses.ApiResponse.class)
                .content();

        assertThatThrownBy(() -> mapper.mapArray(
                        contentAnnotations, ApiResponse.class, SchemaResolver.Caller.API_RESPONSE, mapperContext))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("The @Content annotation with mediaType \"application/json\" sets both schema and array,"
                        + " but only one of them is allowed");
    }

    interface Handler {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                content =
                        @Content(
                                mediaType = "application/json",
                                schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = String.class),
                                array =
                                        @ArraySchema(
                                                schema =
                                                        @io.swagger.v3.oas.annotations.media.Schema(
                                                                implementation = String.class))))
        void schemaAndArray();
    }
}
