package io.meshcloud.openapispecforspring.test.app62;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App62Controller {
    @GetMapping("/shapes/default")
    Shape shape() {
        return new Circle(1.0);
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
    @JsonSubTypes({@JsonSubTypes.Type(value = Circle.class, name = "circle")})
    interface Shape {
        String getType();
    }

    record Circle(double radius) implements Shape {
        @Override
        public String getType() {
            return "circle";
        }
    }
}
