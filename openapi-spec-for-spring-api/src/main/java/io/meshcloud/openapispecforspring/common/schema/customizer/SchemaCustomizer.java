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

package io.meshcloud.openapispecforspring.common.schema.customizer;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.function.Consumer;

/**
 * Customizer for {@link Schema}. Run AFTER the initial schema is built
 * and annotations are applied, but BEFORE nested properties are resolved.
 *
 * @see SchemaPropertiesCustomizer for customizing properties
 */
@FunctionalInterface
public interface SchemaCustomizer extends OpenApiOrderedUtils.DefaultOrdered {
    /**
     * Customize the given schema by reference.
     *
     * @param schema              schema
     * @param javaType            java type this schema was built from (might be Void)
     * @param annotationsSupplier annotations supplier used during schema building
     * @param recursiveResolver   recursive resolver
     */
    void customize(
            Schema schema,
            JavaType javaType,
            AnnotationsSupplier annotationsSupplier,
            RecursiveResolver recursiveResolver);

    /**
     * Recursive resolver for {@link #customize} call. Similar
     * to {@link SchemaResolver} but with less flexibility: only
     * class types can be analyzed, they are always referenced
     * and the {@link SchemaResolver.Mode mode} is fixed.
     *
     * <p> When used inside a customizer, be careful to prevent
     * endless recursion by only applying the customization
     * on a condition, such as a annotation being present.
     *
     * @see SchemaResolver#resolveFromClassWithoutReference
     */
    interface RecursiveResolver {
        /**
         * Resolves the class to a schema that is always referenced.
         *
         * @param clazz  class type to be resolved
         * @param setter setter to apply the schema reference
         */
        void alwaysAsReference(Class<?> clazz, Consumer<Schema> setter);
    }
}
