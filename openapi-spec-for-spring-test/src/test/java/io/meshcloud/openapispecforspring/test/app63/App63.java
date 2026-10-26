package io.meshcloud.openapispecforspring.test.app63;

import io.meshcloud.openapispecforspring.test.ApplicationObjectMapperForSchemas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ApplicationObjectMapperForSchemas.class)
class App63 {
    public static void main(String[] args) {
        SpringApplication.run(App63.class, args);
    }
}
