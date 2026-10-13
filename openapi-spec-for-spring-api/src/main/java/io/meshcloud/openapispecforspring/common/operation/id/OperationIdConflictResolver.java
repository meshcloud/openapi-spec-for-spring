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

package io.meshcloud.openapispecforspring.common.operation.id;

import io.meshcloud.openapispecforspring.common.operation.OperationWithInfo;
import java.util.List;

/**
 * Resolver for non-unique operation IDs. See also {@link OperationIdProvider}.
 */
public interface OperationIdConflictResolver {
    /**
     * Resolve a operation id conflict by modifying the given operations via reference.
     * <p>
     * OpenApi model building will fail if conflict hasn't been resolved.
     *
     * @param operationId             conflicting operation id
     * @param operationsWithConflicts operations with conflicts and infos.
     */
    void resolveConflict(String operationId, List<OperationWithInfo> operationsWithConflicts);
}
