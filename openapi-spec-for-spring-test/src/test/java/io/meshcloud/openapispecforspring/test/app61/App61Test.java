package io.meshcloud.openapispecforspring.test.app61;

import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebMvcIntTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(
        properties = {
            "spring.data.web.pageable.one-indexed-parameters=true",
            "spring.data.web.pageable.max-page-size=100"
        })
class App61Test extends AbstractOpenApiSpecWebMvcIntTest {}
