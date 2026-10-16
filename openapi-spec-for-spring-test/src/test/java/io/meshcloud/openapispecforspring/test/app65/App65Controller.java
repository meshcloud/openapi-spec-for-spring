package io.meshcloud.openapispecforspring.test.app65;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App65Controller {

    @GetMapping("/pets")
    List<String> pets(Pageable pageable) {
        return List.of();
    }

    @GetMapping("/toys")
    List<String> toys(@RequestParam String color, @PageableDefault(size = 50, page = 2) Pageable pageable) {
        return List.of();
    }
}
