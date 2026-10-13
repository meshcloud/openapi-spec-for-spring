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

package io.meshcloud.openapispecforspring.common.operation.response;

import static io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver.Caller.API_RESPONSE;
import static io.meshcloud.openapispecforspring.common.util.OpenApiCollectionUtils.firstNonNull;

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.operation.OperationBuilderContext;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferencedSchemaConsumer;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.model.media.Content;
import io.meshcloud.openapispecforspring.model.media.MediaType;
import io.meshcloud.openapispecforspring.model.response.ApiResponse;
import io.meshcloud.openapispecforspring.model.response.ApiResponses;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.MergedAnnotation;

@RequiredArgsConstructor
public class DefaultOperationApiResponsesFromMethodCustomizer implements OperationApiResponsesFromMethodCustomizer {

    static final String DEFAULT_ANNOTATION_RESPONSE_CODE = MergedAnnotation.of(
                    io.swagger.v3.oas.annotations.responses.ApiResponse.class)
            .getDefaultValue("responseCode", String.class)
            .orElseThrow(
                    () -> new IllegalStateException("Cannot infer default response code from ApiResponse annotation"));

    private final ApiResponseDefaultProvider apiResponseDefaultProvider;
    private final SchemaResolver schemaResolver;
    private final List<HandlerMethod.ResponseMapper> handlerMethodResponseMappers;

    @Override
    public void customize(ApiResponses apiResponses, OperationBuilderContext operationBuilderContext) {
        HandlerMethod handlerMethod = operationBuilderContext.getOperationInfo().getHandlerMethod();
        ReferencedSchemaConsumer referencedSchemaConsumer =
                operationBuilderContext.getReferencedItemConsumer(ReferencedSchemaConsumer.class);
        Map<String, ApiResponse> annotatedSuccessResponsesWithoutContent = apiResponses.entrySet().stream()
                .filter(entry ->
                        isSuccessWithBody(entry.getKey()) && entry.getValue().getContent() == null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));
        firstNonNull(handlerMethodResponseMappers, mapper -> mapper.map(handlerMethod))
                .ifPresent(handlerMethodResponses -> handlerMethodResponses.forEach(handlerMethodResponse -> {
                    ApiResponse defaultApiResponse =
                            getAndReplaceDefaultApiResponse(apiResponses, handlerMethodResponse.getResponseCode());
                    addMethodResponseContent(
                            defaultApiResponse, handlerMethod, handlerMethodResponse, referencedSchemaConsumer);
                    handlerMethodResponse.customize(defaultApiResponse);
                    annotatedSuccessResponsesWithoutContent.forEach((responseCode, apiResponse) -> {
                        if (!responseCode.equals(handlerMethodResponse.getResponseCode())) {
                            shareMethodResponseContent(defaultApiResponse, apiResponse, handlerMethodResponse);
                        }
                    });
                }));
    }

    private static boolean isSuccessWithBody(String responseCode) {
        return responseCode.length() == 3 && responseCode.startsWith("2") && !"204".equals(responseCode);
    }

    private void addMethodResponseContent(
            ApiResponse apiResponse,
            HandlerMethod handlerMethod,
            HandlerMethod.Response handlerMethodResponse,
            ReferencedSchemaConsumer referencedSchemaConsumer) {
        Content content = Optional.ofNullable(apiResponse.getContent()).orElseGet(Content::new);
        addMediaTypesToApiResponseContent(content, handlerMethod, handlerMethodResponse, referencedSchemaConsumer);
        if (!content.isEmpty()) {
            apiResponse.setContent(content);
        }
    }

    // Shared, so each response schema is resolved once and not again per status code.
    private static void shareMethodResponseContent(
            ApiResponse methodApiResponse, ApiResponse apiResponse, HandlerMethod.Response handlerMethodResponse) {
        Content methodContent = methodApiResponse.getContent();
        if (methodContent == null) {
            return;
        }
        Content content = Optional.ofNullable(apiResponse.getContent()).orElseGet(Content::new);
        handlerMethodResponse.getProducesContentTypes().forEach(contentType -> {
            MediaType mediaType = methodContent.get(contentType);
            if (mediaType != null) {
                content.putIfAbsent(contentType, mediaType);
            }
        });
        if (!content.isEmpty()) {
            apiResponse.setContent(content);
        }
    }

    private void addMediaTypesToApiResponseContent(
            Content content,
            HandlerMethod handlerMethod,
            HandlerMethod.Response handlerMethodResponse,
            ReferencedSchemaConsumer referencedSchemaConsumer) {
        handlerMethodResponse.getProducesContentTypes().forEach(contentType -> {
            MediaType mediaType = content.getOrDefault(contentType, new MediaType());
            // we might have already set some media type, only amend this if the schema is not present
            // this gives annotations a higher preference than the schema inferred from the method return type
            if (mediaType.getSchema() == null) {
                handlerMethodResponse.getType().ifPresent(responseType -> {
                    AnnotationsSupplier annotationsSupplier = responseType
                            .getAnnotationsSupplier()
                            // restrict searching the annotations from the handler method to @Schema or @ArraySchema
                            // only
                            // this prevents things like @Deprecated on the method to make the response schema also
                            // deprecated
                            // but we can still use @Schema to modify properties of the "default" response
                            .andThen(new AnnotationsSupplier() {
                                @Override
                                public <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
                                    if (annotationType.equals(Schema.class)
                                            || annotationType.equals(ArraySchema.class)) {
                                        return handlerMethod.findAnnotations(annotationType);
                                    }
                                    return Stream.empty();
                                }
                            });
                    schemaResolver.resolveFromType(
                            API_RESPONSE,
                            responseType.getType(),
                            annotationsSupplier,
                            referencedSchemaConsumer,
                            mediaType::setSchema);
                });
                // putting empty media types is ok
                // when there are multiple content types present for one response code
                content.put(contentType, mediaType);
            }
        });
    }

    private ApiResponse getAndReplaceDefaultApiResponse(ApiResponses apiResponses, String responseCodeFromMethod) {
        // we want to keep the place at which the default API response is inserted,
        // but we want to change the "default" response to the response code of the method
        AtomicReference<ApiResponse> defaultApiResponseHolder = new AtomicReference<>();
        ApiResponses modified = new ApiResponses();
        apiResponses.forEach((responseCode, apiResponse) -> {
            if (DEFAULT_ANNOTATION_RESPONSE_CODE.equals(responseCode) || responseCodeFromMethod.equals(responseCode)) {
                if (defaultApiResponseHolder.get() != null) {
                    // we cannot meaningfully merge those two API responses, so bail out
                    throw new IllegalStateException("Found default API response object for key "
                            + DEFAULT_ANNOTATION_RESPONSE_CODE + " and method code " + responseCodeFromMethod
                            + ". Only one variant is supported for default response.");
                }
                defaultApiResponseHolder.set(apiResponse);
                modified.put(responseCodeFromMethod, apiResponse);
            } else {
                modified.put(responseCode, apiResponse);
            }
        });

        // update via given reference to original map
        apiResponses.clear();
        apiResponses.putAll(modified);

        // fallback to provider if default isn't present
        if (defaultApiResponseHolder.get() == null) {
            ApiResponse defaultApiResponse = apiResponseDefaultProvider.build(responseCodeFromMethod);
            apiResponses.put(responseCodeFromMethod, defaultApiResponse);
            return defaultApiResponse;
        }
        return defaultApiResponseHolder.get();
    }
}
