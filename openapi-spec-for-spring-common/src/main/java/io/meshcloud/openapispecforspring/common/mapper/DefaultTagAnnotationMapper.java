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
import static io.meshcloud.openapispecforspring.common.util.OpenApiStringUtils.setStringIfNotBlank;

import io.meshcloud.openapispecforspring.model.tag.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class DefaultTagAnnotationMapper implements TagAnnotationMapper {
    private final ExternalDocumentationAnnotationMapper externalDocumentationAnnotationMapper;
    private final ExtensionAnnotationMapper extensionAnnotationMapper;

    @Override
    public Tag map(io.swagger.v3.oas.annotations.tags.Tag tagAnnotation) {
        Tag tag = new Tag();
        if (StringUtils.isBlank(tagAnnotation.name())) {
            throw new IllegalStateException("Tag annotation must not have blank tag name");
        }
        tag.setName(tagAnnotation.name());
        setStringIfNotBlank(tagAnnotation.description(), tag::setDescription);
        setIfNotEmpty(externalDocumentationAnnotationMapper.map(tagAnnotation.externalDocs()), tag::setExternalDocs);
        setMapIfNotEmpty(extensionAnnotationMapper.mapArray(tagAnnotation.extensions()), tag::setExtensions);
        return tag;
    }
}
