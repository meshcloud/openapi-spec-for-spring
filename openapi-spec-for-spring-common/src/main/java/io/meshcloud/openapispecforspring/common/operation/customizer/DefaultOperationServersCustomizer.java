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

import static io.meshcloud.openapispecforspring.common.util.OpenApiCollectionUtils.setCollectionIfNotEmpty;

import io.meshcloud.openapispecforspring.common.mapper.ServerAnnotationMapper;
import io.meshcloud.openapispecforspring.common.operation.OperationBuilderContext;
import io.meshcloud.openapispecforspring.model.operation.Operation;
import io.meshcloud.openapispecforspring.model.server.Server;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultOperationServersCustomizer implements OperationCustomizer {

    public static final int ORDER = DEFAULT_ORDER;

    private final ServerAnnotationMapper serverAnnotationMapper;

    @Override
    public void customize(Operation operation, OperationBuilderContext operationBuilderContext) {
        List<Server> servers = Stream.concat(
                        operation.getServers() == null ? Stream.empty() : operation.getServers().stream(),
                        collectServersFromHandlerMethod(operationBuilderContext))
                .distinct()
                .toList();
        setCollectionIfNotEmpty(servers, operation::setServers);
    }

    private Stream<Server> collectServersFromHandlerMethod(OperationBuilderContext operationBuilderContext) {
        return operationBuilderContext
                .getOperationInfo()
                .getHandlerMethod()
                .findAnnotations(io.swagger.v3.oas.annotations.servers.Server.class)
                .map(serverAnnotationMapper::map);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
