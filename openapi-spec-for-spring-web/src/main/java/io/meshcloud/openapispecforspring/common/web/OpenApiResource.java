/*-
 * #%L
 * OpenAPI Spec for Spring :: Web
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

package io.meshcloud.openapispecforspring.common.web;

import static io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier.Purpose.OPEN_API_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.meshcloud.openapispecforspring.common.OpenApiConfigurationProperties;
import io.meshcloud.openapispecforspring.common.OpenApiSpecGenerator;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiYamlMapper;
import io.meshcloud.openapispecforspring.model.OpenApi;
import io.swagger.v3.oas.annotations.Hidden;
import java.lang.reflect.Method;
import java.util.Arrays;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

@RequiredArgsConstructor
@Hidden // exclude from OpenApi spec
@ResponseBody // all handler methods return response bodies, not view names
public class OpenApiResource {

    private static final String[] YAML_MEDIA_TYPES = {
        // see https://stackoverflow.com/q/332129
        // there seems to be no larger consensus what Accept: header tells we shall send YAML
        "application/yaml", "application/x-yaml", "text/yaml", "text/x-yaml", "text/vnd.yaml",
    };

    private final OpenApiSpecGenerator openApiSpecGenerator;
    private final OpenApiConfigurationProperties properties;
    private final OpenApiObjectMapperSupplier objectMapperSupplier;

    @Nullable
    private final OpenApiYamlMapper openApiYamlMapper;

    public <T> void registerMapping(
            RequestMappingInfoBuilder<T> requestMappingInfoBuilder,
            RequestMappingRegistrar<T> requestMappingRegistrar,
            Object handler) {
        requestMappingRegistrar.register(
                requestMappingInfoBuilder.build(properties.getApiDocsPath(), APPLICATION_JSON_VALUE),
                handler,
                findMethod(handler.getClass(), "getOpenApiAsJson"));
        requestMappingRegistrar.register(
                requestMappingInfoBuilder.build(properties.getApiDocsPath(), YAML_MEDIA_TYPES),
                handler,
                findMethod(handler.getClass(), "getOpenApiAsYaml"));
    }

    private static Method findMethod(Class<?> clazz, String methodName) {
        return Arrays.stream(clazz.getMethods())
                .filter(method -> method.getName().equals(methodName))
                .reduce((a, b) -> {
                    throw new IllegalStateException(
                            "Found more than one method with name " + methodName + " on " + clazz);
                })
                .orElseThrow(
                        () -> new IllegalStateException("No method found with name " + methodName + " on " + clazz));
    }

    public String getOpenApiAsJson() throws JsonProcessingException {
        OpenApi openApi = openApiSpecGenerator.generateOpenApi();
        return objectMapperSupplier.get(OPEN_API_JSON).writeValueAsString(openApi);
    }

    public String getOpenApiAsYaml() throws OpenApiYamlNotSupportedException, JsonProcessingException {
        if (openApiYamlMapper == null) {
            throw new OpenApiYamlNotSupportedException();
        }
        OpenApi openApi = openApiSpecGenerator.generateOpenApi();
        return openApiYamlMapper.map(openApi);
    }

    public static class OpenApiYamlNotSupportedException extends Exception {}

    public interface RequestMappingInfoBuilder<T> {
        T build(String path, String... produces);
    }

    public interface RequestMappingRegistrar<T> {
        void register(T mapping, Object handler, Method method);
    }
}
