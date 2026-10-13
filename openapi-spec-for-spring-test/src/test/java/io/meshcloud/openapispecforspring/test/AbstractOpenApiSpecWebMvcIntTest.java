package io.meshcloud.openapispecforspring.test;

import static io.meshcloud.openapispecforspring.test.OpenApiJsonIntegrationTestUtils.findResourceFilenameFromTestClass;

import org.junit.jupiter.api.Test;

public abstract class AbstractOpenApiSpecWebMvcIntTest extends AbstractOpenApiSpecWebMvcBaseIntTest {

    private final String expectedJsonFile = findResourceFilenameFromTestClass(getClass(), ".json");

    @Test
    public void getOpenApiAsJson() throws Exception {
        assertResponseBodyMatchesOpenApiJson(expectedJsonFile, performApiDocsRequest(x -> x));
    }
}
