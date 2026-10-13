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

import static io.meshcloud.openapispecforspring.common.util.OpenApiCollectionUtils.setCollectionIfNotEmpty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.buildStringMapFromStream;
import static io.meshcloud.openapispecforspring.common.util.OpenApiMapUtils.setMapIfNotEmpty;

import io.meshcloud.openapispecforspring.common.util.OpenApiStringUtils;
import io.meshcloud.openapispecforspring.model.server.ServerVariable;
import io.meshcloud.openapispecforspring.model.server.ServerVariables;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultServerVariableAnnotationMapper implements ServerVariableAnnotationMapper {

    private final ExtensionAnnotationMapper extensionAnnotationMapper;

    @Override
    public ServerVariables mapArray(io.swagger.v3.oas.annotations.servers.ServerVariable[] serverVariablesAnnotations) {
        return buildStringMapFromStream(
                Arrays.stream(serverVariablesAnnotations),
                io.swagger.v3.oas.annotations.servers.ServerVariable::name,
                this::map,
                ServerVariables::new);
    }

    @Override
    public ServerVariable map(io.swagger.v3.oas.annotations.servers.ServerVariable serverVariableAnnotation) {
        ServerVariable serverVariable = new ServerVariable();
        OpenApiStringUtils.setStringIfNotBlank(
                serverVariableAnnotation.defaultValue(), serverVariable::setDefaultValue);
        OpenApiStringUtils.setStringIfNotBlank(serverVariableAnnotation.description(), serverVariable::setDescription);
        setCollectionIfNotEmpty(
                Arrays.asList(serverVariableAnnotation.allowableValues()), serverVariable::setEnumValues);
        setMapIfNotEmpty(
                extensionAnnotationMapper.mapArray(serverVariableAnnotation.extensions()),
                serverVariable::setExtensions);
        return serverVariable;
    }
}
