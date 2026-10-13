package io.meshcloud.openapispecforspring.test.app24;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebMvcBaseIntTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "openapi-spec-for-spring.enabled=false")
class App24Test extends AbstractOpenApiSpecWebMvcBaseIntTest {

    @Test
    void testOpenApiIsNotFoundWhenDisabled() throws Exception {
        performApiDocsRequest(x -> x).andExpect(status().isNotFound());
    }
}
