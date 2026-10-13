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

package io.meshcloud.openapispecforspring.common.operation.parameter;

import io.meshcloud.openapispecforspring.autoconfigure.OpenApiSpecWebAutoConfiguration;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * Default implementation based on Spring's {@link
 * DefaultParameterNameDiscoverer} via {@link #delegate} and {@link
 * OpenApiSpecWebAutoConfiguration#defaultOpenApiSpringWebParameterNameDiscoverer()}.
 * <p>
 * This library does not mess around with the discovered Spring Parameters
 * by calling {@link MethodParameter#initParameterNameDiscovery} in {@link
 * io.meshcloud.openapispecforspring.common.paths.DefaultSpringWebHandlerMethodBuilder
 * DefaultSpringWebHandlerMethodBuilder}.
 *
 * @see org.springframework.core.MethodParameter#getParameterName
 * @see org.springframework.web.method.support.InvocableHandlerMethod#getMethodArgumentValues
 */
@RequiredArgsConstructor
public class DefaultOpenApiSpringWebParameterNameDiscoverer implements OpenApiSpringWebParameterNameDiscoverer {
    private final ParameterNameDiscoverer delegate;

    @Override
    @Nullable
    public List<String> getParameterNames(Method method) {
        String[] parameterNames = delegate.getParameterNames(method);
        if (parameterNames == null) {
            return null;
        }
        return Arrays.asList(parameterNames);
    }
}
