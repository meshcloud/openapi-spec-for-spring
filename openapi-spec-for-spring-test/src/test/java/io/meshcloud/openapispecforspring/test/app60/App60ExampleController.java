package io.meshcloud.openapispecforspring.test.app60;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/examples")
class App60ExampleController {
    @GetMapping("/kennel")
    Kennel getKennel() {
        return new Kennel("123456789012", "{\"size\": \"large\"}", List.of("rex"));
    }

    record Kennel(
            @Schema(example = "123456789012") String licenseNumber,
            @Schema(example = "{\"size\": \"large\"}") String rawLabel,
            @Schema(example = "[\"rex\", \"bello\"]") List<String> dogNames) {}
}
