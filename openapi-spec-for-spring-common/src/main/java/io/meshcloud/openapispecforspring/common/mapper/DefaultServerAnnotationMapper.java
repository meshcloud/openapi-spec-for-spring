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

package io.meshcloud.openapispecforspring.common.mapper;

import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.setMapIfNotEmpty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiStringUtils.setStringIfNotBlank;

import io.meshcloud.openapispecforspring.model.server.Server;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultServerAnnotationMapper implements ServerAnnotationMapper {

    private final ServerVariableAnnotationMapper serverVariableAnnotationMapper;
    private final ExtensionAnnotationMapper extensionAnnotationMapper;

    @Override
    public List<Server> mapArray(io.swagger.v3.oas.annotations.servers.Server[] serversAnnotations) {
        return Arrays.stream(serversAnnotations).map(this::map).toList();
    }

    @Override
    public Server map(io.swagger.v3.oas.annotations.servers.Server serverAnnotation) {
        Server server = new Server();
        setStringIfNotBlank(serverAnnotation.description(), server::setDescription);
        setStringIfNotBlank(serverAnnotation.url(), server::setUrl);
        setMapIfNotEmpty(serverVariableAnnotationMapper.mapArray(serverAnnotation.variables()), server::setVariables);
        setMapIfNotEmpty(extensionAnnotationMapper.mapArray(serverAnnotation.extensions()), server::setExtensions);
        return server;
    }
}
