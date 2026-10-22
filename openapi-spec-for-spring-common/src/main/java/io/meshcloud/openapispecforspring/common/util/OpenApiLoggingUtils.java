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

import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OpenApiLoggingUtils {

    private static Object logLazily(Supplier<String> stringSupplier) {
        return new Object() {
            @Override
            public String toString() {
                return stringSupplier.get();
            }
        };
    }

    public interface HasToPrettyString {
        String toPrettyString();
    }

    public static Object logPretty(Object object) {
        return logLazily(() -> {
            if (object instanceof HasToPrettyString hasToPrettyString) {
                return hasToPrettyString.toPrettyString();
            }
            if (object instanceof Schema schema) {
                return prettyPrintSchema(schema);
            }
            return object.toString();
        });
    }

    public static String prettyPrintSchema(Schema schema) {
        StringBuilder sb = new StringBuilder();
        if (schema.getNullable() != null && schema.getNullable()) {
            sb.append('?');
        }
        if (schema.getName() != null) {
            sb.append(schema.getName());
        } else if (schema.getItems() != null) {
            sb.append('[').append(prettyPrintSchema(schema.getItems())).append(']');
        } else if (schema.getType() != null) {
            sb.append("type:").append(schema.getType());
        } else if (schema.getRef() != null) {
            sb.append("-->").append(schema.getRef());
        }
        if (schema.getProperties() != null) {
            sb.append('{');
            schema.getProperties().forEach((propertyName, propertySchema) -> {
                sb.append(propertyName);
                sb.append('=');
                sb.append(prettyPrintSchema(propertySchema));
                sb.append(',');
            });
            sb.append("}");
        }
        return sb.toString();
    }
}
