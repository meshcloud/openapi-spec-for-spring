package io.meshcloud.openapispecforspring.test.app81;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App81Controller {

    @GetMapping("/drawing")
    App81Drawing getDrawing() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/drawing")
    void saveDrawing(@RequestBody App81Drawing drawing) {}
}
