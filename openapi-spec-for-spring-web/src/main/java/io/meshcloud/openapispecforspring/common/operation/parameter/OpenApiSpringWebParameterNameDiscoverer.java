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

import java.lang.reflect.Method;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * Integration of Spring's {@link
 * org.springframework.core.ParameterNameDiscoverer} to enable
 * the same parameter name discover algorithm as Spring is using.
 */
public interface OpenApiSpringWebParameterNameDiscoverer {
    /**
     * Discover parameter names for method. Return {@code
     * null} if that's not possible.
     *
     * @param method method to inspect
     * @return list of parameter names, or {@code null} if not possible.
     */
    @Nullable
    List<String> getParameterNames(Method method);
}
