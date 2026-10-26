package io.meshcloud.openapispecforspring.test.app64;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/empty-objects")
class App64EmptyObjectController {

    @GetMapping("/feeding")
    App64Feeding getFeeding() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/feeding")
    void saveFeeding(@RequestBody App64Feeding feeding) {}
}
