package io.meshcloud.openapispecforspring.test.app81;

import io.meshcloud.openapispecforspring.test.ApplicationObjectMapperForSchemas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ApplicationObjectMapperForSchemas.class)
class App81 {
    public static void main(String[] args) {
        SpringApplication.run(App81.class, args);
    }
}
