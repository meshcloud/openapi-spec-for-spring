package io.meshcloud.openapispecforspring.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import io.meshcloud.openapispecforspring.data.PageableParameterExpander;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.data.domain.Pageable;

class OpenApiSpecDataAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(OpenApiSpecDataAutoConfiguration.class));

    @Test
    void expandsPageableByDefault() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(PageableParameterExpander.class));
    }

    @Test
    void expandsNothingWhenDisabled() {
        contextRunner
                .withPropertyValues("openapi-spec-for-spring.data.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(PageableParameterExpander.class));
    }

    @Test
    void expandsNothingWithoutSpringData() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(Pageable.class))
                .run(context -> assertThat(context).doesNotHaveBean(PageableParameterExpander.class));
    }
}
