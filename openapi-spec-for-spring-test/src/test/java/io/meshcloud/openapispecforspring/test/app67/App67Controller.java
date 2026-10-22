package io.meshcloud.openapispecforspring.test.app67;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App67Controller {

    @GetMapping("/vehicles/default")
    Vehicle vehicle() {
        return new Car(4);
    }

    @GetMapping("/animals/default")
    Animal animal() {
        return new Cat(true);
    }

    sealed interface Vehicle permits Car, Bike {}

    record Car(int doors) implements Vehicle {}

    record Bike(boolean electric) implements Vehicle {}

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
    sealed interface Animal permits Cat, Dog {}

    @JsonTypeName("cat")
    record Cat(boolean indoor) implements Animal {}

    record Dog(String breed) implements Animal {}
}
