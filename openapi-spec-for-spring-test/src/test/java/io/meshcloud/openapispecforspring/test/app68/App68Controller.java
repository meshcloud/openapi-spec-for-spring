package io.meshcloud.openapispecforspring.test.app68;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App68Controller {
    @SuppressWarnings("rawtypes")
    @GetMapping("/raw")
    ResponseEntity getWithRawResponseEntity() {
        return ResponseEntity.ok("raw");
    }

    @SuppressWarnings("rawtypes")
    @GetMapping(path = "/raw-again", produces = "text/plain")
    ResponseEntity getAgainWithRawResponseEntity() {
        return ResponseEntity.ok("raw");
    }
}
