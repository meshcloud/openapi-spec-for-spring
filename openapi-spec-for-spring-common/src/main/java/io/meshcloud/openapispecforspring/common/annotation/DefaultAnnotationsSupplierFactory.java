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

package io.meshcloud.openapispecforspring.common.annotation;

import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.core.annotation.AnnotationFilter;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;

public class DefaultAnnotationsSupplierFactory implements AnnotationsSupplierFactory {

    private static final ClassValue<Optional<Class<? extends Annotation>>> REPEATABLE_CONTAINER = new ClassValue<>() {
        @Override
        protected Optional<Class<? extends Annotation>> computeValue(Class<?> annotationType) {
            return Optional.ofNullable(annotationType.getAnnotation(Repeatable.class))
                    .map(Repeatable::value);
        }
    };

    private static final ClassValue<Optional<Method>> CONTAINER_VALUE_METHOD = new ClassValue<>() {
        @Override
        protected Optional<Method> computeValue(Class<?> containerType) {
            return Arrays.stream(containerType.getMethods())
                    .filter(method -> MergedAnnotation.VALUE.equals(method.getName()))
                    .filter(method -> method.getParameterCount() == 0)
                    .findFirst();
        }
    };

    private final Map<AnnotatedElement, AnnotationsSupplierFromAnnotatedElement> elementSuppliers =
            new ConcurrentHashMap<>();

    @Override
    public AnnotationsSupplier createFromMember(AnnotatedMember annotatedMember) {
        // using annotatedMember.getAnnotatedElement() and falling back
        // to the other method is not correct, as it doesn't find annotations specified on fields
        return new AnnotationSupplierFromMember(annotatedMember, this::supplierFor);
    }

    @Override
    public AnnotationsSupplier createFromAnnotatedElement(AnnotatedElement annotatedElement) {
        return supplierFor(annotatedElement);
    }

    private AnnotationsSupplierFromAnnotatedElement supplierFor(AnnotatedElement annotatedElement) {
        return elementSuppliers.computeIfAbsent(annotatedElement, AnnotationsSupplierFromAnnotatedElement::new);
    }

    private abstract static class RepeatableContainerAwareAnnotationsSupplier implements AnnotationsSupplier {
        @Override
        public final <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
            // always append possible repeatable annotations
            return Stream.concat(findAnnotationsDelegate(annotationType), findFromRepeatableContainer(annotationType));
        }

        private <A extends Annotation> Stream<A> findFromRepeatableContainer(Class<A> annotationType) {
            Optional<Class<? extends Annotation>> containerType = REPEATABLE_CONTAINER.get(annotationType);
            Optional<Method> valueMethod = containerType.flatMap(CONTAINER_VALUE_METHOD::get);
            if (containerType.isEmpty() || valueMethod.isEmpty()) {
                return Stream.empty();
            }
            return findAnnotationsDelegate(containerType.get())
                    .flatMap(container -> containedAnnotations(container, valueMethod.get()))
                    .filter(annotationType::isInstance)
                    .map(annotationType::cast);
        }

        private static Stream<Object> containedAnnotations(Annotation container, Method valueMethod) {
            try {
                Object result = valueMethod.invoke(container);
                return result instanceof Object[] objects ? Arrays.stream(objects) : Stream.empty();
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new IllegalStateException("Cannot invoke value method getter " + valueMethod, e);
            }
        }

        protected abstract <A extends Annotation> Stream<A> findAnnotationsDelegate(Class<A> annotationType);
    }

    private static class AnnotationSupplierFromMember extends RepeatableContainerAwareAnnotationsSupplier {
        private final AnnotatedMember annotatedMember;
        private final Optional<AnnotationsSupplierFromAnnotatedElement> typeUseAnnotationsSupplier;

        AnnotationSupplierFromMember(
                AnnotatedMember annotatedMember,
                Function<AnnotatedElement, AnnotationsSupplierFromAnnotatedElement> elementSuppliers) {
            this.annotatedMember = annotatedMember;
            this.typeUseAnnotationsSupplier = findAnnotatedType(annotatedMember).map(elementSuppliers);
        }

        @Override
        public <A extends Annotation> Stream<A> findAnnotationsDelegate(Class<A> annotationType) {
            // Jackson only sees declaration annotations, which misses TYPE_USE annotations such as JSpecify's.
            return Optional.ofNullable(annotatedMember.getAnnotation(annotationType))
                    .or(() -> typeUseAnnotationsSupplier.flatMap(supplier ->
                            supplier.findAnnotationsDelegate(annotationType).findFirst()))
                    .stream();
        }

        private static Optional<AnnotatedType> findAnnotatedType(AnnotatedMember annotatedMember) {
            if (annotatedMember instanceof AnnotatedParameter annotatedParameter
                    && annotatedParameter.getOwner().getMember() instanceof Executable executable) {
                Parameter[] parameters = executable.getParameters();
                int index = annotatedParameter.getIndex();
                return index < parameters.length ? Optional.of(parameters[index].getAnnotatedType()) : Optional.empty();
            }
            Member member = annotatedMember.getMember();
            if (member instanceof Field field) {
                return Optional.of(field.getAnnotatedType());
            }
            if (member instanceof Method method && method.getParameterCount() == 0) {
                return Optional.of(method.getAnnotatedReturnType());
            }
            return Optional.empty();
        }
    }

    private static class AnnotationsSupplierFromAnnotatedElement extends RepeatableContainerAwareAnnotationsSupplier {
        private final AnnotatedElement annotatedElement;
        private final MergedAnnotations mergedAnnotations;
        private final Map<Class<?>, List<? extends Annotation>> foundAnnotations = new ConcurrentHashMap<>();

        AnnotationsSupplierFromAnnotatedElement(AnnotatedElement annotatedElement) {
            this.annotatedElement = annotatedElement;
            this.mergedAnnotations = MergedAnnotations.from(
                    annotatedElement,
                    MergedAnnotations.SearchStrategy.TYPE_HIERARCHY,
                    RepeatableContainers.standardRepeatables());
        }

        @Override
        @SuppressWarnings("unchecked")
        public <A extends Annotation> Stream<A> findAnnotationsDelegate(Class<A> annotationType) {
            return ((List<A>) foundAnnotations.computeIfAbsent(annotationType, ignored -> lookUp(annotationType)))
                    .stream();
        }

        private <A extends Annotation> List<A> lookUp(Class<A> annotationType) {
            if (AnnotationFilter.PLAIN.matches(annotationType)) {
                // PLAIN annotations are ignored by merged annotations API, see
                // https://github.com/spring-projects/spring-framework/issues/24932
                return List.of(annotatedElement.getAnnotationsByType(annotationType));
            }
            return mergedAnnotations.stream(annotationType)
                    .map(MergedAnnotation::synthesize)
                    .toList();
        }
    }
}
