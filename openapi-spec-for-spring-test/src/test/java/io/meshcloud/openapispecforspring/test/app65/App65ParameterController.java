package io.meshcloud.openapispecforspring.test.app65;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parameters")
class App65ParameterController {
    static final String PARCEL_V1 = "application/vnd.parcel.v1+json";
    static final String PARCEL_V2 = "application/vnd.parcel.v2+json";

    @GetMapping(path = "/parcels/{parcelId}", produces = PARCEL_V1)
    String parcelV1(
            @Parameter(description = "In this version, the tracking code of the parcel.") @PathVariable("parcelId")
                    String trackingCode) {
        return trackingCode;
    }

    @GetMapping(path = "/parcels/{parcelId}", produces = PARCEL_V2)
    String parcelV2(@Parameter(description = "The UUID of the parcel.") @PathVariable UUID parcelId) {
        return parcelId.toString();
    }

    @GetMapping(path = "/depots/{depotId}", produces = PARCEL_V1)
    String depotV1(@Parameter(description = "The code of the depot.") @PathVariable("depotId") String code) {
        return code;
    }

    @GetMapping(path = "/depots/{depotId}", produces = PARCEL_V2)
    String depotV2(@Parameter(description = "The code of the depot.") @PathVariable String depotId) {
        return depotId;
    }
}
