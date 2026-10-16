package io.meshcloud.openapispecforspring.test.app87;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App87Controller {

    @GetMapping("/setting")
    App87Setting getSetting() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/setting")
    void saveSetting(@RequestBody App87Setting setting) {}
}
