package io.meshcloud.openapispecforspring.test.app9;

import io.meshcloud.openapispecforspring.common.reference.component.requestbody.ReferenceIdentifierBuilderForRequestBody;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class App9Configuration {
    @Bean
    public ReferenceIdentifierBuilderForRequestBody referenceIdentifierFactoryForRequestBody() {
        return (item, suggestedIdentifier, numberOfSetters) -> "RequestBody";
    }
}
