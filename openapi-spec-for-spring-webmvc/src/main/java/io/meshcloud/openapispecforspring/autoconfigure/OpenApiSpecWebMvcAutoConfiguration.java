/*-
 * #%L
 * OpenAPI Spec for Spring :: WebMVC
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

package io.meshcloud.openapispecforspring.autoconfigure;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import io.meshcloud.openapispecforspring.common.OpenApiConfigurationProperties;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethodsProvider;
import io.meshcloud.openapispecforspring.common.paths.SpringWebHandlerMethodBuilder;
import io.meshcloud.openapispecforspring.common.paths.SpringWebRequestMethodEnumMapper;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHttpMessageConvertersSupplier;
import io.meshcloud.openapispecforspring.common.web.OpenApiResource;
import io.meshcloud.openapispecforspring.webmvc.HandlerMethodPathPatternsProviderForWebMvc;
import io.meshcloud.openapispecforspring.webmvc.HandlerMethodsProviderForWebMvc;
import io.meshcloud.openapispecforspring.webmvc.OpenApiRequestAwareSupplierForWebMvc;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OpenApiSpecWebMvcAutoConfiguration {

    @Bean
    @Conditional(OpenApiConfigurationProperties.EnabledCondition.class)
    public InitializingBean openApiResourceRegistration(
            OpenApiResource openApiResource, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return () -> openApiResource.registerMapping(
                (path, produces) -> RequestMappingInfo.paths(path)
                        .methods(GET)
                        .produces(produces)
                        .options(requestMappingHandlerMapping.getBuilderConfiguration())
                        .build(),
                requestMappingHandlerMapping::registerMapping,
                openApiResource);
    }

    @Bean
    @ConditionalOnMissingBean
    public HandlerMethodsProvider handlerMethodsProviderFromWebMvc(
            RequestMappingHandlerMapping requestMappingHandlerMapping,
            SpringWebHandlerMethodBuilder springWebHandlerMethodBuilder,
            SpringWebRequestMethodEnumMapper springWebRequestMethodEnumMapper,
            HandlerMethodPathPatternsProviderForWebMvc handlerMethodPathPatternsProviderForWebMvc) {
        return new HandlerMethodsProviderForWebMvc(
                requestMappingHandlerMapping,
                springWebHandlerMethodBuilder,
                springWebRequestMethodEnumMapper,
                handlerMethodPathPatternsProviderForWebMvc);
    }

    @Bean
    @ConditionalOnMissingBean
    public HandlerMethodPathPatternsProviderForWebMvc handlerMethodPathPatternsProviderForWebMvc() {
        return new HandlerMethodPathPatternsProviderForWebMvc();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiRequestAwareSupplierForWebMvc openApiRequestAwareProviderForWebMvc(
            WebRequest webRequest, HttpServletRequest httpServletRequest) {
        return new OpenApiRequestAwareSupplierForWebMvc(webRequest, httpServletRequest);
    }

    @Bean
    @ConditionalOnMissingBean
    public SpringWebHttpMessageConvertersSupplier httpMessageConvertersSupplierForWebMvc(
            RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        return requestMappingHandlerAdapter::getMessageConverters;
    }
}
