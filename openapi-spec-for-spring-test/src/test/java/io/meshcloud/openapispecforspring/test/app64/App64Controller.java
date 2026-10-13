package io.meshcloud.openapispecforspring.test.app64;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App64Controller {

    @PostMapping("/pets")
    Pet create(@RequestBody Pet pet) {
        return pet;
    }

    record Pet(@JsonProperty(required = true) String name, String nickname) {}
}
