package io.meshcloud.openapispecforspring.test.app64;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/discriminators")
class App64DiscriminatorController {

    @GetMapping("/garages/default")
    Garage garage() {
        return new Garage(new Car(4), VehicleType.CAR);
    }

    enum VehicleType {
        CAR,
        BIKE
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Car.class, name = "car"),
        @JsonSubTypes.Type(value = Bike.class, name = "bike")
    })
    sealed interface Vehicle permits Car, Bike {}

    record Car(int doors) implements Vehicle {}

    record Bike(boolean electric) implements Vehicle {}

    record Garage(Vehicle vehicle, VehicleType preferredType) {}
}
