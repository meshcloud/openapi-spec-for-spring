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

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethod;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public class SpringWebHandlerMethodIdentifierMerger {
    private static final String SEPARATOR = "_";

    public String mergeIdentifiers(List<SpringWebHandlerMethod> handlerMethods) {
        Deque<String> identifiers = handlerMethods.stream()
                .map(HandlerMethod::getIdentifier)
                .distinct()
                .sorted()
                .collect(Collectors.toCollection(ArrayDeque::new));
        String commonPrefix = getCommonPrefix(identifiers);
        return Stream.concat(
                        Stream.of(commonPrefix),
                        identifiers.stream()
                                .map(identifier -> stripCommonPrefix(commonPrefix, identifier))
                                .filter(s -> !s.isEmpty()))
                .collect(Collectors.joining(SEPARATOR));
    }

    public String stripCommonPrefix(String commonPrefix, String identifier) {
        String stripped = identifier.substring(commonPrefix.length());
        if (stripped.startsWith(SEPARATOR)) {
            return stripped.substring(SEPARATOR.length());
        }
        return stripped;
    }

    public String getCommonPrefix(Deque<String> identifiers) {
        String commonPrefix = StringUtils.getCommonPrefix(identifiers.getFirst(), identifiers.getLast());
        if (commonPrefix.endsWith(SEPARATOR)) {
            return commonPrefix.substring(0, commonPrefix.length() - SEPARATOR.length());
        }
        return commonPrefix;
    }
}
