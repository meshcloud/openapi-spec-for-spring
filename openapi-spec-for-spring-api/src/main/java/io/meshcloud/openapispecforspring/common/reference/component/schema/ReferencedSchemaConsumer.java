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

package io.meshcloud.openapispecforspring.common.reference.component.schema;

import io.meshcloud.openapispecforspring.common.reference.fortype.ReferencedItemConsumerForType;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.function.Consumer;

/**
 * Consumer for to-be-referenced {@link Schema}. Also supports
 * to require a reference (useful in case of self-referencing
 * schemas).
 *
 * @see ReferencedItemConsumerForType
 */
public interface ReferencedSchemaConsumer extends ReferencedItemConsumerForType<Schema> {
    void alwaysAsReference(Schema schema, Consumer<Schema> setter);
}
