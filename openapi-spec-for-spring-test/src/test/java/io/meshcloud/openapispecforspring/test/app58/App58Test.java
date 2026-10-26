package io.meshcloud.openapispecforspring.test.app58;

import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebMvcIntTest;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.main.lazy-initialization=true")
class App58Test extends AbstractOpenApiSpecWebMvcIntTest {}
