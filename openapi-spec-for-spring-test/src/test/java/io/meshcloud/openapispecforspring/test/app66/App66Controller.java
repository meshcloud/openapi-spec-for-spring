package io.meshcloud.openapispecforspring.test.app66;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App66Controller {

    @GetMapping(path = "/kennels/{kennelId}", produces = "application/vnd.kennel.v1+json")
    String kennelV1(@Parameter(description = "The number of the kennel.") @PathVariable String kennelId) {
        return kennelId;
    }

    @GetMapping(path = "/kennels/{kennelId}", produces = "application/vnd.kennel.v2+json")
    String kennelV2(@Parameter(description = "The name of the kennel.") @PathVariable String kennelId) {
        return kennelId;
    }
}
