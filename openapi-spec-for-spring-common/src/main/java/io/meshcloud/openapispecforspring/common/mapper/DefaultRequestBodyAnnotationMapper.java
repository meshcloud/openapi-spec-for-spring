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

import static io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver.Caller.REQUEST_BODY;
import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.setMapIfNotEmpty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiStringUtils.setStringIfNotBlank;

import io.meshcloud.openapispecforspring.model.requestbody.RequestBody;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultRequestBodyAnnotationMapper implements RequestBodyAnnotationMapper {
    private final ContentAnnotationMapper contentAnnotationMapper;
    private final ExtensionAnnotationMapper extensionAnnotationMapper;

    @Override
    public RequestBody buildFromAnnotation(
            io.swagger.v3.oas.annotations.parameters.RequestBody requestBodyAnnotation, MapperContext mapperContext) {
        RequestBody requestBody = new RequestBody();
        applyFromAnnotation(requestBody, requestBodyAnnotation, mapperContext);
        return requestBody;
    }

    @Override
    public void applyFromAnnotation(
            RequestBody requestBody,
            io.swagger.v3.oas.annotations.parameters.RequestBody annotation,
            MapperContext mapperContext) {
        setStringIfNotBlank(annotation.description(), requestBody::setDescription);
        setMapIfNotEmpty(
                contentAnnotationMapper.mapArray(annotation.content(), RequestBody.class, REQUEST_BODY, mapperContext),
                requestBody::setContent);
        if (annotation.required()) {
            requestBody.setRequired(true);
        }
        setMapIfNotEmpty(extensionAnnotationMapper.mapArray(annotation.extensions()), requestBody::setExtensions);
    }
}
