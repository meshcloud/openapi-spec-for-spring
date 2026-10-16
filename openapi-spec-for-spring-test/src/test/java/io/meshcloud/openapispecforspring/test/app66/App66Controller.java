package io.meshcloud.openapispecforspring.test.app66;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App66Controller {

    @GetMapping("/pets/{id:[0-9a-f\\-]{36}}/toys/{name}")
    String toy(@PathVariable String id, @PathVariable String name) {
        return name;
    }
}
