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

package io.meshcloud.openapispecforspring.common.paths;

import io.meshcloud.openapispecforspring.model.path.RequestMethod;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class SpringWebRequestMethodEnumMapper {
    public Set<RequestMethod> map(Collection<org.springframework.web.bind.annotation.RequestMethod> requestMethods) {
        return requestMethods.stream()
                .map(Enum::name)
                .map(RequestMethod::valueOf)
                .collect(Collectors.toSet());
    }
}
