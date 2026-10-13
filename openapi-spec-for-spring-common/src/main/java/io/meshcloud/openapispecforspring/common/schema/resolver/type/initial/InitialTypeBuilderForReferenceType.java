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

package io.meshcloud.openapispecforspring.common.schema.resolver.type.initial;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InitialTypeBuilderForReferenceType implements InitialTypeBuilder {

    public static final int ORDER = DEFAULT_ORDER;

    private final AnnotationsSupplierFactory annotationsSupplierFactory;

    @Nullable
    @Override
    public InitialType build(
            SchemaResolver.Caller caller,
            JavaType javaType,
            AnnotationsSupplier annotationsSupplier,
            RecursiveBuilder recursiveBuilder) {
        if (javaType.isReferenceType()) {
            JavaType contentType = javaType.getContentType();
            return recursiveBuilder.build(
                    contentType,
                    annotationsSupplier.andThen(
                            annotationsSupplierFactory.createFromAnnotatedElement(contentType.getRawClass())));
        }
        return null;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
