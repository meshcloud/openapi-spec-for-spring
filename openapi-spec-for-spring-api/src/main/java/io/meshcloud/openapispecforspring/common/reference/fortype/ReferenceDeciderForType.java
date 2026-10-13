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

package io.meshcloud.openapispecforspring.common.reference.fortype;

/**
 * Decides if the given to referenced item should become part of
 * the {@link io.meshcloud.openapispecforspring.model.Components
 * Open Api model components}. Otherwise, it stays inlined.
 *
 * <p>Type-specific interfaces are typically overridden to
 * customize this decision (for example, one can control
 * if nothing shall be referenced for a given type).
 *
 * @param <T> type of the to be referenced item
 */
@FunctionalInterface
public interface ReferenceDeciderForType<T> {
    /**
     * Decide if item should become a reference and part of the
     * {@link io.meshcloud.openapispecforspring.model.Components
     * Open Api model components}.
     *
     * <p>Default implementation returns {@code true}
     * if the {@code numberOfUsages} is larger than 1.
     *
     * @param item                item to be referenced
     * @param referenceIdentifier identifier for item
     * @param numberOfUsages      number of usages
     * @return true if item shall be referenced, false otherwise
     */
    boolean turnIntoReference(T item, String referenceIdentifier, long numberOfUsages);
}
