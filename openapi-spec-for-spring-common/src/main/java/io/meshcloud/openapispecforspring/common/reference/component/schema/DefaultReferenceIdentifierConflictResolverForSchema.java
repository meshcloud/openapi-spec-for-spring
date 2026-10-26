/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
 * %%
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

import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DefaultReferenceIdentifierConflictResolverForSchema
        implements ReferenceIdentifierConflictResolverForSchema {

    @Override
    public List<String> resolveConflict(List<Schema> itemsWithSameReferenceIdentifier, String originalIdentifier) {
        if (isRequestAndResponseView(itemsWithSameReferenceIdentifier)) {
            return itemsWithSameReferenceIdentifier.stream()
                    .map(schema -> originalIdentifier
                            + (schema.getDirection() == Schema.Direction.REQUEST ? "Request" : "Response"))
                    .toList();
        }
        return IntStream.range(0, itemsWithSameReferenceIdentifier.size())
                .mapToObj(i -> originalIdentifier + "_" + i)
                .toList();
    }

    private static boolean isRequestAndResponseView(List<Schema> schemas) {
        return schemas.size() == 2
                && schemas.stream()
                        .map(Schema::getDirection)
                        .collect(Collectors.toSet())
                        .equals(Set.of(Schema.Direction.REQUEST, Schema.Direction.RESPONSE));
    }
}
