package io.meshcloud.openapispecforspring.test.app43;

import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.extension.jackson.JacksonPolymorphismTypeSchemaNameBuilder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class App43Configuration {

    @Bean
    public JacksonPolymorphismTypeSchemaNameBuilder jacksonPolymorphismTypeSchemaNameBuilder(
            SchemaNameBuilder schemaNameBuilder) {
        return (caller, javaTypeOwningJsonTypeInfo, jsonTypeInfo) -> {
            String property = jsonTypeInfo.property();
            String propertyName = StringUtils.isNotBlank(property)
                    ? property
                    : jsonTypeInfo.use().getDefaultPropertyName();
            return schemaNameBuilder.buildFromType(caller, javaTypeOwningJsonTypeInfo) + propertyName;
        };
    }
}
