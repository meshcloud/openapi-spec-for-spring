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

import static java.lang.Boolean.TRUE;

import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.Nullable;

public class DefaultReferenceIdentifierBuilderForSchema implements ReferenceIdentifierBuilderForSchema {

    private static final String NULLABLE_IDENTIFIER_PREFIX = "nullable";
    private static final Pattern CHARACTERS_OUTSIDE_COMPONENT_NAME = Pattern.compile("[^a-zA-Z0-9.\\-_]");

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public String buildIdentifier(Schema schema, @Nullable String suggestedIdentifier, int numberOfSetters) {
        List<Object> identifierComponents = new ArrayList<>();
        if (TRUE.equals(schema.getNullable())) {
            identifierComponents.add(NULLABLE_IDENTIFIER_PREFIX);
        }
        if (StringUtils.isNotBlank(suggestedIdentifier)) {
            identifierComponents.add(suggestedIdentifier);
        } else if (StringUtils.isNotBlank(schema.getType())) {
            identifierComponents.add(schema.getType());
            if (StringUtils.isNotBlank(schema.getFormat())) {
                identifierComponents.add(schema.getFormat());
            }
            if (schema.getItems() != null) {
                identifierComponents.add(
                        buildIdentifier(schema.getItems(), schema.getItems().getName(), numberOfSetters));
            }
        } else {
            // fallback to dummy counted identifier, as schema are sometimes required to be referenced
            identifierComponents.addAll(Arrays.asList("Schema", counter.getAndIncrement()));
        }
        String identifier = identifierComponents.stream().map(Object::toString).collect(Collectors.joining("_"));
        return CHARACTERS_OUTSIDE_COMPONENT_NAME.matcher(identifier).replaceAll("_");
    }
}
