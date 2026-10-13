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

import io.meshcloud.openapispecforspring.common.reference.ReferencedItemConsumer;
import io.meshcloud.openapispecforspring.common.reference.ReferencedItemConsumerSupplier;
import io.meshcloud.openapispecforspring.model.trait.HasContent;
import java.util.Optional;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MapperContextImpl implements MapperContext {
    private final ReferencedItemConsumerSupplier referencedItemConsumerSupplier;

    @Nullable
    @With
    private final MediaTypesProvider mediaTypesProvider;

    public static MapperContextImpl of(ReferencedItemConsumerSupplier referencedItemConsumerSupplier) {
        return new MapperContextImpl(referencedItemConsumerSupplier, null);
    }

    @Override
    public <T extends ReferencedItemConsumer> T getReferencedItemConsumer(Class<T> referencedItemConsumerClazz) {
        return referencedItemConsumerSupplier.get(referencedItemConsumerClazz);
    }

    @Override
    public Optional<Set<String>> findMediaTypes(Class<? extends HasContent> owningType) {
        return Optional.ofNullable(mediaTypesProvider).map(provider -> provider.getMediaTypes(owningType));
    }

    @Override
    public MapperContext withReferencedItemOwner(@Nullable Object owner) {
        return new MapperContextImpl(referencedItemConsumerSupplier.withOwner(owner), mediaTypesProvider);
    }
}
