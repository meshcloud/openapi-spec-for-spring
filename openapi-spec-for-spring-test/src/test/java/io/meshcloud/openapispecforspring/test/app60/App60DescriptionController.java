package io.meshcloud.openapispecforspring.test.app60;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/descriptions")
class App60DescriptionController {

    @GetMapping("/pets/default")
    Pet pet() {
        return new Pet(Color.BLACK, Color.WHITE, "Rex", "Rexy", new Address("Main St"), new Address("Side St"));
    }

    enum Color {
        BLACK,
        WHITE
    }

    @Schema(description = "A postal address")
    record Address(String street) {}

    record Pet(
            @Schema(description = "The main color") Color mainColor,
            @Schema(description = "The accent color") Color accentColor,
            @Schema(description = "A name of the pet") String name,
            @Schema(description = "A name of the pet") String nickname,

            @Schema(description = "Where the pet lives", nullable = true)
            Address home,

            Address vet) {}
}
