package io.meshcloud.openapispecforspring.test.app55;

import org.jspecify.annotations.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App55Controller {
    @GetMapping("record")
    public RecordDto getRecord() {
        return null;
    }

    @PostMapping("record")
    public void postRecord(@RequestBody RecordDto recordDto) {}

    @GetMapping("class")
    public ClassDto getClassDto() {
        return null;
    }

    @SuppressWarnings("deprecation")
    record RecordDto(
            String nonNull,
            @Nullable String nullable,
            @Deprecated @Nullable Integer deprecatedNullable) {}

    static class ClassDto {
        public String nonNull;

        public @Nullable String jspecifyNullable;

        @org.springframework.lang.Nullable
        public String springNullable;

        @jakarta.annotation.Nullable
        public String jakartaNullable;

        @Deprecated
        public String deprecated;

        private String nullableGetter;

        public @Nullable String getNullableGetter() {
            return nullableGetter;
        }
    }
}
