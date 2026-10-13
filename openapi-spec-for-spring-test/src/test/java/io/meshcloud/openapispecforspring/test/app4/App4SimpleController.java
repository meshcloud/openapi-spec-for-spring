package io.meshcloud.openapispecforspring.test.app4;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App4SimpleController {
    @PostMapping(value = "/post1", produces = "text/plain")
    @ApiResponse(description = "Default response for post mapping")
    @Schema(implementation = String.class)
    public List<String> postMapping1() {
        return null;
    }
}
