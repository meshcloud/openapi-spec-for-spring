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

package io.meshcloud.openapispecforspring.common.operation.customizer;

import static io.meshcloud.openapispecforspring.common.util.OpenApiCollectionUtils.setCollectionIfNotEmpty;

import io.meshcloud.openapispecforspring.common.mapper.TagAnnotationMapper;
import io.meshcloud.openapispecforspring.common.operation.OperationBuilderContext;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.reference.tag.ReferencedTagsConsumer;
import io.meshcloud.openapispecforspring.model.operation.Operation;
import io.meshcloud.openapispecforspring.model.tag.Tag;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class DefaultOperationTagsCustomizer implements OperationCustomizer {

    public static final int ORDER = DEFAULT_ORDER;

    private final TagAnnotationMapper tagAnnotationMapper;

    @Override
    public void customize(Operation operation, OperationBuilderContext operationBuilderContext) {
        setTagNamesToOperation(
                operation,
                Stream.concat(
                        operation.getTags() == null ? Stream.empty() : operation.getTags().stream(),
                        collectTagsFromMethodAndClass(operationBuilderContext)));
    }

    private static void setTagNamesToOperation(Operation operation, Stream<String> tagNames) {
        List<String> distinctNonBlankTags =
                tagNames.filter(StringUtils::isNotBlank).distinct().toList();
        setCollectionIfNotEmpty(distinctNonBlankTags, operation::setTags);
    }

    private Stream<String> collectTagsFromMethodAndClass(OperationBuilderContext operationBuilderContext) {
        HandlerMethod handlerMethod = operationBuilderContext.getOperationInfo().getHandlerMethod();
        List<Tag> tags = handlerMethod
                .findAnnotations(io.swagger.v3.oas.annotations.tags.Tag.class)
                .map(tagAnnotationMapper::map)
                .toList();
        setCollectionIfNotEmpty(tags, nonEmptyTags -> operationBuilderContext
                .getReferencedItemConsumer(ReferencedTagsConsumer.class)
                .accept(nonEmptyTags));
        return tags.stream().map(Tag::getName);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
