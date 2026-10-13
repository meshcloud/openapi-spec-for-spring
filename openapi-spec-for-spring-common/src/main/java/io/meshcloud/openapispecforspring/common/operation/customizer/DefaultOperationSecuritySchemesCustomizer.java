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

package io.meshcloud.openapispecforspring.common.operation.customizer;

import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.buildStringMapFromStream;

import io.meshcloud.openapispecforspring.common.mapper.SecuritySchemeAnnotationMapper;
import io.meshcloud.openapispecforspring.common.operation.OperationBuilderContext;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.reference.component.securityscheme.ReferencedSecuritySchemesConsumer;
import io.meshcloud.openapispecforspring.model.operation.Operation;
import io.meshcloud.openapispecforspring.model.security.SecurityScheme;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultOperationSecuritySchemesCustomizer implements OperationCustomizer {

    public static final int ORDER = DEFAULT_ORDER;

    private final SecuritySchemeAnnotationMapper securitySchemeAnnotationMapper;

    @Override
    public void customize(Operation operation, OperationBuilderContext operationBuilderContext) {
        HandlerMethod handlerMethod = operationBuilderContext.getOperationInfo().getHandlerMethod();
        Map<String, SecurityScheme> securitySchemes = buildStringMapFromStream(
                handlerMethod.findAnnotations(io.swagger.v3.oas.annotations.security.SecurityScheme.class),
                io.swagger.v3.oas.annotations.security.SecurityScheme::name,
                securitySchemeAnnotationMapper::map);
        operationBuilderContext
                .getReferencedItemConsumer(ReferencedSecuritySchemesConsumer.class)
                .accept(securitySchemes);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
