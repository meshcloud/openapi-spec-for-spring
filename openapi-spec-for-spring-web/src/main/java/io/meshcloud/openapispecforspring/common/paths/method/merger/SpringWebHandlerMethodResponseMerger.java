/*-
 * #%L
 * OpenAPI Spec for Spring :: Web
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

package io.meshcloud.openapispecforspring.common.paths.method.merger;

import static io.meshcloud.openapispecforspring.common.util.OpenApiStreamUtils.groupingByPairKeyAndCollectingValuesTo;
import static io.meshcloud.openapispecforspring.common.util.OpenApiStreamUtils.groupingByPairKeyAndCollectingValuesToList;

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.method.AbstractSpringWebHandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodContentTypesMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodResponseCodeMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodReturnTypeMapper;
import io.meshcloud.openapispecforspring.model.media.Content;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

@RequiredArgsConstructor
public class SpringWebHandlerMethodResponseMerger {

    private final SpringWebHandlerMethodTypeMerger typeMerger;
    private final SpringWebHandlerMethodContentTypesMapper contentTypesMapper;
    private final SpringWebHandlerMethodResponseCodeMapper responseCodeMapper;
    private final SpringWebHandlerMethodReturnTypeMapper returnTypeMapper;

    public List<HandlerMethod.Response> mergeResponses(List<SpringWebHandlerMethod> handlerMethods) {
        List<HandlerMethod.Response> mergedResponses = new ArrayList<>();
        Map<String, Map<Set<String>, List<SpringWebHandlerMethod>>> groupedHandlerMethods = handlerMethods.stream()
                .map(method -> Pair.of(
                        responseCodeMapper.getResponseCode(method),
                        Pair.of(contentTypesMapper.findProducesContentTypes(method), method)))
                .collect(groupingByPairKeyAndCollectingValuesTo(groupingByPairKeyAndCollectingValuesToList()));

        groupedHandlerMethods.forEach((responseCode, typesGroupedByProduces) ->
                typesGroupedByProduces.forEach((producesContentTypes, methods) -> mergedResponses.add(
                        new AbstractSpringWebHandlerMethod.SpringWebResponse(
                                responseCode, producesContentTypes, mergeReturnTypes(methods)) {
                            @Override
                            public boolean shouldClearContent(Content content) {
                                return groupedHandlerMethods.get(responseCode).size() == 1;
                            }
                        })));
        return mergedResponses;
    }

    private Optional<HandlerMethod.Type> mergeReturnTypes(List<SpringWebHandlerMethod> handlerMethods) {
        return handlerMethods.stream().map(returnTypeMapper::getReturnType).reduce(typeMerger::mergeType);
    }
}
