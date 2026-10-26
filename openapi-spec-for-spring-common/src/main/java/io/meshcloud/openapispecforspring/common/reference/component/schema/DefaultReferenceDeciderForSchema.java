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

import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.AnyValueSchema;
import io.meshcloud.openapispecforspring.model.media.Schema;
import org.apache.commons.lang3.StringUtils;

public class DefaultReferenceDeciderForSchema implements ReferenceDeciderForSchema {

    @Override
    public boolean turnIntoReference(Schema schema, String referenceIdentifier, long numberOfUsages) {
        if (onlyTypeIsSet(schema) || schema instanceof AnyValueSchema) {
            return false;
        }
        if (StringUtils.isNotBlank(schema.getName())) {
            return true;
        }
        return numberOfUsages > 1;
    }

    private boolean onlyTypeIsSet(Schema schema) {
        // check if the schema becomes "empty" once the type is also null
        // this is a little trick to avoid checking all other properties!
        return schema.withType(null).isEmpty();
    }
}
