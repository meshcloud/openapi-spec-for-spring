package io.meshcloud.openapispecforspring.test.app75;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App75Controller {
    @GetMapping("/zoo")
    Zoo getZoo() {
        return new Zoo(Map.of(), Map.of(), Map.of(), Map.of(), Map.of());
    }

    record Zoo(
            @Schema(description = "Animals by their name") Map<String, Animal> animalsByName,

            @Schema(description = "Animals by their enclosure")
            Map<String, Animal> animalsByEnclosure,

            Map<Enclosure, Animal> animalsPerEnclosure,
            Map<String, List<Animal>> animalsBySpecies,

            @Schema(description = "Keepers by shift", additionalPropertiesSchema = Keeper.class)
            Map<String, Object> keepersByShift) {}

    @Schema(description = "An animal of the zoo")
    record Animal(String name) {}

    record Keeper(String name) {}

    enum Enclosure {
        SAVANNA,
        @JsonProperty("rain-forest")
        RAIN_FOREST
    }
}
