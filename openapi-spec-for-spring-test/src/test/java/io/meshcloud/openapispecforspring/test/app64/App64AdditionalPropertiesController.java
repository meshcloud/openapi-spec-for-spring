package io.meshcloud.openapispecforspring.test.app64;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/additional-properties")
class App64AdditionalPropertiesController {

    @GetMapping("/pet")
    App64Pet getPet() {
        throw new UnsupportedOperationException();
    }
}
