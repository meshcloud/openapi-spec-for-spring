package io.meshcloud.openapispecforspring.test.app65;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/optional-parameters")
class App65OptionalParameterController {

    @GetMapping("/books")
    List<String> books(
            @RequestParam String shelf,
            @RequestParam Optional<String> author,
            @RequestParam @Nullable String title,
            @RequestParam(defaultValue = "10") int limit,
            @RequestHeader("X-Library") String library,
            @RequestHeader("X-Reader") Optional<String> reader) {
        return List.of();
    }

    @GetMapping(value = "/shelves", produces = "application/json")
    List<String> shelvesAsJson(@RequestParam Optional<String> genre) {
        return List.of();
    }

    @GetMapping(value = "/shelves", produces = "text/plain")
    String shelvesAsText(@RequestParam Optional<String> genre) {
        return "";
    }
}
