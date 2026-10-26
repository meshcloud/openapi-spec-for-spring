package io.meshcloud.openapispecforspring.test.app64;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/compositions")
class App64CompositionController {

    @GetMapping("/setting")
    App64Setting getSetting() {
        throw new UnsupportedOperationException();
    }

    @PostMapping("/setting")
    void saveSetting(@RequestBody App64Setting setting) {}
}
