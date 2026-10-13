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

import static io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier.Purpose.SCHEMA_BUILDING;
import static io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils.earlierThan;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class InitialTypeBuilderForSchemaAnnotation implements InitialTypeBuilder {

    // make this higher precedence as the implementation from the @Schema annotation should override anything else
    public static final int ORDER = earlierThan(DEFAULT_ORDER);

    private final OpenApiObjectMapperSupplier openApiObjectMapperSupplier;
    private final AnnotationsSupplierFactory annotationsSupplierFactory;

    @Nullable
    @Override
    public InitialType build(
            SchemaResolver.Caller caller,
            JavaType javaType,
            AnnotationsSupplier annotationsSupplier,
            RecursiveBuilder recursiveBuilder) {
        return annotationsSupplier
                .findAnnotations(Schema.class)
                .map(Schema::implementation)
                .filter(clazz -> !Void.class.equals(clazz))
                .findFirst()
                .map(clazz -> recursiveBuilder.build(
                        openApiObjectMapperSupplier.get(SCHEMA_BUILDING).constructType(clazz),
                        annotationsSupplierFactory.createFromAnnotatedElement(clazz)))
                .orElse(null);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
