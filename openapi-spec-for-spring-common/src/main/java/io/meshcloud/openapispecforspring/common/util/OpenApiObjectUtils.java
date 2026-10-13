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

package io.meshcloud.openapispecforspring.common.util;

import io.meshcloud.openapispecforspring.model.trait.HasIsEmpty;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenApiObjectUtils {

    public static <T> void setIf(@Nullable T value, Predicate<T> condition, Consumer<T> setter) {
        if (condition.test(value)) {
            setter.accept(value);
        }
    }

    public static <T> void setIfNotNull(@Nullable T value, Consumer<T> setter) {
        setIf(value, Objects::nonNull, setter);
    }

    public static <T extends HasIsEmpty<?>> void setIfNotEmpty(T value, Consumer<T> setter) {
        setIf(value, v -> !v.isEmpty(), setter);
    }
}
