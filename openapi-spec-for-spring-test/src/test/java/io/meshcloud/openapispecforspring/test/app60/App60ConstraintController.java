package io.meshcloud.openapispecforspring.test.app60;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/constraints")
class App60ConstraintController {

    @GetMapping("/pets/{id:[0-9a-f]{8}}")
    Pet constrainedPet(@PathVariable String id) {
        return new Pet("rex", "max", 3, 4);
    }

    @GetMapping("/owners/{id:[0-9a-f]{8}}")
    Pet constrainedOwner(@PathVariable String id) {
        return new Pet("ann", "bob", 30, 40);
    }

    record Pet(
            @Pattern(regexp = "[a-z]+") String name,
            @Pattern(regexp = "[a-z]+") String nickname,
            @Min(0) int age,
            @Min(0) int weight) {}
}
