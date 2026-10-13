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

package io.meshcloud.openapispecforspring.common.schema.resolver;

import static io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier.Purpose.SCHEMA_BUILDING;
import static io.meshcloud.openapispecforspring.common.util.OpenApiLoggingUtils.logPretty;
import static io.meshcloud.openapispecforspring.common.util.OpenApiLoggingUtils.prettyPrintSchema;
import static io.meshcloud.openapispecforspring.common.util.OpenApiObjectUtils.setIfNotEmpty;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferenceDecisionAwareSchemaSetter;
import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferencedSchemaConsumer;
import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaCustomizer;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.TypeResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialSchemaBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialTypeBuilder;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.common.util.OpenApiLoggingUtils;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
@Slf4j
public class DefaultSchemaResolver implements SchemaResolver {

    private final OpenApiObjectMapperSupplier openApiObjectMapperSupplier;
    private final AnnotationsSupplierFactory annotationsSupplierFactory;

    private final List<InitialTypeBuilder> initialTypeBuilders;
    private final List<InitialSchemaBuilder> initialSchemaBuilders;
    private final List<SchemaCustomizer> schemaCustomizers;
    private final List<TypeResolver> typeResolvers;

    @Override
    public Schema resolveFromClassWithoutReference(
            Caller caller, Class<?> clazz, ReferencedSchemaConsumer referencedSchemaConsumer) {
        return resolveFromTypeWithoutReference(
                caller, clazz, annotationsSupplierFactory.createFromAnnotatedElement(clazz), referencedSchemaConsumer);
    }

    @Override
    public void resolveFromType(
            Caller caller,
            Type type,
            AnnotationsSupplier annotationsSupplier,
            ReferencedSchemaConsumer referencedSchemaConsumer,
            Consumer<Schema> schemaSetter) {
        setIfNotEmpty(
                resolveFromTypeWithoutReference(caller, type, annotationsSupplier, referencedSchemaConsumer),
                schema -> referencedSchemaConsumer.maybeAsReference(schema, schemaSetter));
    }

    private Schema resolveFromTypeWithoutReference(
            Caller caller,
            Type type,
            AnnotationsSupplier annotationsSupplier,
            ReferencedSchemaConsumer referencedSchemaConsumer) {
        ObjectMapper mapper = openApiObjectMapperSupplier.get(SCHEMA_BUILDING);
        Context context = new Context(caller, referencedSchemaConsumer, mapper);
        JavaType javaType = mapper.constructType(type);
        Schema schema = context.buildSchemaFromTypeRecursively(javaType, annotationsSupplier);
        context.resolveReferencedSchemas();
        return RecursiveSchema.unwrap(schema);
    }

    @RequiredArgsConstructor
    private class Context implements NestedSchemaBuilder {

        private final Caller caller;
        private final ReferencedSchemaConsumer referencedSchemaConsumer;
        private final ObjectMapper objectMapper;
        private final Map<TypeResolver.RecursionKey, Schema> knownSchemas = new LinkedHashMap<>();
        private final LinkedList<ReferencableSchema> referencableSchemas = new LinkedList<>();

        Schema buildSchemaFromTypeRecursively(JavaType javaType, AnnotationsSupplier annotationsSupplier) {
            InitialType initialType = buildInitialType(javaType, annotationsSupplier);
            Schema schema = buildInitialSchema(initialType);
            customizeSchema(schema, initialType);
            return runTypeResolvers(schema, initialType);
        }

        @Override
        public NestedSchema buildNestedSchema(JavaType javaType, AnnotationsSupplier annotationsSupplier) {
            InitialType initialType = buildInitialType(javaType, annotationsSupplier);
            Schema schema = buildInitialSchema(initialType);
            customizeSchema(schema, initialType);
            Schema typeOnlySchema = buildCustomizedSchema(
                    initialType,
                    annotationsSupplierFactory.createFromAnnotatedElement(
                            initialType.getType().getRawClass()));
            Schema useFacets = SchemaUseFacets.moveDifferingFacets(schema, typeOnlySchema);
            boolean initialTypeBuilderReplacedAnnotations = initialType.getAnnotationsSupplier() != annotationsSupplier;
            if (initialTypeBuilderReplacedAnnotations) {
                SchemaUseFacets.addMissing(
                        useFacets,
                        SchemaUseFacets.moveDifferingFacets(
                                buildCustomizedSchema(initialType, annotationsSupplier), typeOnlySchema));
            }
            return new NestedSchema(runTypeResolvers(schema, initialType), useFacets);
        }

