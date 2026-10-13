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

package io.meshcloud.openapispecforspring.model.operation;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.meshcloud.openapispecforspring.model.path.PathItem;
import io.meshcloud.openapispecforspring.model.trait.HasReference;
import java.util.Collections;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Callback
 *
 * @see "https://github.com/OAI/OpenAPI-Specification/blob/3.0.1/versions/3.0.1.md#callbackObject"
 */
@EqualsAndHashCode
@ToString
public class Callback implements HasReference<Callback> {

    @JsonIgnore
    @Setter
    private String callbackUrlExpression;

    @JsonIgnore
    @Setter
    private PathItem pathItem;

    @Getter
    @Setter
    private String ref;

    @JsonAnyGetter
    public Map<String, PathItem> get() {
        return Collections.singletonMap(callbackUrlExpression, pathItem);
    }

    @Override
    public Callback createInstance() {
        return new Callback();
    }
}
