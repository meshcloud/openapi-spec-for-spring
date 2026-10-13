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

package io.meshcloud.openapispecforspring.common.reference.component.link;

import io.meshcloud.openapispecforspring.common.reference.AbstractReferencedItemStorage;
import io.meshcloud.openapispecforspring.common.reference.ReferenceType;
import io.meshcloud.openapispecforspring.common.reference.fortype.ReferenceDeciderForType;
import io.meshcloud.openapispecforspring.common.reference.fortype.ReferenceIdentifierBuilderForType;
import io.meshcloud.openapispecforspring.common.reference.fortype.ReferenceIdentifierConflictResolverForType;
import io.meshcloud.openapispecforspring.model.link.Link;
import java.util.Collections;
import java.util.Map;

public class ReferencedLinkStorage extends AbstractReferencedItemStorage<Link> {
    ReferencedLinkStorage(
            ReferenceDeciderForType<Link> referenceDecider,
            ReferenceIdentifierBuilderForType<Link> referenceIdentifierFactory,
            ReferenceIdentifierConflictResolverForType<Link> referenceIdentifierConflictResolver) {
        super(
                ReferenceType.LINK,
                referenceDecider,
                referenceIdentifierFactory,
                referenceIdentifierConflictResolver,
                Link::new,
                Collections.emptyList());
    }

    public void maybeReferenceLinks(Map<String, Link> links) {
        addEntriesFromMap(links);
    }
}
