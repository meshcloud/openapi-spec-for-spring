package io.meshcloud.openapispecforspring.test.app89;

import io.meshcloud.openapispecforspring.test.ApplicationObjectMapperForSchemas;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ApplicationObjectMapperForSchemas.class)
class App89 {
    public static void main(String[] args) {
        SpringApplication.run(App89.class, args);
    }
}
