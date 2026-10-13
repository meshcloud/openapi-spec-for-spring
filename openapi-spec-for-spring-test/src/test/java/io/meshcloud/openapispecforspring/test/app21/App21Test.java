package io.meshcloud.openapispecforspring.test.app21;

import static io.meshcloud.openapispecforspring.test.OpenApiJsonIntegrationTestUtils.assertMatchesOpenApiJson;
import static org.assertj.core.api.Assertions.assertThat;

import io.meshcloud.openapispecforspring.common.OpenApiConfigurationProperties;
import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebIntTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
        properties = {
            "openapi-spec-for-spring.api-docs-path=" + App21Test.PATH_TO_API_DOCS,
            "server.servlet.context-path=/my-context-path",
            "server.forward-headers-strategy = framework"
        })
class App21Test extends AbstractOpenApiSpecWebIntTest {
    static final String PATH_TO_API_DOCS = "/my/own-path/to-api-docs";

    @Autowired
    private OpenApiConfigurationProperties properties;

    @Test
    void testWithCustomApiDocsPath() throws Exception {
        assertMatchesOpenApiJson("app21.json", () -> getResponseBody(PATH_TO_API_DOCS)
                .replace(buildHost(), "http://localhost/my-context-path"));
    }

    @Test
    void testConfigurationPropertyIsSet() {
        // the controller's @RequestMapping resolves the path via SpEL from this property
        assertThat(properties.getApiDocsPath()).isEqualTo(PATH_TO_API_DOCS);
    }
}
