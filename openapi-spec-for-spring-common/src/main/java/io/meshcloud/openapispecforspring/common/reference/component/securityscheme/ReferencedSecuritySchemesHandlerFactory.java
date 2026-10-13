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

package io.meshcloud.openapispecforspring.common.reference.component.securityscheme;

import io.meshcloud.openapispecforspring.common.mapper.SecuritySchemeAnnotationMapper;
import io.meshcloud.openapispecforspring.common.reference.handler.ReferencedItemHandler;
import io.meshcloud.openapispecforspring.common.reference.handler.ReferencedItemHandlerFactory;
import io.meshcloud.openapispecforspring.common.security.OpenApiSecuritySchemesSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiSpringBootApplicationAnnotationsSupplier;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReferencedSecuritySchemesHandlerFactory implements ReferencedItemHandlerFactory {

    private final OpenApiSpringBootApplicationAnnotationsSupplier springBootApplicationAnnotationsSupplier;
    private final SecuritySchemeAnnotationMapper securitySchemeAnnotationMapper;
    private final List<OpenApiSecuritySchemesSupplier> openApiSecuritySchemesSuppliers;

    @Override
    public ReferencedItemHandler create() {
        return new ReferencedSecuritySchemesHandlerImpl(
                springBootApplicationAnnotationsSupplier,
                securitySchemeAnnotationMapper,
                openApiSecuritySchemesSuppliers);
    }
}
