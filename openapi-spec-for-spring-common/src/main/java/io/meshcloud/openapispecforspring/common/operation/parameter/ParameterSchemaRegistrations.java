/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
 * %%
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
import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.common.reference.ReferencedItemConsumer;
import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferencedSchemaConsumer;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.jspecify.annotations.Nullable;

/**
 * Holds back the schemas registered for references while parameters are built, until the merge has decided which
 * parameters stay. Each copy of a merged parameter would otherwise count as one more use of its schema, and a schema
 * used once would become a shared component.
 */
final class ParameterSchemaRegistrations {

    private record Registration(Parameter owner, Runnable register) {}

    private final List<Registration> registrations = new ArrayList<>();

    OperationBuilderContext recordingFor(Parameter owner, OperationBuilderContext context) {
        return new OperationBuilderContext() {
            @Override
            public OperationInfo getOperationInfo() {
                return context.getOperationInfo();
            }

            @Override
            public MapperContext getMapperContext(HandlerMethod.@Nullable Context handlerMethodContext) {
                return context.getMapperContext(handlerMethodContext);
            }

            @Override
            public <T extends ReferencedItemConsumer> T getReferencedItemConsumer(Class<T> consumerClass) {
                T consumer = context.getReferencedItemConsumer(consumerClass);
                return consumer instanceof ReferencedSchemaConsumer schemaConsumer
                        ? consumerClass.cast(recording(owner, schemaConsumer))
                        : consumer;
            }
        };
    }

    private ReferencedSchemaConsumer recording(Parameter owner, ReferencedSchemaConsumer target) {
        return new ReferencedSchemaConsumer() {
            @Override
            public void maybeAsReference(Schema schema, Consumer<Schema> setter) {
                setter.accept(schema);
                registrations.add(new Registration(owner, () -> target.maybeAsReference(schema, setter)));
            }

            @Override
            public void alwaysAsReference(Schema schema, Consumer<Schema> setter) {
                registrations.add(new Registration(owner, () -> target.alwaysAsReference(schema, setter)));
            }
        };
    }

    /**
     * Registers the schemas of the given parameters, and of the copies whose schema a merged parameter holds in its
     * {@code anyOf}.
     */
    void registerFor(List<Parameter> parameters) {
        Set<Object> kept = Collections.newSetFromMap(new IdentityHashMap<>());
        parameters.forEach(parameter -> {
            kept.add(parameter);
            Schema schema = parameter.getSchema();
            if (schema != null && schema.getAnyOf() != null) {
                kept.addAll(schema.getAnyOf());
            }
        });
        registrations.stream()
                .filter(registration -> kept.contains(registration.owner())
                        || kept.contains(registration.owner().getSchema()))
                .forEach(registration -> registration.register().run());
    }
}
