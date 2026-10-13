/*-
 * #%L
 * OpenAPI Spec for Spring :: Model
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

package io.meshcloud.openapispecforspring.model.response;

import io.meshcloud.openapispecforspring.model.header.Header;
import io.meshcloud.openapispecforspring.model.link.Link;
import io.meshcloud.openapispecforspring.model.media.Content;
import io.meshcloud.openapispecforspring.model.trait.HasContent;
import io.meshcloud.openapispecforspring.model.trait.HasExtensions;
import io.meshcloud.openapispecforspring.model.trait.HasReference;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponse
 *
 * @see "https://github.com/OAI/OpenAPI-Specification/blob/3.0.1/versions/3.0.1.md#responseObject"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiResponse implements HasContent, HasExtensions, HasReference<ApiResponse> {
    private String description;
    private Map<String, Header> headers;
    private Content content;
    private Map<String, Link> links;
    private Map<String, Object> extensions;
    private String ref;

    @Override
    public ApiResponse createInstance() {
        return new ApiResponse();
    }
}
