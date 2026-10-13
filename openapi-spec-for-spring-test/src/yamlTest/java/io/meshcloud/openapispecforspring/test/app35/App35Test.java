package io.meshcloud.openapispecforspring.test.app35;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebMvcBaseIntTest;
import org.junit.jupiter.api.Test;

class App35Test extends AbstractOpenApiSpecWebMvcBaseIntTest {

    @Test
    void getOpenApiAsJson_withAcceptTextHtml() throws Exception {
        performApiDocsRequest(x -> x.accept("text/html")).andExpect(status().isNotAcceptable());
    }

    @Test
    void getOpenApiAsJson_withAcceptApplicationYaml() throws Exception {
        assertResponseBodyMatchesOpenApiYaml("app35.yaml", performApiDocsRequest(x -> x.accept("application/yaml")));
    }

    @Test
    void getOpenApiAsJson_withAcceptApplicationXYaml() throws Exception {
        assertResponseBodyMatchesOpenApiYaml("app35.yaml", performApiDocsRequest(x -> x.accept("application/x-yaml")));
    }

    @Test
    void getOpenApiAsJson_withAcceptTextYaml() throws Exception {
        assertResponseBodyMatchesOpenApiYaml("app35.yaml", performApiDocsRequest(x -> x.accept("text/yaml")));
    }

    @Test
    void getOpenApiAsJson_withAcceptTextXYaml() throws Exception {
        assertResponseBodyMatchesOpenApiYaml("app35.yaml", performApiDocsRequest(x -> x.accept("text/x-yaml")));
    }

    @Test
    void getOpenApiAsJson_withAcceptTextVndYaml() throws Exception {
        assertResponseBodyMatchesOpenApiYaml("app35.yaml", performApiDocsRequest(x -> x.accept("text/vnd.yaml")));
    }
}
