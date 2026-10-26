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

package io.meshcloud.openapispecforspring.common.schema.resolver;

import io.meshcloud.openapispecforspring.model.media.Schema;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

/**
 * Facets that describe how a property uses a type. They stay on the property, so that the schema of the type, and
 * with it the component that may be built from it, is the same for every property of that type.
 */
final class SchemaUseFacets {

    private static final List<Facet<?>> FACETS = List.of(
            new Facet<>(Schema::getDescription, Schema::setDescription),
            new Facet<>(Schema::getTitle, Schema::setTitle),
            new Facet<>(Schema::getNullable, Schema::setNullable),
            new Facet<>(Schema::getReadOnly, Schema::setReadOnly),
            new Facet<>(Schema::getWriteOnly, Schema::setWriteOnly),
            new Facet<>(Schema::getDeprecated, Schema::setDeprecated),
            new Facet<>(Schema::getExample, Schema::setExample),
            new Facet<>(Schema::getDefaultValue, Schema::setDefaultValue),
            new Facet<>(Schema::getExternalDocs, Schema::setExternalDocs));

    private SchemaUseFacets() {}

    static Schema moveDifferingFacets(Schema schema, TypeFacets typeFacets) {
        Schema useFacets = new Schema();
        for (int i = 0; i < FACETS.size(); i++) {
            FACETS.get(i).moveIfDiffering(schema, typeFacets.values().get(i), useFacets);
        }
        return useFacets;
    }

    // OpenAPI 3.0 ignores the siblings of a $ref, so the reference gets wrapped in allOf.
    static Schema wrapReference(Schema useFacets, Schema reference) {
        if (useFacets.isEmpty()) {
            return reference;
        }
        Schema wrapper = Schema.builder().allOf(List.of(reference)).build();
        copyInto(useFacets, wrapper);
        return wrapper;
    }

    static void addMissing(Schema useFacets, Schema moreUseFacets) {
        FACETS.forEach(facet -> facet.copyIfUnset(moreUseFacets, useFacets));
    }

    static void copyInto(Schema useFacets, Schema schema) {
        FACETS.forEach(facet -> facet.copyIfSet(useFacets, schema));
    }

    record TypeFacets(List<@Nullable Object> values) {
        static TypeFacets of(Schema typeOnlySchema) {
            List<@Nullable Object> values = new ArrayList<>();
            FACETS.forEach(facet -> values.add(facet.getter().apply(typeOnlySchema)));
            return new TypeFacets(Collections.unmodifiableList(values));
        }
    }

    private record Facet<T>(Function<Schema, @Nullable T> getter, BiConsumer<Schema, @Nullable T> setter) {

        @SuppressWarnings("unchecked")
        void moveIfDiffering(Schema schema, @Nullable Object typeValue, Schema useFacets) {
            T value = getter.apply(schema);
            if (!Objects.equals(value, typeValue)) {
                setter.accept(useFacets, value);
                setter.accept(schema, (T) typeValue);
            }
        }

        void copyIfUnset(Schema from, Schema to) {
            if (getter.apply(to) == null) {
                copyIfSet(from, to);
            }
        }

        void copyIfSet(Schema from, Schema to) {
            T value = getter.apply(from);
            if (value != null) {
                setter.accept(to, value);
            }
        }
    }
}
