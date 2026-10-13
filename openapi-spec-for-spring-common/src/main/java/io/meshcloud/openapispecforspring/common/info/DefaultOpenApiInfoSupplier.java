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

package io.meshcloud.openapispecforspring.common.info;

import static io.meshcloud.openapispecforspring.common.util.OpenApiObjectUtils.setIfNotNull;

import io.meshcloud.openapispecforspring.common.mapper.InfoAnnotationMapper;
import io.meshcloud.openapispecforspring.common.supplier.OpenAPIDefinitionAnnotationSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiSpringBootApplicationClassSupplier;
import io.meshcloud.openapispecforspring.model.info.Contact;
import io.meshcloud.openapispecforspring.model.info.Info;
import io.meshcloud.openapispecforspring.model.info.License;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultOpenApiInfoSupplier implements OpenApiInfoSupplier {
    private final OpenApiInfoConfigurationProperties properties;
    private final InfoAnnotationMapper infoAnnotationMapper;
    private final OpenApiVersionSupplier openApiVersionSupplier;
    private final OpenApiSpringBootApplicationClassSupplier springBootApplicationClassSupplier;
    private final OpenAPIDefinitionAnnotationSupplier openAPIDefinitionAnnotationSupplier;

    @Override
    public Info get() {
        Info info = openAPIDefinitionAnnotationSupplier
                .get()
                .map(OpenAPIDefinition::info)
                .map(infoAnnotationMapper::map)
                .orElseGet(Info::new);

        // application properties should take effect with higher prio
        setIfNotNull(properties.getTitle(), info::setTitle);
        setIfNotNull(properties.getDescription(), info::setDescription);
        setIfNotNull(properties.getTermsOfService(), info::setTermsOfService);
        setIfNotNull(properties.getContact(), contactConfig -> {
            Contact contact = Optional.ofNullable(info.getContact()).orElseGet(Contact::new);
            setIfNotNull(contactConfig.getName(), contact::setName);
            setIfNotNull(contactConfig.getUrl(), contact::setUrl);
            setIfNotNull(contactConfig.getEmail(), contact::setEmail);
            setIfNotNull(contactConfig.getExtensions(), contact::setExtensions);
            info.setContact(contact);
        });
        setIfNotNull(properties.getLicense(), licenseConfig -> {
            License license = Optional.ofNullable(info.getLicense()).orElseGet(License::new);
            setIfNotNull(licenseConfig.getName(), license::setName);
            setIfNotNull(licenseConfig.getUrl(), license::setUrl);
            setIfNotNull(licenseConfig.getExtensions(), license::setExtensions);
            info.setLicense(license);
        });
        setIfNotNull(properties.getVersion(), info::setVersion);
        setIfNotNull(properties.getExtensions(), info::setExtensions);

        if (info.getVersion() == null) {
            info.setVersion(openApiVersionSupplier.get());
        }

        if (info.getTitle() == null) {
            Optional<Class<?>> springBootApplicationClass =
                    springBootApplicationClassSupplier.findSpringBootApplicationClass();
            info.setTitle(springBootApplicationClass
                    .map(clazz -> "API for " + clazz.getSimpleName())
                    .orElse("Unknown API"));
        }

        return info;
    }
}
