package io.meshcloud.openapispecforspring.test.app72;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App72Controller {

    @GetMapping("/pets/{id:[0-9a-f]{8}}")
    Pet pet(@PathVariable String id) {
        return new Pet("rex", "max", 3, 4);
    }

    @GetMapping("/owners/{id:[0-9a-f]{8}}")
    Pet owner(@PathVariable String id) {
        return new Pet("ann", "bob", 30, 40);
    }

    record Pet(
            @Pattern(regexp = "[a-z]+") String name,
            @Pattern(regexp = "[a-z]+") String nickname,
            @Min(0) int age,
            @Min(0) int weight) {}
}
