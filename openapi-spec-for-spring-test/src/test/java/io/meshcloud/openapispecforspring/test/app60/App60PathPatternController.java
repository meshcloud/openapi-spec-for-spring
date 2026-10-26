package io.meshcloud.openapispecforspring.test.app60;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/path-patterns")
class App60PathPatternController {

    @GetMapping("/pets/{id:[0-9a-f\\-]{36}}/toys/{name}")
    String toy(@PathVariable String id, @PathVariable String name) {
        return name;
    }
}
