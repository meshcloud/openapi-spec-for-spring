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

package io.meshcloud.openapispecforspring.common.schema.customizer;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.model.media.Schema;
import javax.annotation.Nullable;

public class SchemaCustomizerForNullable implements SchemaCustomizer {
    @Override
    public void customize(
            Schema schema,
            JavaType javaType,
            AnnotationsSupplier annotationsSupplier,
            RecursiveResolver recursiveResolver) {
        annotationsSupplier.findAnnotations(Nullable.class).findFirst().ifPresent(ignored -> schema.setNullable(true));
    }
}
