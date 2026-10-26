package io.meshcloud.openapispecforspring.test.app64;

import io.meshcloud.openapispecforspring.test.ApplicationObjectMapperForSchemas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ApplicationObjectMapperForSchemas.class)
class App64 {
    public static void main(String[] args) {
        SpringApplication.run(App64.class, args);
    }
}
