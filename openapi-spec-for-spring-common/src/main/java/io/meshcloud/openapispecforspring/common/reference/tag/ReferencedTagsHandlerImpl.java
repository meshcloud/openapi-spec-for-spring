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

package io.meshcloud.openapispecforspring.common.reference.tag;

import static io.meshcloud.openapispecforspring.common.util.OpenApiCollectionUtils.setCollectionIfNotEmpty;

import io.meshcloud.openapispecforspring.common.mapper.TagAnnotationMapper;
import io.meshcloud.openapispecforspring.common.reference.handler.ReferencedItemHandler;
import io.meshcloud.openapispecforspring.common.supplier.OpenAPIDefinitionAnnotationSupplier;
import io.meshcloud.openapispecforspring.model.OpenApi;
import io.meshcloud.openapispecforspring.model.tag.Tag;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ReferencedTagsHandlerImpl implements ReferencedTagsConsumer, ReferencedItemHandler {

    private final Map<String, Tag> tagsByName = new LinkedHashMap<>();

    public ReferencedTagsHandlerImpl(
            OpenAPIDefinitionAnnotationSupplier openAPIDefinitionAnnotationSupplier,
            TagAnnotationMapper tagAnnotationMapper) {
        openAPIDefinitionAnnotationSupplier
                .getAnnotations(OpenAPIDefinition::tags)
                .map(tagAnnotationMapper::map)
                .forEach(this::consumeTag);
    }

    @Override
    public void accept(List<Tag> tags) {
        tags.forEach(this::consumeTag);
    }

    @Override
    public void applyToOpenApi(OpenApi openApi) {
        setCollectionIfNotEmpty(new ArrayList<>(tagsByName.values()), openApi::setTags);
    }

    private void consumeTag(Tag tag) {
        if (StringUtils.isBlank(tag.getName())) {
            throw new IllegalStateException("Tag has blank name: " + tag);
        }
        tagsByName.merge(tag.getName(), tag, (a, b) -> {
            if (a.withName(null).isEmpty()) {
                return b;
            }
            if (b.withName(null).isEmpty() || a.equals(b)) {
                return a;
            }
            throw new IllegalStateException("Found conflicting tag with identical name: " + a + " vs. " + b);
        });
    }
}
