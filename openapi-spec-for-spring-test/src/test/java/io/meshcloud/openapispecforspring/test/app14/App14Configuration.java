package io.meshcloud.openapispecforspring.test.app14;

import io.meshcloud.openapispecforspring.common.reference.component.schema.ReferenceDeciderForSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class App14Configuration {
    @Bean
    public ReferenceDeciderForSchema referenceDeciderForSchema() {
        return (schema, referenceIdentifier, numberOfUsages) -> false;
    }
}
