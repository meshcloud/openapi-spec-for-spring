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

package io.meshcloud.openapispecforspring.common.paths.method;

import static org.springframework.http.MediaType.ALL_VALUE;

import io.meshcloud.openapispecforspring.common.mapper.MapperContext;
import io.meshcloud.openapispecforspring.common.mapper.MediaTypesProvider;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethodRequestBodyParameterMapper.RequestBodyParameter;
import io.meshcloud.openapispecforspring.model.requestbody.RequestBody;
import io.meshcloud.openapispecforspring.model.response.ApiResponse;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SpringWebHandlerMethodMapper {

    @RequiredArgsConstructor
    public static class ContextModifierMapper implements HandlerMethod.ContextModifierMapper<MapperContext> {

        private final SpringWebHandlerMethodContentTypesMapper contentTypesMapper;

        @Override
        public HandlerMethod.@Nullable ContextModifier<MapperContext> map(HandlerMethod.@Nullable Context context) {
            if (context instanceof SpringWebHandlerMethod springWebHandlerMethod) {
                MediaTypesProvider mediaTypesProvider = owningType -> {
                    if (RequestBody.class.equals(owningType)) {
                        Set<String> consumesContentTypes =
                                contentTypesMapper.findConsumesContentTypes(springWebHandlerMethod);
                        return consumesContentTypes.isEmpty() ? Set.of(ALL_VALUE) : consumesContentTypes;
                    } else if (ApiResponse.class.equals(owningType)) {
                        return contentTypesMapper.findProducesContentTypes(springWebHandlerMethod);
                    }
                    throw new IllegalStateException("Cannot provide media types for " + owningType.getSimpleName());
                };
                return mapperContext -> mapperContext.withMediaTypesProvider(mediaTypesProvider);
            }
            return null; // indicates we can't map this handler method context
        }
    }

    @RequiredArgsConstructor
    public static class RequestBodyMapper implements HandlerMethod.RequestBodyMapper {

        private final SpringWebHandlerMethodContentTypesMapper contentTypesMapper;
        private final SpringWebHandlerMethodRequestBodyParameterMapper requestBodyParameterMapper;

        @Nullable
        @Override
        public List<HandlerMethod.RequestBody> map(HandlerMethod handlerMethod) {
            if (handlerMethod instanceof SpringWebHandlerMethod springWebHandlerMethod) {
                return requestBodyParameterMapper
                        .findRequestBodyParameter(springWebHandlerMethod)
                        .map(requestBodyParameter ->
                                buildSpringWebRequestBody(requestBodyParameter, springWebHandlerMethod))
                        .map(HandlerMethod.RequestBody.class::cast)
                        .map(Collections::singletonList)
                        .orElseGet(Collections::emptyList);
            }
            return null; // indicates we can't map this handler method instance
        }

        private AbstractSpringWebHandlerMethod.SpringWebRequestBody buildSpringWebRequestBody(
                RequestBodyParameter requestBodyParameter, SpringWebHandlerMethod handlerMethod) {
            return new AbstractSpringWebHandlerMethod.SpringWebRequestBody(
                    requestBodyParameter.getParameter().getAnnotationsSupplier(),
                    contentTypesMapper.findConsumesContentTypes(handlerMethod),
                    requestBodyParameter.getParameter().getType(),
                    requestBodyParameter.isRequired()) {
                @Override
                public HandlerMethod.Context getContext() {
                    return handlerMethod;
                }
            };
        }
    }

    @RequiredArgsConstructor
    public static class ResponseMapper implements HandlerMethod.ResponseMapper {

        private final SpringWebHandlerMethodContentTypesMapper contentTypesMapper;
        private final SpringWebHandlerMethodResponseCodeMapper responseCodeMapper;
        private final SpringWebHandlerMethodReturnTypeMapper returnTypeMapper;

        @Nullable
        @Override
        public List<HandlerMethod.Response> map(HandlerMethod handlerMethod) {
            if (handlerMethod instanceof SpringWebHandlerMethod springWebHandlerMethod) {
                return Collections.singletonList(new AbstractSpringWebHandlerMethod.SpringWebResponse(
                        responseCodeMapper.getResponseCode(springWebHandlerMethod),
                        contentTypesMapper.findProducesContentTypes(springWebHandlerMethod),
                        Optional.of(returnTypeMapper.getReturnType(springWebHandlerMethod))));
            }
            return null; // indicates we can't map this handler method instance
        }
    }
}
