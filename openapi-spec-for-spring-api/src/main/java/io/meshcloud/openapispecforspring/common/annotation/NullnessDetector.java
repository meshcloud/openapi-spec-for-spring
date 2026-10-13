/*-
 * #%L
 * OpenAPI Spec for Spring :: API
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

package io.meshcloud.openapispecforspring.common.annotation;

/**
 * Decides if an annotated element, such as a schema property
 * or a handler method parameter, may be null. Can be autowired as bean.
 */
@FunctionalInterface
public interface NullnessDetector {
    /**
     * Check if the annotated element may be null.
     *
     * @param annotationsSupplier annotations of the element
     * @return true if the element may be null
     */
    boolean isNullable(AnnotationsSupplier annotationsSupplier);
}
