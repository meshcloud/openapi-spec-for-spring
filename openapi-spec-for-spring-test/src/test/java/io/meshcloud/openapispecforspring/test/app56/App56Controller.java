package io.meshcloud.openapispecforspring.test.app56;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App56Controller {

    @GetMapping("dto")
    public SomeDto getDto() {
        return new SomeDto("some-value");
    }

    @PostMapping("dto")
    public void postDto(@RequestBody SomeDto someDto) {}

    @PostMapping("raw")
    @Operation(
            requestBody =
                    @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            content = @Content(schema = @Schema(implementation = SomeDto.class))))
    public void postRaw(HttpServletRequest request) {}

    @GetMapping(value = "csv", produces = "text/csv")
    public String getCsv() {
        return "someProperty\nsome-value\n";
    }

    @Value
    static class SomeDto {
        String someProperty;
    }
}
