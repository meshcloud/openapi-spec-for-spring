package io.meshcloud.openapispecforspring.test.app36;

import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebMvcBaseIntTest;
import org.junit.jupiter.api.Test;

class App36Test extends AbstractOpenApiSpecWebMvcBaseIntTest {

    @Test
    void getOpenApiAsJson() throws Exception {
        assertResponseBodyMatchesOpenApiYaml("app36.yaml", performApiDocsRequest(x -> x.accept("application/yaml")));
    }
}
