package io.meshcloud.openapispecforspring.test.app62;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App62Controller {

    @GetMapping("/plants")
    List<String> plants(Pageable pageable) {
        return List.of();
    }

    @GetMapping("/seeds")
    List<String> seeds(@PageableDefault(size = 50) Pageable pageable) {
        return List.of();
    }
}
