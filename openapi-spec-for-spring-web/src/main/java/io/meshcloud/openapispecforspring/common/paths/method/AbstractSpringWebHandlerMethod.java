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

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.model.media.Content;
import io.meshcloud.openapispecforspring.model.response.ApiResponse;
import io.meshcloud.openapispecforspring.model.trait.HasIsEmpty;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractSpringWebHandlerMethod implements HandlerMethod {

    @Getter
    private final List<Parameter> parameters;

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    @ToString
    public static class SpringWebType implements HandlerMethod.Type {
        private final java.lang.reflect.Type type;

        @ToString.Exclude
        private final AnnotationsSupplier annotationsSupplier;
    }

    @RequiredArgsConstructor
    public static class SpringWebParameter implements Parameter {
        @Nullable
        private final String parameterName;

        private final Type parameterType;

        @Getter
        private final AnnotationsSupplier annotationsSupplier;

        @Override
        public Optional<String> getName() {
            return Optional.ofNullable(parameterName);
        }

        @Override
        public Optional<Type> getType() {
            return Optional.of(parameterType);
        }
    }

    @RequiredArgsConstructor
    @Getter
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class SpringWebRequestBody implements RequestBody {
        private final AnnotationsSupplier annotationsSupplier;
        private final Set<String> consumesContentTypes;
        private final Optional<Type> type;

        @Nullable
        private final Boolean required;

        @Override
        public void customize(io.meshcloud.openapispecforspring.model.requestbody.RequestBody requestBody) {
            if (requestBody.getRequired() == null && required != null) {
                requestBody.setRequired(required);
            }
        }
    }

    @RequiredArgsConstructor
    @Getter
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static class SpringWebResponse implements Response {
        private final String responseCode;
        private final Set<String> producesContentTypes;
        private final Optional<Type> type;

        public boolean shouldClearContent(Content content) {
            return producesContentTypes.equals(content.keySet());
        }

        @Override
        public void customize(ApiResponse apiResponse) {
            Content content = apiResponse.getContent();
            boolean hasEmptyContent = content != null
                    && content.size() == 1
                    && content.values().stream().allMatch(HasIsEmpty::isEmpty);
            if (hasEmptyContent && shouldClearContent(content)) {
                apiResponse.setContent(null);
            }
        }
    }
}
