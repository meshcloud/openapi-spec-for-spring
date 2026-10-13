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

package io.meshcloud.openapispecforspring.common.operation;

import io.meshcloud.openapispecforspring.common.mapper.MapperContext;
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.reference.ReferencedItemConsumer;
import io.meshcloud.openapispecforspring.common.reference.ReferencedItemConsumerSupplier;
import java.util.function.Function;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor(staticName = "of")
public class OperationBuilderContextImpl implements OperationBuilderContext {
    @Getter
    private final OperationInfo operationInfo;

    private final Function<HandlerMethod.@Nullable Context, MapperContext> mapperContextSupplier;
    private final ReferencedItemConsumerSupplier referencedItemConsumerSupplier;

    @Override
    public <T extends ReferencedItemConsumer> T getReferencedItemConsumer(Class<T> referencedItemConsumerClazz) {
        return referencedItemConsumerSupplier.get(referencedItemConsumerClazz);
    }

    @Override
    public MapperContext getMapperContext(HandlerMethod.@Nullable Context context) {
        return mapperContextSupplier.apply(context);
    }
}
