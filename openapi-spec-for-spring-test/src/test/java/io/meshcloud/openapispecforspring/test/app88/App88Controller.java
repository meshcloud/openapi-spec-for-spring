package io.meshcloud.openapispecforspring.test.app88;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App88Controller {

    @GetMapping("/pet")
    App88Pet getPet() {
        throw new UnsupportedOperationException();
    }
}
