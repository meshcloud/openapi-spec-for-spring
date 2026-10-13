/*-
 * #%L
 * OpenAPI Spec for Spring :: WebMVC
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

package io.meshcloud.openapispecforspring.webmvc;

import io.meshcloud.openapispecforspring.common.paths.HandlerMethodWithInfo;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethodsProvider;
import io.meshcloud.openapispecforspring.common.paths.SpringWebHandlerMethodBuilder;
import io.meshcloud.openapispecforspring.common.paths.SpringWebRequestMethodEnumMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@RequiredArgsConstructor
public class HandlerMethodsProviderForWebMvc implements HandlerMethodsProvider {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final SpringWebHandlerMethodBuilder springWebHandlerMethodBuilder;
    private final SpringWebRequestMethodEnumMapper springWebRequestMethodEnumMapper;
    private final HandlerMethodPathPatternsProviderForWebMvc handlerMethodPathPatternsProviderForWebMvc;

    @Override
    public List<HandlerMethodWithInfo> getHandlerMethods() {
        return requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                .map(entry -> new HandlerMethodWithInfo(
                        springWebHandlerMethodBuilder.build(entry.getValue()),
                        handlerMethodPathPatternsProviderForWebMvc.findPatterns(entry.getKey()),
                        springWebRequestMethodEnumMapper.map(
                                entry.getKey().getMethodsCondition().getMethods())))
                .toList();
    }
}
