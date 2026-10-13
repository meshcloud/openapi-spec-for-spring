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

package io.meshcloud.openapispecforspring.common.reference.component.parameter;

import io.meshcloud.openapispecforspring.common.reference.ReferenceType;
import io.meshcloud.openapispecforspring.common.reference.handler.AbstractDependentReferencedComponentHandler;
import io.meshcloud.openapispecforspring.model.Components;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.apache.commons.lang3.tuple.Pair;

@RequiredArgsConstructor
public class ReferencedParametersHandlerImpl extends AbstractDependentReferencedComponentHandler
        implements ReferencedParametersConsumer {
    private final ReferencedParameterStorage storage;

    @With
    @Nullable
    private final Object owner;

    @Override
    public void maybeAsReference(List<Parameter> parameters, Consumer<List<Parameter>> parametersSetter) {
        var modifiableParameters = new ArrayList<>(parameters);
        parametersSetter.accept(modifiableParameters);
        storage.maybeReferenceParameters(modifiableParameters, owner);
    }

    @Override
    public Pair<ReferenceType, List<ReferenceType>> getBuildDependencies() {
        return storage.getBuildDependencies();
    }

    @Override
    public void applyToComponents(Components components) {
        storage.buildReferencedItems(components::setParameters);
    }
}
