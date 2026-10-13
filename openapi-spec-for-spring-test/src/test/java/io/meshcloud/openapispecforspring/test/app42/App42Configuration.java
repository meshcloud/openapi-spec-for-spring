package io.meshcloud.openapispecforspring.test.app42;

import io.meshcloud.openapispecforspring.common.schema.customizer.SchemaPropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class App42Configuration {

    @Bean
    public SchemaPropertiesCustomizer schemaPropertiesCustomizerForMyRequiredAnnotation() {
        return (schema, javaType, annotationsSupplier, properties) ->
                properties.forEach((propertyName, property) -> property.customize(
                        (propertySchema, propertyType, propertyAnnotationsSupplier) -> propertyAnnotationsSupplier
                                .findAnnotations(MyRequiredAnnotation.class)
                                .findFirst()
                                .ifPresent(ignored -> schema.addRequired(propertyName))));
    }
}
