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

package io.meshcloud.openapispecforspring.model;

import io.meshcloud.openapispecforspring.model.example.Example;
import io.meshcloud.openapispecforspring.model.header.Header;
import io.meshcloud.openapispecforspring.model.link.Link;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.meshcloud.openapispecforspring.model.operation.Callback;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import io.meshcloud.openapispecforspring.model.requestbody.RequestBody;
import io.meshcloud.openapispecforspring.model.response.ApiResponse;
import io.meshcloud.openapispecforspring.model.security.SecurityScheme;
import io.meshcloud.openapispecforspring.model.trait.HasExtensions;
import io.meshcloud.openapispecforspring.model.trait.HasIsEmpty;
import io.meshcloud.openapispecforspring.model.trait.HasReference;
import java.util.Map;
import lombok.Data;

/**
 * Components
 *
 * @see "https://github.com/OAI/OpenAPI-Specification/blob/3.0.1/versions/3.0.1.md#componentsObject"
 */
@Data
public class Components implements HasExtensions, HasIsEmpty<Components> {
    private Map<String, ? extends HasReference<Example>> examples;
    private Map<String, ? extends HasReference<Header>> headers;
    private Map<String, ? extends HasReference<Parameter>> parameters;
    private Map<String, ? extends HasReference<RequestBody>> requestBodies;
    private Map<String, ? extends HasReference<ApiResponse>> responses;
    private Map<String, ? extends HasReference<Schema>> schemas;
    private Map<String, ? extends HasReference<SecurityScheme>> securitySchemes;
    private Map<String, ? extends HasReference<Link>> links;
    private Map<String, ? extends HasReference<Callback>> callbacks;
    private Map<String, Object> extensions;

    @Override
    public Components createInstance() {
        return new Components();
    }
}
