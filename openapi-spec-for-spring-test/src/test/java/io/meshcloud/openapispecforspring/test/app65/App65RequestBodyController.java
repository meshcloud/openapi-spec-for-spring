package io.meshcloud.openapispecforspring.test.app65;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/request-bodies")
class App65RequestBodyController {
    static final String ORDER_V1 = "application/vnd.example.order.v1+json";
    static final String ORDER_V2 = "application/vnd.example.order.v2+json";

    @PostMapping(path = "/orders", consumes = ORDER_V1)
    void createOrderV1(@RequestBody OrderV1 order, @RequestParam boolean dryRun) {}

    @PostMapping(path = "/orders", consumes = ORDER_V2)
    void createOrderV2(@RequestBody OrderV2 order, @RequestParam boolean dryRun) {}

    record OrderV1(String item) {}

    record OrderV2(String item, int quantity) {}
}
