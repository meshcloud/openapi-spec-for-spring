package io.meshcloud.openapispecforspring.test.app60;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/patterns")
class App60PatternController {
    @GetMapping("/ponds")
    Pond getPond() {
        return new Pond("P-001", "P-002", "Lily pond");
    }

    record Pond(
            @Schema(pattern = "^P-[0-9]{3}$") String id,
            @Schema(pattern = "^P-[0-9]{3}$") String neighbourId,
            String name) {}
}
