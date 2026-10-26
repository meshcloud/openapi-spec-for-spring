/*-
 * #%L
 * OpenAPI Spec for Spring :: Model
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

package io.meshcloud.openapispecforspring.model.media;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.meshcloud.openapispecforspring.model.ExternalDocumentation;
import io.meshcloud.openapispecforspring.model.trait.HasExtensions;
import io.meshcloud.openapispecforspring.model.trait.HasIsEmpty;
import io.meshcloud.openapispecforspring.model.trait.HasReference;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.With;

/**
 * Schema
 *
 * @see "https://github.com/OAI/OpenAPI-Specification/blob/3.0.1/versions/3.0.1.md#schemaObject"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Schema implements HasExtensions, HasReference<Schema>, HasIsEmpty<Schema> {
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private String name;

    // A type can have a request and a response view that differ, e.g. in their required properties; this tells the
    // two apart when they need distinct names.
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private Direction direction;

    // Facets that a property puts on this schema. They count for the equality of the schema that owns the property,
    // so two objects whose properties differ only in their descriptions stay two components.
    @JsonIgnore
    @With
    private Schema useFacets;

    @JsonProperty("default")
    private Object defaultValue;

    private String title;
    private BigDecimal multipleOf;
    private BigDecimal maximum;
    private Boolean exclusiveMaximum;
    private BigDecimal minimum;
    private Boolean exclusiveMinimum;
    private Integer maxLength;
    private Integer minLength;
    private String pattern;
    private Integer maxItems;
    private Integer minItems;
    private Boolean uniqueItems;
    private Integer maxProperties;
    private Integer minProperties;
    private List<String> required;

    @With
    private String type;

    private Schema not;
    private Map<String, Schema> properties;
    private Object additionalProperties;
    private String description;
    private String format;
    private String ref;
    private Boolean nullable;
    private Boolean readOnly;
    private Boolean writeOnly;
    private Object example;
    private ExternalDocumentation externalDocs;
    private Boolean deprecated;
    private XML xml;

    @JsonProperty("enum")
    private List<Object> enumValues;

    private Discriminator discriminator;
    private Schema items;
    private List<Schema> allOf;
    private List<Schema> anyOf;
    private List<Schema> oneOf;
    private Map<String, Object> extensions;

    public void setProperty(String propertyName, Schema propertySchema) {
        if (properties == null) {
            properties = new LinkedHashMap<>();
        }
        properties.put(propertyName, propertySchema);
    }

    public void addRequired(String requiredProperty) {
        if (required == null) {
            required = new ArrayList<>();
        }
        if (!required.contains(requiredProperty)) {
            required.add(requiredProperty);
        }
    }

    @Override
    public Schema createInstance() {
        return new Schema();
    }

    public enum Direction {
        REQUEST,
        RESPONSE
    }
}
