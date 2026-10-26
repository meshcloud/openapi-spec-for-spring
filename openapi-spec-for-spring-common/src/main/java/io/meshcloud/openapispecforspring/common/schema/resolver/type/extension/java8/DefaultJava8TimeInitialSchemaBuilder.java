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

package io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.java8;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS;
import static io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.java8.Java8TimeTypeResolverConfigurationProperties.Format.INFER_FROM_OBJECT_MAPPER;
import static io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.java8.Java8TimeTypeResolverConfigurationProperties.Format.ISO8601;
import static io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.java8.Java8TimeTypeResolverConfigurationProperties.Format.UNIX_EPOCH_SECONDS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.java8.Java8TimeTypeResolverConfigurationProperties.Format;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.model.media.Schema;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
public class DefaultJava8TimeInitialSchemaBuilder implements Java8TimeInitialSchemaBuilder {

    public static final int ORDER = DEFAULT_ORDER;

    private static final String STRING = "string";
    private static final String DATE_TIME = "date-time";

    // In timestamp mode Jackson writes these as arrays or numbers, which needs its own schemas (issue #4).
    private static final Map<Class<?>, Optional<String>> ISO_DATE_FORMATS = Map.of(
            LocalDate.class, Optional.of("date"),
            LocalDateTime.class, Optional.of(DATE_TIME),
            OffsetDateTime.class, Optional.of(DATE_TIME),
            ZonedDateTime.class, Optional.of(DATE_TIME),
            LocalTime.class, Optional.empty(),
            OffsetTime.class, Optional.empty(),
            YearMonth.class, Optional.empty(),
            MonthDay.class, Optional.empty(),
            Year.class, Optional.empty());

    private final Java8TimeTypeResolverConfigurationProperties properties;

    @Nullable
    private final ObjectMapper objectMapper;

    @Nullable
    @Override
    public Schema buildFromType(SchemaResolver.Caller caller, InitialType initialType) {
        Class<?> rawClass = initialType.getType().getRawClass();
        if (rawClass.equals(Instant.class)) {
            Format format = getFormat(WRITE_DATES_AS_TIMESTAMPS);
            Schema.SchemaBuilder schemaBuilder = createSchemaBuilderWithType(format);
            if (format == ISO8601) {
                return schemaBuilder.format(DATE_TIME).build();
            }
            return schemaBuilder.build();
        } else if (rawClass.equals(Duration.class)) {
            Format format = getFormat(WRITE_DURATIONS_AS_TIMESTAMPS);
            Schema.SchemaBuilder schemaBuilder = createSchemaBuilderWithType(format);
            if (format == ISO8601) {
                return schemaBuilder.format("duration").build();
            }
            return schemaBuilder.build();
        } else if (rawClass.equals(Period.class)) {
            return Schema.builder().type(STRING).format("duration").build();
        } else if (ZoneId.class.isAssignableFrom(rawClass)) {
            return Schema.builder().type(STRING).build();
        } else if (ISO_DATE_FORMATS.containsKey(rawClass) && getFormat(WRITE_DATES_AS_TIMESTAMPS) == ISO8601) {
            Schema.SchemaBuilder schemaBuilder = Schema.builder().type(STRING);
            ISO_DATE_FORMATS.get(rawClass).ifPresent(schemaBuilder::format);
            return schemaBuilder.build();
        }
        return null;
    }

    private Format getFormat(SerializationFeature serializationFeature) {
        if (properties.getFormat() == INFER_FROM_OBJECT_MAPPER) {
            if (objectMapper == null) {
                throw new IllegalStateException(
                        "Cannot infer format from object mapper without object mapper being present");
            }
            return objectMapper.isEnabled(serializationFeature) ? UNIX_EPOCH_SECONDS : ISO8601;
        }
        return properties.getFormat();
    }

    private static Schema.SchemaBuilder createSchemaBuilderWithType(Format format) {
        if (format == ISO8601) {
            return Schema.builder().type(STRING);
        } else if (format == UNIX_EPOCH_SECONDS) {
            return Schema.builder().type("number").format("int64");
        }
        throw new IllegalArgumentException("Unsupported Java8 Time Format " + format);
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
