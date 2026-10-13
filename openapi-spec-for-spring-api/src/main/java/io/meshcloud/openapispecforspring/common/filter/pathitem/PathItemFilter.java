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

package io.meshcloud.openapispecforspring.common.filter.pathitem;

import io.meshcloud.openapispecforspring.model.path.PathItem;

/**
 * Filter for path items. Run AFTER the {@link PathItem path item} is built.
 *
 * <p>This filter does not remove possibly referenced
 * items due to operation building. Prefer using {@link
 * io.meshcloud.openapispecforspring.common.filter.handlermethod.HandlerMethodFilter
 * HandlerMethodFilter} whenever possible.
 */
@FunctionalInterface
public interface PathItemFilter {
    /**
     * Accept the given path item to become part of the OpenApi model.
     *
     * @param pathItem    built path item
     * @param pathPattern path pattern this path item is built for
     * @return true if path item shall be included, false otherwise
     */
    boolean accept(PathItem pathItem, String pathPattern);
}
