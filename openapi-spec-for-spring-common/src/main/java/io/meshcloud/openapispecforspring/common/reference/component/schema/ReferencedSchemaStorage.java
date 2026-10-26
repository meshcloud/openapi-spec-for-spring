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

import io.meshcloud.openapispecforspring.common.reference.AbstractReferencedItemStorage;
import io.meshcloud.openapispecforspring.common.reference.ReferenceType;
import io.meshcloud.openapispecforspring.common.reference.fortype.ReferenceDeciderForType;
import io.meshcloud.openapispecforspring.common.reference.fortype.ReferenceIdentifierBuilderForType;
import io.meshcloud.openapispecforspring.common.reference.fortype.ReferenceIdentifierConflictResolverForType;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ReferencedSchemaStorage extends AbstractReferencedItemStorage<Schema> {

    private final List<ReferenceDecisionAwareSchemaSetter> referenceDecisionAwareSetters = new ArrayList<>();

    ReferencedSchemaStorage(
            ReferenceDeciderForType<Schema> referenceDecider,
            ReferenceIdentifierBuilderForType<Schema> referenceIdentifierFactory,
            ReferenceIdentifierConflictResolverForType<Schema> referenceIdentifierConflictResolver) {
        super(
                ReferenceType.SCHEMA,
                referenceDecider,
                referenceIdentifierFactory,
                referenceIdentifierConflictResolver,
                Schema::new,
                Collections.emptyList());
    }

    void storeAlwaysReference(Schema schema, Consumer<Schema> setter) {
        addEntry(
                schema,
                setter,
                AddEntryParameters.builder()
                        .referenceRequired(true)
                        .suggestedIdentifier(schema.getName())
                        .build());
    }

    void storeMaybeReference(Schema schema, Consumer<Schema> setter) {
        if (setter instanceof ReferenceDecisionAwareSchemaSetter referenceDecisionAwareSetter) {
            referenceDecisionAwareSetters.add(referenceDecisionAwareSetter);
        }
        addEntry(
                schema,
                setter,
                AddEntryParameters.builder()
                        .suggestedIdentifier(schema.getName())
                        .build());
    }

    private static final Schema GROUPING_USE_FACETS = new Schema();

    // A schema's own use facets stay on the property that uses it, so equal schemas share one component, while the
    // use facets of nested schemas still distinguish their owners.
    @Override
    protected Object groupingKey(Schema schema) {
        return List.of(schema.getClass(), schema.withUseFacets(GROUPING_USE_FACETS));
    }

    @Override
    public void buildReferencedItems(Consumer<Map<String, Schema>> referencedItemsConsumer) {
        super.buildReferencedItems(referencedItemsConsumer);
        referenceDecisionAwareSetters.forEach(ReferenceDecisionAwareSchemaSetter::afterReferencesDecided);
    }
}
