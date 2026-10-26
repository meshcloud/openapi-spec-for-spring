package io.meshcloud.openapispecforspring.test.app58;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App58Controller {
    @GetMapping("/greeting")
    String greeting() {
        return "hello";
    }
}
