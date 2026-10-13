/*-
 * #%L
 * OpenAPI Spec for Spring :: API
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

package io.meshcloud.openapispecforspring.common.operation.response;

import static io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils.muchLaterThan;

/**
 * Intermediate interface for {@link
 * io.meshcloud.openapispecforspring.model.response.ApiResponse
 * api response description} to support easier bean overriding.
 */
public interface OperationApiResponsesDescriptionCustomizer extends OperationApiResponsesCustomizer {
    // make sure we run pretty late, as we don't want to interfere with other customizer possibly providing a better
    // description
    int ORDER = muchLaterThan(DEFAULT_ORDER);

    @Override
    default int getOrder() {
        return ORDER;
    }
}
