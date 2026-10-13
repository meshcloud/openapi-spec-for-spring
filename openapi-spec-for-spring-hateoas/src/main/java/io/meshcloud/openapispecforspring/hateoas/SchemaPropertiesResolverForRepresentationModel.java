/*-
 * #%L
 * OpenAPI Spec for Spring :: HATEOAS
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

package io.meshcloud.openapispecforspring.hateoas;

import static io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier.Purpose.SCHEMA_BUILDING;
import static io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils.laterThan;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.SchemaPropertiesResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.SchemaProperty;
import io.meshcloud.openapispecforspring.common.schema.resolver.properties.SchemaPropertyFilter;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Links;
import org.springframework.hateoas.RepresentationModel;

/**
 * Keep in step with {@link org.springframework.hateoas.mediatype.hal.RepresentationModelMixin}.
 */
@RequiredArgsConstructor
public class SchemaPropertiesResolverForRepresentationModel implements SchemaPropertiesResolver, SchemaPropertyFilter {

    public static final int ORDER = laterThan(DEFAULT_ORDER);

    private final OpenApiObjectMapperSupplier objectMapperSupplier;

    @Override
    public boolean accept(
            SchemaResolver.Caller caller,
            BeanPropertyDefinition property,
            BeanDescription beanDescriptionForType,
            AnnotationsSupplier annotationsSupplierForType,
            MapperConfig<?> mapperConfig) {
        AnnotatedMember member = property.getPrimaryMember();
        return member == null || member.getDeclaringClass() != RepresentationModel.class;
    }

    @Override
    public Map<String, SchemaProperty> findProperties(
            SchemaResolver.Caller caller, JavaType javaType, AnnotationsSupplier annotationsSupplier) {
        if (!RepresentationModel.class.isAssignableFrom(javaType.getRawClass())) {
            return Map.of();
        }
        JavaType linksType = objectMapperSupplier.get(SCHEMA_BUILDING).constructType(Links.class);
        return Map.of("_links", new SchemaProperty() {
            @Override
            public JavaType getType() {
                return linksType;
            }

            @Override
            public AnnotationsSupplier getAnnotationsSupplier() {
                return AnnotationsSupplier.EMPTY;
            }
        });
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