        private Schema buildCustomizedSchema(InitialType initialType, AnnotationsSupplier annotationsSupplier) {
            Schema schema = buildInitialSchema(initialType);
            schemaCustomizers.forEach(customizer ->
                    customizer.customize(schema, initialType.getType(), annotationsSupplier, (clazz, setter) -> {}));
            return schema;
        }

        private Schema runTypeResolvers(Schema schema, InitialType initialType) {
            for (TypeResolver typeResolver : typeResolvers) {
                TypeResolverActions actions = new TypeResolverActions();
                TypeResolver.RecursionKey recursionKey = typeResolver.resolve(caller, schema, initialType, actions);
                if (actions.isEmpty()) {
                    // the type resolve hasn't added any "recursive" actions, so try the next type resolver
                    continue;
                }
                // prevent depth-first recursion if we already know this schema
                Schema knownSchema = checkKnownSchema(schema, recursionKey);
                if (knownSchema != null) {
                    return knownSchema;
                }
                actions.runRecursively();
                return recursionKey != null ? new RecursiveSchema(schema, recursionKey) : schema;
            }
            return schema;
        }

        public void resolveReferencedSchemas() {
            knownSchemas.forEach((recursionKey, knownSchema) ->
                    LOGGER.debug("Known Schema: {} -> {}", recursionKey.hashCode(), logPretty(knownSchema)));
            referencableSchemas.forEach(referencableSchema -> {
                LOGGER.debug("Resolving: {}", logPretty(referencableSchema.getSchema()));
                if (referencableSchema.getSchema() instanceof RecursiveSchema recursiveSchema) {
                    referencedSchemaConsumer.alwaysAsReference(
                            recursiveSchema.getSchema(), referencableSchema.getSchemaConsumer());
                } else if (referencableSchema.isMustBeReferenced()) {
                    referencedSchemaConsumer.alwaysAsReference(
                            referencableSchema.getSchema(), referencableSchema.getSchemaConsumer());
                } else {
                    referencedSchemaConsumer.maybeAsReference(
                            referencableSchema.getSchema(), referencableSchema.getSchemaConsumer());
                }
            });
        }

        @Nullable
        private Schema checkKnownSchema(Schema schema, TypeResolver.@Nullable RecursionKey recursionKey) {
            if (recursionKey == null) {
                return null;
            }
            Schema existingSchema = knownSchemas.get(recursionKey);
            if (existingSchema != null) {
                return new RecursiveSchema(existingSchema, recursionKey);
            }
            knownSchemas.put(recursionKey, schema);
            return null;
        }

        private InitialType buildInitialType(JavaType javaType, AnnotationsSupplier annotationsSupplier) {
            InitialTypeBuilder.RecursiveBuilder recursiveBuilder = new InitialTypeRecursiveBuilder();
            for (InitialTypeBuilder initialTypeBuilder : initialTypeBuilders) {
                InitialType initialType =
                        initialTypeBuilder.build(caller, javaType, annotationsSupplier, recursiveBuilder);
                if (initialType != null) {
                    return initialType;
                }
            }
            return InitialType.of(javaType, annotationsSupplier);
        }

        private Schema buildInitialSchema(InitialType initialType) {
            for (InitialSchemaBuilder initialSchemaBuilder : initialSchemaBuilders) {
                Schema initialSchema = initialSchemaBuilder.buildFromType(caller, initialType);
                if (initialSchema != null) {
                    return initialSchema;
                }
            }
            throw new IllegalStateException("No initial schema builder found for " + initialType.getType());
        }

        private void customizeSchema(Schema schema, InitialType initialType) {
            schemaCustomizers.forEach(customizer -> customizer.customize(
                    schema,
                    initialType.getType(),
                    initialType.getAnnotationsSupplier(),
                    (clazz, setter) -> setIfNotEmpty(
                            DefaultSchemaResolver.this.resolveFromClassWithoutReference(
                                    caller, clazz, referencedSchemaConsumer),
                            innerSchema -> referencedSchemaConsumer.alwaysAsReference(innerSchema, setter))));
        }

