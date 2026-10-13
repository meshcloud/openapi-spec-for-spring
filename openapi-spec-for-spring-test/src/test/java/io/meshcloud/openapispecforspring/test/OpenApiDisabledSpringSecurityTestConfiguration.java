package io.meshcloud.openapispecforspring.test;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Turns off the Basic Auth that Spring Security enables by default, as the tests only need it for the firewall.
 */
public class OpenApiDisabledSpringSecurityTestConfiguration {
    @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
    @ConditionalOnMissingBean
    @Bean
    public SecurityFilterChain noWebMvcSecurityFilterChain(HttpSecurity http) throws Exception {
        return http.build();
    }
}
