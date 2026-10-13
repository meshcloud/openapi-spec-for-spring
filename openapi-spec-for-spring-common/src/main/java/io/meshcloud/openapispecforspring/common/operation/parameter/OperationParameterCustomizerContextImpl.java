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

package io.meshcloud.openapispecforspring.common.operation.parameter;

import io.meshcloud.openapispecforspring.common.mapper.MapperContext;
import io.meshcloud.openapispecforspring.common.operation.OperationBuilderContext;
import io.meshcloud.openapispecforspring.common.operation.OperationInfo;
import io.meshcloud.openapispecforspring.common.operation.parameter.customizer.OperationParameterCustomizerContext;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.reference.ReferencedItemConsumer;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor(staticName = "of")
public class OperationParameterCustomizerContextImpl implements OperationParameterCustomizerContext {
    private final HandlerMethod.@Nullable Parameter handlerMethodParameter;

    private final OperationBuilderContext operationBuilderContext;

    @Override
    public Optional<HandlerMethod.Parameter> getHandlerMethodParameter() {
        return Optional.ofNullable(handlerMethodParameter);
    }

    @Override
    public OperationInfo getOperationInfo() {
        return operationBuilderContext.getOperationInfo();
    }

    @Override
    public MapperContext getMapperContext(HandlerMethod.@Nullable Context context) {
        return operationBuilderContext.getMapperContext(context);
    }

    @Override
    public <T extends ReferencedItemConsumer> T getReferencedItemConsumer(Class<T> referencedItemConsumerClazz) {
        return operationBuilderContext.getReferencedItemConsumer(referencedItemConsumerClazz);
    }
}
