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

package io.meshcloud.openapispecforspring.common.annotation;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import org.springframework.util.ClassUtils;

final class OptionalAnnotationTypes {
    private final List<Class<? extends Annotation>> presentTypes;

    OptionalAnnotationTypes(String... classNames) {
        ClassLoader classLoader = OptionalAnnotationTypes.class.getClassLoader();
        this.presentTypes = Arrays.stream(classNames)
                .filter(className -> ClassUtils.isPresent(className, classLoader))
                .<Class<? extends Annotation>>map(className ->
                        ClassUtils.resolveClassName(className, classLoader).asSubclass(Annotation.class))
                .toList();
    }

    boolean isAnyFoundIn(AnnotationsSupplier annotationsSupplier) {
        return presentTypes.stream()
                .anyMatch(annotationType -> annotationsSupplier
                        .findAnnotations(annotationType)
                        .findAny()
                        .isPresent());
    }
}
