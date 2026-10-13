/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
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

import io.meshcloud.openapispecforspring.common.operation.OperationBuilderContext;
import io.meshcloud.openapispecforspring.model.response.ApiResponses;
import org.apache.commons.lang3.StringUtils;

public class DefaultOperationApiResponsesDescriptionCustomizer implements OperationApiResponsesDescriptionCustomizer {
    @Override
    public void customize(ApiResponses apiResponses, OperationBuilderContext operationBuilderContext) {
        apiResponses.forEach((responseCode, apiResponse) -> {
            // make sure a description is always given, as it's required by the OpenApi spec
            if (StringUtils.isBlank(apiResponse.getDescription())) {
                apiResponse.setDescription("Default response");
            }
        });
    }
}
