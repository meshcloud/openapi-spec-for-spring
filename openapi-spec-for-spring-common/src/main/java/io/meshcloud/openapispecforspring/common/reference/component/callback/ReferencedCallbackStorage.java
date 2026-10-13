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

package io.meshcloud.openapispecforspring.common.reference.component.callback;

import static io.meshcloud.openapispecforspring.common.reference.ReferenceType.API_RESPONSE;
import static io.meshcloud.openapispecforspring.common.reference.ReferenceType.CALLBACK;
import static io.meshcloud.openapispecforspring.common.reference.ReferenceType.PARAMETER;
import static io.meshcloud.openapispecforspring.common.reference.ReferenceType.REQUEST_BODY;

import io.meshcloud.openapispecforspring.common.reference.AbstractReferencedItemStorage;
import io.meshcloud.openapispecforspring.common.reference.fortype.ReferenceDeciderForType;
import io.meshcloud.openapispecforspring.common.reference.fortype.ReferenceIdentifierBuilderForType;
import io.meshcloud.openapispecforspring.common.reference.fortype.ReferenceIdentifierConflictResolverForType;
import io.meshcloud.openapispecforspring.model.operation.Callback;
import java.util.Arrays;
import java.util.Map;

public class ReferencedCallbackStorage extends AbstractReferencedItemStorage<Callback> {

    ReferencedCallbackStorage(
            ReferenceDeciderForType<Callback> referenceDecider,
            ReferenceIdentifierBuilderForType<Callback> referenceIdentifierFactory,
            ReferenceIdentifierConflictResolverForType<Callback> referenceIdentifierConflictResolver) {
        super(
                CALLBACK,
                referenceDecider,
                referenceIdentifierFactory,
                referenceIdentifierConflictResolver,
                Callback::new,
                Arrays.asList(CALLBACK, API_RESPONSE, PARAMETER, REQUEST_BODY));
    }

    void storeMaybeReferenceCallbacks(Map<String, Callback> callbacks) {
        // no need for ownership as callbacks are referenced at only one place
        // this makes life easier than for example ApiResponses reference handling
        // here, we don't need to track the callback name together with the setter,
        // but simpler build a Set of callback names inside the entry
        callbacks.forEach((callbackName, callback) -> addEntry(
                callback,
                referenceCallback -> callbacks.put(callbackName, referenceCallback),
                AddEntryParameters.builder().suggestedIdentifier(callbackName).build()));
    }
}
