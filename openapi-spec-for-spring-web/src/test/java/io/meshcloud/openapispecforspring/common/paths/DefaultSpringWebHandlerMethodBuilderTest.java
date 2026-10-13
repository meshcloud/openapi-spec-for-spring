package io.meshcloud.openapispecforspring.common.paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.operation.parameter.OpenApiSpringWebParameterNameDiscoverer;
import io.meshcloud.openapispecforspring.common.paths.method.SpringWebHandlerMethod;
import java.util.Arrays;
import java.util.List;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;

@ExtendWith(MockitoExtension.class)
class DefaultSpringWebHandlerMethodBuilderTest {

    private DefaultSpringWebHandlerMethodBuilder sut;

    @Mock
    private AnnotationsSupplierFactory annotationsSupplierFactory;

    @Mock
    private OpenApiSpringWebParameterNameDiscoverer parameterNameDiscoverer;

    @Mock
    private org.springframework.web.method.HandlerMethod springWebHandlerMethod;

    @Mock
    private MethodParameter methodParameter1;

    @BeforeEach
    void setUp() {
        sut = new DefaultSpringWebHandlerMethodBuilder(annotationsSupplierFactory, parameterNameDiscoverer, List.of());
    }

    @Test
    void build_parameterNumberMismatch() {
        when(parameterNameDiscoverer.getParameterNames(null)).thenReturn(Arrays.asList("parameter1", "parameter2"));
        when(springWebHandlerMethod.getMethodParameters()).thenReturn(new MethodParameter[] {methodParameter1});

        val actual = sut.build(springWebHandlerMethod);

        assertThat(actual).isInstanceOf(SpringWebHandlerMethod.class);
    }
}
