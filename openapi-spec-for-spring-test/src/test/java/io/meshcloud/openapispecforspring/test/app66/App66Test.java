package io.meshcloud.openapispecforspring.test.app66;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebMvcBaseIntTest;
import org.junit.jupiter.api.Test;

class App66Test extends AbstractOpenApiSpecWebMvcBaseIntTest {

    @Test
    void failsWhenMergedParametersHaveDifferentDescriptions() {
        assertThatThrownBy(() -> performApiDocsRequest(x -> x))
                .rootCause()
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot merge the parameters named kennelId in path of GET /kennels/{kennelId}"
                        + " from the handler methods kennelV1, kennelV2, because their values of description"
                        + " differ: [The number of the kennel., The name of the kennel.]");
    }
}
