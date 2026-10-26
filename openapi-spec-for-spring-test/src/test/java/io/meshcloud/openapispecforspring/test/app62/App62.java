package io.meshcloud.openapispecforspring.test.app62;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;

@SpringBootApplication
class App62 {
    public static void main(String[] args) {
        SpringApplication.run(App62.class, args);
    }

    @Bean
    PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
        return resolver -> {
            resolver.setMaxPageSize(25);
            resolver.setPageParameterName("p");
            resolver.setOneIndexedParameters(true);
        };
    }

    @Bean
    SortHandlerMethodArgumentResolverCustomizer sortCustomizer() {
        return resolver -> resolver.setSortParameter("order");
    }
}
