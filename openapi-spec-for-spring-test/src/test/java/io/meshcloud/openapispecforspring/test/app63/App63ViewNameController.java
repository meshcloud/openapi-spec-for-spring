package io.meshcloud.openapispecforspring.test.app63;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/view-names")
class App63ViewNameController {

    @GetMapping("/seed")
    App63Seed getSeed() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/seed")
    void sow(@RequestBody App63Seed seed) {}

    @PostMapping("/seeds")
    void order(@RequestBody App63SeedRequest request) {}
}
