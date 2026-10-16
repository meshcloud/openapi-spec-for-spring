package io.meshcloud.openapispecforspring.common.schema.resolver.properties;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.meshcloud.openapispecforspring.common.annotation.AnnotationsSupplier;
import io.meshcloud.openapispecforspring.common.annotation.DefaultAnnotationsSupplierFactory;
import io.meshcloud.openapispecforspring.common.schema.resolver.SchemaResolver;
import java.util.List;
import org.junit.jupiter.api.Test;

class DefaultSchemaPropertiesResolverTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DefaultSchemaPropertiesResolver sut = new DefaultSchemaPropertiesResolver(
            purpose -> objectMapper, List.of(), new DefaultAnnotationsSupplierFactory());

    @Test
    void sortsGetterOnlyPropertiesByNameAfterTheFieldProperties() {
        assertThat(propertyNamesOf(Gauge.class)).containsExactly("unit", "alpha", "middle", "zeta");
    }

    @Test
    void keepsADeclaredPropertyOrder() {
        assertThat(propertyNamesOf(OrderedGauge.class)).containsExactly("zeta", "alpha");
    }

    private List<String> propertyNamesOf(Class<?> type) {
        return List.copyOf(sut.findProperties(
                        SchemaResolver.Caller.API_RESPONSE, objectMapper.constructType(type), AnnotationsSupplier.EMPTY)
                .keySet());
    }

    @SuppressWarnings("unused")
    private static class Gauge {
        public String unit = "bar";

        public int getZeta() {
            return 3;
        }

        public int getAlpha() {
            return 1;
        }

        public int getMiddle() {
            return 2;
        }
    }

    @JsonPropertyOrder({"zeta", "alpha"})
    @SuppressWarnings("unused")
    private static class OrderedGauge {
        public int getAlpha() {
            return 1;
        }

        public int getZeta() {
            return 3;
        }
    }
}
