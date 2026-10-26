package io.meshcloud.openapispecforspring.test.app65;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parameter-types")
class App65ParameterTypeController {
    static final String READINGS_V1 = "application/vnd.readings.v1+json";
    static final String READINGS_V2 = "application/vnd.readings.v2+json";

    @GetMapping(path = "/readings", produces = READINGS_V1)
    String readingsV1(@RequestParam String period, @RequestParam LocalDate since) {
        return period + since;
    }

    @GetMapping(path = "/readings", produces = READINGS_V2)
    String readingsV2(@RequestParam YearMonth period, @RequestParam Instant since) {
        return period.toString() + since;
    }
}
