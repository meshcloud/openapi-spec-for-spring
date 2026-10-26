package io.meshcloud.openapispecforspring.test.app60;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/distinct-descriptions")
class App60DistinctDescriptionController {

    @GetMapping("/visits")
    Visit visit() {
        return new Visit(new PetRef("rex", "pet"), new OwnerRef("ann", "owner"));
    }

    record PetRef(
            @Schema(description = "The name of the pet") String name,
            @Schema(description = "Always pet") String kind) {}

    record OwnerRef(
            @Schema(description = "The name of the owner") String name,
            @Schema(description = "Always owner") String kind) {}

    record Visit(PetRef pet, OwnerRef owner) {}
}
