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

package io.meshcloud.openapispecforspring.common.schema.resolver;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

/**
 * Lives for one spec build only, so the memory use does not grow while the application runs.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SchemaBuildMemo {

    private static final ThreadLocal<@Nullable Map<Object, Object>> ENTRIES = new ThreadLocal<>();

    public static <T> T during(Supplier<T> build) {
        ENTRIES.set(new HashMap<>());
        try {
            return build.get();
        } finally {
            ENTRIES.remove();
        }
    }

    // Map.computeIfAbsent fails when compute adds another entry, which a nested resolution may do (java:S3824).
    @SuppressWarnings({"unchecked", "java:S3824"})
    public static <T> T computeIfAbsent(Object key, Supplier<T> compute) {
        Map<Object, Object> entries = ENTRIES.get();
        if (entries == null) {
            return compute.get();
        }
        Object value = entries.get(key);
        if (value == null) {
            value = compute.get();
            entries.put(key, value);
        }
        return (T) value;
    }
}
