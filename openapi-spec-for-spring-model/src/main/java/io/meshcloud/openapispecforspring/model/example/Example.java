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

package io.meshcloud.openapispecforspring.model.example;

import io.meshcloud.openapispecforspring.model.trait.HasExtensions;
import io.meshcloud.openapispecforspring.model.trait.HasIsEmpty;
import io.meshcloud.openapispecforspring.model.trait.HasReference;
import java.util.Map;
import lombok.Data;

/**
 * Example
 */
@Data
public class Example implements HasExtensions, HasReference<Example>, HasIsEmpty<Example> {
    private String summary;
    private String description;
    private Object value;
    private String externalValue;
    private String ref;
    private Map<String, Object> extensions;

    @Override
    public Example createInstance() {
        return new Example();
    }
}
