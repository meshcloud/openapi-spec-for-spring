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

package io.meshcloud.openapispecforspring.common.schema.resolver.properties;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.jspecify.annotations.Nullable;
import org.springframework.util.ClassUtils;

/**
 * Kotlin marks nullable types with {@code org.jetbrains.annotations.Nullable}, which has class retention, so only
 * Kotlin's own metadata tells nullability at runtime. It is read through kotlin-reflect, if the application has it,
 * by plain reflection, so the library does not depend on Kotlin.
 */
final class KotlinNullability {

    @Nullable
    private final KotlinReflect kotlinReflect = KotlinReflect.loadIfPresent();

    private final Map<Class<?>, Set<Object>> nullableMembersByClass = new ConcurrentHashMap<>();

    boolean isNullable(AnnotatedMember member) {
        Class<?> declaringClass = member.getDeclaringClass();
        if (kotlinReflect == null || !declaringClass.isAnnotationPresent(kotlinReflect.metadataType())) {
            return false;
        }
        Set<Object> nullableMembers =
                nullableMembersByClass.computeIfAbsent(declaringClass, kotlinReflect::nullableMembers);
        if (member instanceof AnnotatedParameter parameter
                && parameter.getOwner().getMember() instanceof Executable executable) {
            return nullableMembers.contains(new ParameterKey(executable, parameter.getIndex()));
        }
        return nullableMembers.contains(member.getMember());
    }

    private record ParameterKey(Executable executable, int index) {}

    private record KotlinReflect(
            Class<? extends Annotation> metadataType,
            Method getKotlinClass,
            Method getDeclaredMemberProperties,
            Method getReturnType,
            Method getParameters,
            Method isMarkedNullable,
            Method getJavaGetter,
            Method getJavaField,
            Method getJavaSetter,
            Class<?> mutablePropertyType,
            Method getKotlinFunction,
            Method getParameterType,
            Method getParameterIndex) {

        @Nullable
        static KotlinReflect loadIfPresent() {
            ClassLoader classLoader = KotlinNullability.class.getClassLoader();
            if (!ClassUtils.isPresent("kotlin.reflect.full.KClasses", classLoader)) {
                return null;
            }
            try {
                Class<?> kClass = Class.forName("kotlin.reflect.KClass", false, classLoader);
                Class<?> kCallable = Class.forName("kotlin.reflect.KCallable", false, classLoader);
                Class<?> kProperty = Class.forName("kotlin.reflect.KProperty", false, classLoader);
                Class<?> kMutableProperty = Class.forName("kotlin.reflect.KMutableProperty", false, classLoader);
                Class<?> kParameter = Class.forName("kotlin.reflect.KParameter", false, classLoader);
                Class<?> jvmMapping = Class.forName("kotlin.reflect.jvm.ReflectJvmMapping", false, classLoader);
                return new KotlinReflect(
                        Class.forName("kotlin.Metadata", false, classLoader).asSubclass(Annotation.class),
                        Class.forName("kotlin.jvm.JvmClassMappingKt", false, classLoader)
                                .getMethod("getKotlinClass", Class.class),
                        Class.forName("kotlin.reflect.full.KClasses", false, classLoader)
                                .getMethod("getDeclaredMemberProperties", kClass),
                        kCallable.getMethod("getReturnType"),
                        kCallable.getMethod("getParameters"),
                        Class.forName("kotlin.reflect.KType", false, classLoader)
                                .getMethod("isMarkedNullable"),
                        jvmMapping.getMethod("getJavaGetter", kProperty),
                        jvmMapping.getMethod("getJavaField", kProperty),
                        jvmMapping.getMethod("getJavaSetter", kMutableProperty),
                        kMutableProperty,
                        jvmMapping.getMethod("getKotlinFunction", Constructor.class),
                        kParameter.getMethod("getType"),
                        kParameter.getMethod("getIndex"));
            } catch (ReflectiveOperationException | LinkageError e) {
                return null;
            }
        }

        Set<Object> nullableMembers(Class<?> clazz) {
            Set<Object> nullableMembers = new HashSet<>();
            try {
                addNullableProperties(clazz, nullableMembers);
                addNullableConstructorParameters(clazz, nullableMembers);
            } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
                // kotlin-reflect cannot read every class, e.g. synthetic ones; such a class has no nullable members.
                return Set.of();
            }
            return nullableMembers;
        }

        private void addNullableProperties(Class<?> clazz, Set<Object> nullableMembers)
                throws ReflectiveOperationException {
            Object kotlinClass = getKotlinClass.invoke(null, clazz);
            for (Object property : (Collection<?>) getDeclaredMemberProperties.invoke(null, kotlinClass)) {
                if (isMarkedNullable(getReturnType.invoke(property))) {
                    addIfNotNull(nullableMembers, getJavaGetter.invoke(null, property));
                    addIfNotNull(nullableMembers, getJavaField.invoke(null, property));
                    if (mutablePropertyType.isInstance(property)
                            && getJavaSetter.invoke(null, property) instanceof Method setter) {
                        nullableMembers.add(new ParameterKey(setter, 0));
                    }
                }
            }
        }

        private void addNullableConstructorParameters(Class<?> clazz, Set<Object> nullableMembers)
                throws ReflectiveOperationException {
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                Object function = getKotlinFunction.invoke(null, constructor);
                if (function == null) {
                    continue;
                }
                for (Object parameter : (Collection<?>) getParameters.invoke(function)) {
                    if (isMarkedNullable(getParameterType.invoke(parameter))) {
                        nullableMembers.add(
                                new ParameterKey(constructor, (Integer) getParameterIndex.invoke(parameter)));
                    }
                }
            }
        }

        private boolean isMarkedNullable(Object type) throws ReflectiveOperationException {
            return (Boolean) isMarkedNullable.invoke(type);
        }

        private static void addIfNotNull(Set<Object> members, @Nullable Object member) {
            if (member != null) {
                members.add(member);
            }
        }
    }
}
