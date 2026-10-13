package io.meshcloud.openapispecforspring.test.app16;

import io.meshcloud.openapispecforspring.common.security.OpenApiSecuritySchemesSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiServersSupplier;
import io.meshcloud.openapispecforspring.model.security.SecurityScheme;
import io.meshcloud.openapispecforspring.model.server.Server;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class App16Configuration {

    @Bean
    public OpenApiServersSupplier openApiServersSupplier() {
        return () -> Collections.singletonList(Server.builder()
                .url("http://some-url-from-supplier")
                .description("Description from supplier")
                .build());
    }

    @Bean
    public OpenApiSecuritySchemesSupplier openApiSecuritySchemesSupplier() {
        return () -> Collections.singletonMap(
                "scheme2",
                SecurityScheme.builder()
                        .type(SecuritySchemeType.HTTP.toString())
                        .description("scheme from supplier")
                        .in(SecuritySchemeIn.COOKIE.toString())
                        .build());
    }
}
