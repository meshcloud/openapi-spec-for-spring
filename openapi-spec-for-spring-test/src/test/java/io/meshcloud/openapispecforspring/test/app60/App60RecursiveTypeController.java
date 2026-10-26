package io.meshcloud.openapispecforspring.test.app60;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recursive-types")
class App60RecursiveTypeController {
    @GetMapping("/garden")
    Garden getGarden() {
        return new Garden(null, null);
    }

    record Garden(OakGrove oaks, BirchGrove birches) {}

    record OakGrove(@Schema(description = "The oldest oak") Tree tree) {}

    record BirchGrove(
            @Schema(description = "The tallest birch") Tree tree) {}

    record Tree(String species, List<Tree> saplings) {}
}