        private class TypeResolverActions extends LinkedList<TypeResolverAction>
                implements TypeResolver.SchemaBuilderFromType {

            @Override
            public void buildSchemaFromType(
                    JavaType javaType, AnnotationsSupplier annotationsSupplier, Consumer<Schema> schemaConsumer) {
                add(TypeResolverAction.of(javaType, annotationsSupplier, schemaConsumer, false));
            }

            @Override
            public void buildSchemaReferenceFromType(
                    JavaType javaType, AnnotationsSupplier annotationsSupplier, Consumer<Schema> schemaConsumer) {
                add(TypeResolverAction.of(javaType, annotationsSupplier, schemaConsumer, true));
            }

            void runRecursively() {
                forEach(action -> {
                    ReferencableSchema referencableSchema = action.run(Context.this);
                    referencableSchemas.add(referencableSchema);
                });
            }
        }

        private class InitialTypeRecursiveBuilder implements InitialTypeBuilder.RecursiveBuilder {
            @Nullable
            @Override
            public InitialType build(JavaType javaType, AnnotationsSupplier annotationsSupplier) {
                return buildInitialType(javaType, annotationsSupplier);
            }

            @Nullable
            @Override
            public InitialType build(Type type, AnnotationsSupplier annotationsSupplier) {
                return buildInitialType(objectMapper.constructType(type), annotationsSupplier);
            }
        }
    }

    @FunctionalInterface
    private interface NestedSchemaBuilder {
        NestedSchema buildNestedSchema(JavaType javaType, AnnotationsSupplier annotationsSupplier);
    }

    private record NestedSchema(Schema schema, Schema useFacets) {}

    // The facets go onto an inline schema only once the storage has compared the schemas, so that schemas which
    // differ only in their use still share one component.
    @RequiredArgsConstructor
    private static class UseFacetsApplyingSetter implements ReferenceDecisionAwareSchemaSetter {
        private final Schema schema;
        private final Schema useFacets;
        private final Consumer<Schema> schemaConsumer;
        private boolean referenced;

        @Override
        public void accept(Schema schemaOrReference) {
            if (schemaOrReference.getRef() != null) {
                referenced = true;
                schemaConsumer.accept(SchemaUseFacets.wrapReference(useFacets, schemaOrReference));
            } else {
                schemaConsumer.accept(schemaOrReference);
            }
        }

        @Override
        public void afterReferencesDecided() {
            if (!referenced) {
                SchemaUseFacets.copyInto(useFacets, schema);
            }
        }
    }

    @RequiredArgsConstructor(staticName = "of")
    @Getter
    private static class ReferencableSchema {
        private final Schema schema;
        private final Consumer<Schema> schemaConsumer;
        private final boolean mustBeReferenced;
    }

    @RequiredArgsConstructor(staticName = "of")
    private static class TypeResolverAction {
        private final JavaType javaType;
        private final AnnotationsSupplier annotationsSupplier;
        private final Consumer<Schema> schemaConsumer;
        private final boolean mustBeReferenced;

        ReferencableSchema run(NestedSchemaBuilder nestedSchemaBuilder) {
            NestedSchema nestedSchema = nestedSchemaBuilder.buildNestedSchema(javaType, annotationsSupplier);
            Schema schema = nestedSchema.schema();
            if (!nestedSchema.useFacets().isEmpty()) {
                schema.setUseFacets(nestedSchema.useFacets());
            }
            LOGGER.debug("Setting {}", logPretty(schema));
            schemaConsumer.accept(schema);
            return ReferencableSchema.of(
                    schema,
                    new UseFacetsApplyingSetter(schema, nestedSchema.useFacets(), schemaConsumer),
                    mustBeReferenced);
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
    private static class RecursiveSchema extends Schema implements OpenApiLoggingUtils.HasToPrettyString {
        @Getter
        private final Schema schema;

        @EqualsAndHashCode.Include
        private final TypeResolver.RecursionKey recursionKey;

        @EqualsAndHashCode.Include
        private @Nullable Schema includedUseFacets() {
            return getUseFacets();
        }

        public static Schema unwrap(Schema schema) {
            if (schema instanceof RecursiveSchema recursiveSchema) {
                return recursiveSchema.getSchema();
            }
            return schema;
        }

        @Override
        public String getName() {
            return schema.getName();
        }

        @Override
        public String toPrettyString() {
            return "->" + prettyPrintSchema(schema) + "$" + recursionKey.hashCode();
        }
    }
}
