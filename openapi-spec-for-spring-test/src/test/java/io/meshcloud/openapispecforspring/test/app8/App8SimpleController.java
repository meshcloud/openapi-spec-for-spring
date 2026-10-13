package io.meshcloud.openapispecforspring.test.app8;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "simple")
class App8SimpleController {

    @GetMapping("get-instant")
    public Instant getMappingReturnInstant() {
        return null;
    }

    @GetMapping("get-instant-2/{pathParam}")
    public Instant getMappingReturnInstantWithParameter(
            @Schema(name = "inputParameter") @RequestParam(required = false) Instant inputParameter1) {
        return null;
    }

    @GetMapping("get-response-entity-instant")
    @Schema(name = "responseEntitySchema")
    public ResponseEntity<Instant> getResponseEntityOfInstant() {
        return null;
    }
}
