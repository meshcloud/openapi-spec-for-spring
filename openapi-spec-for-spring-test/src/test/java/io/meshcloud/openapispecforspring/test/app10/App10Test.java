package io.meshcloud.openapispecforspring.test.app10;

import static io.meshcloud.openapispecforspring.test.OpenApiJsonIntegrationTestUtils.findResourceFilenameFromTestClass;

import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebMvcBaseIntTest;
import org.junit.jupiter.api.Test;

class App10Test extends AbstractOpenApiSpecWebMvcBaseIntTest {

    @Test
    void testWithQueryParam() throws Exception {
        assertResponseBodyMatchesOpenApiJson(
                findResourceFilenameFromTestClass(getClass(), "_admin.json"),
                performApiDocsRequest(x -> x.queryParam("pathPrefix", "/admin")));
    }

    @Test
    void testWithHeaderParam() throws Exception {
        assertResponseBodyMatchesOpenApiJson(
                findResourceFilenameFromTestClass(getClass(), "_user.json"),
                performApiDocsRequest(x -> x.header("X-Path-Prefix", "/user")));
    }
}
