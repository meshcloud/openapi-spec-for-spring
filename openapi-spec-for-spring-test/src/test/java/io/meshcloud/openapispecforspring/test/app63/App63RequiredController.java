package io.meshcloud.openapispecforspring.test.app63;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/required")
class App63RequiredController {

    @PostMapping("/pets")
    Pet create(@RequestBody Pet pet) {
        return pet;
    }

    record Pet(@JsonProperty(required = true) String name, String nickname) {}
}
