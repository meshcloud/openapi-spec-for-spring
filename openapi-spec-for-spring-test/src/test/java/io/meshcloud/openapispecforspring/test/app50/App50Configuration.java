package io.meshcloud.openapispecforspring.test.app50;

import com.fasterxml.jackson.databind.JavaType;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialType;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialTypeBuilder;
import io.meshcloud.openapispecforspring.common.schema.resolver.type.initial.InitialTypeBuilderForReferenceType;
import io.meshcloud.openapispecforspring.common.util.OpenApiOrderedUtils;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

@Configuration
@RequiredArgsConstructor
public class App50Configuration {

    @Bean
    public InitialTypeBuilder conversionServiceAwareInitialTypeBuilder(ConversionService conversionService) {
        return new InitialTypeBuilder() {

            @Override
            public int getOrder() {
                return OpenApiOrderedUtils.laterThan(InitialTypeBuilderForReferenceType.ORDER);
            }

            @Nullable
            @Override
            public InitialType build(
                    SchemaResolver.Caller caller,
                    JavaType javaType,
                    AnnotationsSupplier annotationsSupplier,
                    RecursiveBuilder recursiveBuilder) {
                if (javaType.isPrimitive()
                        || javaType.isTypeOrSubTypeOf(String.class)
                        || caller != SchemaResolver.Caller.PARAMETER) {
                    return null;
                }
                TypeDescriptor parameterTypeDescriptor =
                        new TypeDescriptor(ResolvableType.forType(javaType), javaType.getRawClass(), null);
                TypeDescriptor stringType = TypeDescriptor.valueOf(String.class);
                val canConvertFromString = conversionService.canConvert(stringType, parameterTypeDescriptor);
                if (canConvertFromString) {
                    return recursiveBuilder.build(String.class, annotationsSupplier);
                }
                return null;
            }
        };
    }
}
