package io.meshcloud.openapispecforspring.test.app12;

import io.meshcloud.openapispecforspring.common.filter.operation.parameter.OperationParameterPostFilter;
import io.meshcloud.openapispecforspring.common.filter.operation.parameter.OperationParameterPreFilter;
import java.util.Objects;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestHeader;

@Configuration
class App12Configuration {

    public static final String X_FILTERED_HEADER_NAME = "X-Filtered-Header-Name";

    @Bean
    public OperationParameterPreFilter ignoreSpecificHeaderParameterFilter() {
        return methodParameter -> methodParameter
                .getAnnotationsSupplier()
                .findAnnotations(RequestHeader.class)
                .findFirst()
                .map(annotation -> !annotation.value().equals(X_FILTERED_HEADER_NAME))
                .orElse(true);
    }

    @Bean
    public OperationParameterPostFilter ignoredSpecificParameterName() {
        return parameter -> !Objects.equals(parameter.getName(), "filteredParameterName");
    }
}
