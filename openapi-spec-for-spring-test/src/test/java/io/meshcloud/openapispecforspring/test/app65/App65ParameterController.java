package io.meshcloud.openapispecforspring.test.app65;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parameters")
class App65ParameterController {
    static final String PARCEL_V1 = "application/vnd.parcel.v1+json";
    static final String PARCEL_V2 = "application/vnd.parcel.v2+json";

    @GetMapping(path = "/parcels/{parcelId}", produces = PARCEL_V1)
    String parcelV1(
            @PathVariable("parcelId") String trackingCode,
            @Parameter(deprecated = true) @RequestParam(required = false) String carrier,
            @RequestParam(required = false) String note) {
        return trackingCode + carrier + note;
    }

    @GetMapping(path = "/parcels/{parcelId}", produces = PARCEL_V2)
    @SuppressWarnings({"deprecation", "DeprecatedVariable"})
    String parcelV2(
            @Parameter(description = "The ID of the parcel.") @PathVariable UUID parcelId,
            @Deprecated @RequestParam(required = false) String carrier,
            @Parameter(deprecated = true) @RequestParam(required = false) String note) {
        return parcelId + carrier + note;
    }

    @GetMapping(path = "/depots/{depotId}", produces = PARCEL_V1)
    String depotV1(
            @Parameter(description = "The code of the depot.", example = "BER-1") @PathVariable("depotId") String code,
            @RequestParam List<String> tags) {
        return code + tags;
    }

    @GetMapping(path = "/depots/{depotId}", produces = PARCEL_V2)
    String depotV2(
            @Parameter(description = "The code of the depot.", example = "BER-1") @PathVariable String depotId,
            @RequestParam List<String> tags) {
        return depotId + tags;
    }
}
