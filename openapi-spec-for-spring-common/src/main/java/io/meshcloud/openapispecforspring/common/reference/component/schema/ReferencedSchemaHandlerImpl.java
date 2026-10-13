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

package io.meshcloud.openapispecforspring.common.reference.component.schema;

import io.meshcloud.openapispecforspring.common.reference.ReferenceType;
import io.meshcloud.openapispecforspring.common.reference.handler.AbstractDependentReferencedComponentHandler;
import io.meshcloud.openapispecforspring.model.Components;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.List;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

@RequiredArgsConstructor
public class ReferencedSchemaHandlerImpl extends AbstractDependentReferencedComponentHandler
        implements ReferencedSchemaConsumer {

    private final ReferencedSchemaStorage storage;

    @Override
    public void maybeAsReference(Schema schema, Consumer<Schema> setter) {
        // immediately apply the schema,
        // as it's not decided if the schema is referenced later at all
        setter.accept(schema);
        storage.storeMaybeReference(schema, setter);
    }

    @Override
    public void alwaysAsReference(Schema schema, Consumer<Schema> setter) {
        storage.storeAlwaysReference(schema, setter);
    }

    @Override
    public Pair<ReferenceType, List<ReferenceType>> getBuildDependencies() {
        return storage.getBuildDependencies();
    }

    @Override
    public void applyToComponents(Components components) {
        storage.buildReferencedItems(components::setSchemas);
    }
}
