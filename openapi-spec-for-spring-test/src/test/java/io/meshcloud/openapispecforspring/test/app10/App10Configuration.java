package io.meshcloud.openapispecforspring.test.app10;

import io.meshcloud.openapispecforspring.common.OpenApiRequestParameterProvider;
import io.meshcloud.openapispecforspring.common.filter.operation.OperationPreFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class App10Configuration {

    @Bean
    public OperationPreFilter operationPreFilterForGroupsQueryParam(OpenApiRequestParameterProvider parameterProvider) {
        return operationInfo -> {
            // important to get group inside the lambda, otherwise we're outside of request scope
            return parameterProvider
                    .getFirstQueryParameter("pathPrefix")
                    .map(pathPrefix -> operationInfo.getPathPattern().startsWith(pathPrefix))
                    .orElse(true); // we have another header-based filter, so don't DENY ALL by default
        };
    }

    @Bean
    public OperationPreFilter operationPreFilterForGroupsHeaderParam(
            OpenApiRequestParameterProvider parameterProvider) {
        return operationInfo -> {
            // important to get group inside the lambda, otherwise we're outside of request scope
            return parameterProvider
                    .getFirstHeaderValue("x-path-prefix")
                    .map(pathPrefix -> operationInfo.getPathPattern().startsWith(pathPrefix))
                    .orElse(true); // we have another query-based filter, so don't DENY ALL by default
        };
    }
}
