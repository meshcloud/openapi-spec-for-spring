package io.meshcloud.openapispecforspring.test.app78;

import io.meshcloud.openapispecforspring.test.ApplicationObjectMapperForSchemas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ApplicationObjectMapperForSchemas.class)
class App78 {
    public static void main(String[] args) {
        SpringApplication.run(App78.class, args);
    }
}
