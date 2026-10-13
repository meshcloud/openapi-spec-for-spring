package io.meshcloud.openapispecforspring.test.app70;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App70Controller {
    @GetMapping("/aquarium")
    Aquarium getAquarium() {
        return new Aquarium(3, 40, 7);
    }

    record Aquarium(
            @Schema(maximum = "10") int fishCount,

            @Schema(minimum = "0", maximum = "100", exclusiveMaximum = true)
            int temperatureCelsius,

            int plantCount) {}
}
