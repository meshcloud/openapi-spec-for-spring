package io.meshcloud.openapispecforspring.test.app89;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App89Controller {

    @GetMapping("/feeding")
    App89Feeding getFeeding() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/feeding")
    void saveFeeding(@RequestBody App89Feeding feeding) {}
}
