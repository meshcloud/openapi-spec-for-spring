package io.meshcloud.openapispecforspring.test.app2;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "deprecated-methods")
class App2ControllerWithDeprecatedMethods {

    @GetMapping("get-mapping")
    @Deprecated
    @SuppressWarnings("InlineMeSuggester")
    public String deprecatedGetMapping() {
        return null;
    }

    @PostMapping("post-mapping")
    @Operation(deprecated = true)
    public void deprecatedPostMapping() {}
}
