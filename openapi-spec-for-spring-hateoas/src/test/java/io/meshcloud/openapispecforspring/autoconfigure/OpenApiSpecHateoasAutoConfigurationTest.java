package io.meshcloud.openapispecforspring.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaNameBuilder;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.hateoas.TypeResolverForEntityModel;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.hateoas.RepresentationModel;

class OpenApiSpecHateoasAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(OpenApiSpecHateoasAutoConfiguration.class))
            .withBean(SchemaNameBuilder.class, () -> mock(SchemaNameBuilder.class))
            .withBean(AnnotationsSupplierFactory.class, () -> mock(AnnotationsSupplierFactory.class))
            .withBean(OpenApiObjectMapperSupplier.class, () -> mock(OpenApiObjectMapperSupplier.class));

    @Test
    void describesRepresentationModelsAsHalByDefault() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(TypeResolverForEntityModel.class));
    }

    @Test
    void describesNoHalWhenDisabled() {
        contextRunner
                .withPropertyValues("openapi-spec-for-spring.hateoas.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(TypeResolverForEntityModel.class));
    }

    @Test
    void describesNoHalWithoutSpringHateoas() {
        contextRunner
                .withClassLoader(new FilteredClassLoader(RepresentationModel.class))
                .run(context -> assertThat(context).doesNotHaveBean(TypeResolverForEntityModel.class));
    }
}
