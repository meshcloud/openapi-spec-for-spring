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
import static io.meshcloud.openapispecforspring.common.util.OpenApiObjectUtils.setIfNotEmpty;

import io.meshcloud.openapispecforspring.model.security.OAuthFlows;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultOAuthFlowsAnnotationMapper implements OAuthFlowsAnnotationMapper {

    private final OAuthFlowAnnotationMapper oAuthFlowAnnotationMapper;
    private final ExtensionAnnotationMapper extensionAnnotationMapper;

    @Override
    public OAuthFlows map(io.swagger.v3.oas.annotations.security.OAuthFlows annotation) {
        OAuthFlows oAuthFlows = new OAuthFlows();
        setIfNotEmpty(oAuthFlowAnnotationMapper.map(annotation.implicit()), oAuthFlows::setImplicit);
        setIfNotEmpty(oAuthFlowAnnotationMapper.map(annotation.password()), oAuthFlows::setPassword);
        setIfNotEmpty(oAuthFlowAnnotationMapper.map(annotation.clientCredentials()), oAuthFlows::setClientCredentials);
        setIfNotEmpty(oAuthFlowAnnotationMapper.map(annotation.authorizationCode()), oAuthFlows::setAuthorizationCode);
        setMapIfNotEmpty(extensionAnnotationMapper.mapArray(annotation.extensions()), oAuthFlows::setExtensions);
        return oAuthFlows;
    }
}
