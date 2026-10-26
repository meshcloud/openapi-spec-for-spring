package io.meshcloud.openapispecforspring.test.app59;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/raw-response-entity")
class App59RawResponseEntityController {
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
