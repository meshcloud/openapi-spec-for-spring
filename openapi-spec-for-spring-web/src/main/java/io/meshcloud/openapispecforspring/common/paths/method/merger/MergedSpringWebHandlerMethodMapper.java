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

import io.meshcloud.openapispecforspring.common.mapper.MapperContext;
import io.meshcloud.openapispecforspring.common.mapper.MediaTypesProvider;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.model.requestbody.RequestBody;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MergedSpringWebHandlerMethodMapper {

    public static class ContextModifierMapper implements HandlerMethod.ContextModifierMapper<MapperContext> {
        @Override
        public HandlerMethod.@Nullable ContextModifier<MapperContext> map(HandlerMethod.@Nullable Context context) {
            if (context instanceof MergedSpringWebHandlerMethodContext mergedHandlerMethodContext) {
                MediaTypesProvider mediaTypesProvider = owningType -> {
                    if (RequestBody.class.equals(owningType)) {
                        return mergedHandlerMethodContext.getConsumesContentTypes();
                    }
                    throw new IllegalStateException(
                            "Cannot provide media types for " + owningType.getSimpleName() + " in merged method");
                };
                return mapperContext -> mapperContext.withMediaTypesProvider(mediaTypesProvider);
            }
            return null; // indicates we can't map this handler method context
        }
    }

    @RequiredArgsConstructor
    public static class RequestBodyMapper implements HandlerMethod.RequestBodyMapper {

        private final SpringWebHandlerMethodRequestBodyMerger requestBodyMerger;

        @Nullable
        @Override
        public List<HandlerMethod.RequestBody> map(HandlerMethod handlerMethod) {
            if (handlerMethod instanceof MergedSpringWebHandlerMethod mergedSpringWebHandlerMethod) {
                return requestBodyMerger.mergeRequestBodies(mergedSpringWebHandlerMethod.getHandlerMethods());
            }
            return null; // indicates we can't map this handler method instance
        }
    }

    @RequiredArgsConstructor
    public static class ResponseMapper implements HandlerMethod.ResponseMapper {

        private final SpringWebHandlerMethodResponseMerger responseMerger;

        @Nullable
        @Override
        public List<HandlerMethod.Response> map(HandlerMethod handlerMethod) {
            if (handlerMethod instanceof MergedSpringWebHandlerMethod mergedSpringWebHandlerMethod) {
                return responseMerger.mergeResponses(mergedSpringWebHandlerMethod.getHandlerMethods());
            }
            return null; // indicates we can't map this handler method instance
        }
    }
}
