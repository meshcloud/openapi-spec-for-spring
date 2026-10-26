package io.meshcloud.openapispecforspring.test.app63;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/kotlin")
class App63KotlinController {

    @GetMapping("/plant")
    App63Plant getPlant() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/plant")
    void plant(@RequestBody App63Plant plant) {}
}
