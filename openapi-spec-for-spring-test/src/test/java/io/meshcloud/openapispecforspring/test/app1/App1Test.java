package io.meshcloud.openapispecforspring.test.app1;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebMvcIntTest;
import org.junit.jupiter.api.Test;

class App1Test extends AbstractOpenApiSpecWebMvcIntTest {

    @Test
    void getOpenApiAsYaml_withoutYamlOnClasspath_isNotAcceptable() throws Exception {
        performApiDocsRequest(x -> x.accept("application/yaml")).andExpect(status().isNotAcceptable());
    }
}
