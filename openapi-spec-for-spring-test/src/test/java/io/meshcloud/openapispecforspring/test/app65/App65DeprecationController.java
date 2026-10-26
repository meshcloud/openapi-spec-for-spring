package io.meshcloud.openapispecforspring.test.app65;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deprecations")
class App65DeprecationController {

    private static final String PARCEL_V1 = "application/vnd.example.parcel.v1+json";
    private static final String PARCEL_V2 = "application/vnd.example.parcel.v2+json";
    private static final String LETTER_V1 = "application/vnd.example.letter.v1+json";
    private static final String LETTER_V2 = "application/vnd.example.letter.v2+json";

    @Deprecated
    @Operation(extensions = @Extension(properties = @ExtensionProperty(name = "x-sunset", value = "2030-01-01")))
    @GetMapping(value = "/parcels", produces = PARCEL_V1)
    App65Parcel getParcelV1() {
        throw new UnsupportedOperationException();
    }

    @Operation(extensions = @Extension(properties = @ExtensionProperty(name = "x-owner", value = "shipping")))
    @GetMapping(value = "/parcels", produces = PARCEL_V2)
    App65Parcel getParcelV2() {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    @GetMapping(value = "/letters", produces = LETTER_V1)
    App65Parcel getLetterV1() {
        throw new UnsupportedOperationException();
    }

    @Operation(deprecated = true)
    @GetMapping(value = "/letters", produces = LETTER_V2)
    App65Parcel getLetterV2() {
        throw new UnsupportedOperationException();
    }

    record App65Parcel(String trackingNumber) {}
}
