package io.meshcloud.openapispecforspring.test.app69;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App69Controller {
    @PutMapping("/pets/{name}")
    @ApiResponse(responseCode = "201", description = "Created the pet")
    @ApiResponse(responseCode = "204", description = "Nothing changed")
    ResponseEntity<Pet> upsertPet(@PathVariable String name, @RequestBody Pet pet) {
        return ResponseEntity.ok(pet);
    }

    @PutMapping("/owners/{name}")
    @ApiResponse(
            responseCode = "202",
            description = "Accepted for review",
            content = @Content(mediaType = "text/plain", schema = @Schema(type = "string")))
    ResponseEntity<Owner> upsertOwner(@PathVariable String name, @RequestBody Owner owner) {
        return ResponseEntity.ok(owner);
    }

    record Pet(String name) {}

    record Owner(String name) {}
}
