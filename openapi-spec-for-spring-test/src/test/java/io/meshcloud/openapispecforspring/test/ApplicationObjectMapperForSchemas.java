package io.meshcloud.openapispecforspring.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.meshcloud.openapispecforspring.common.supplier.DefaultOpenApiObjectMapperSupplier;
import io.meshcloud.openapispecforspring.common.supplier.OpenApiObjectMapperSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationObjectMapperForSchemas {

    @Bean
    OpenApiObjectMapperSupplier openApiObjectMapperSupplier(ObjectMapper applicationObjectMapper) {
        DefaultOpenApiObjectMapperSupplier defaultSupplier = new DefaultOpenApiObjectMapperSupplier();
        return purpose -> purpose == OpenApiObjectMapperSupplier.Purpose.SCHEMA_BUILDING
                ? applicationObjectMapper
                : defaultSupplier.get(purpose);
    }
}
