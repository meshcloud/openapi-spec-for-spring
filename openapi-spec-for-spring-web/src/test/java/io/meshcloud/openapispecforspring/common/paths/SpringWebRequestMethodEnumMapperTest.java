package io.meshcloud.openapispecforspring.common.paths;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.RequestMethod;

class SpringWebRequestMethodEnumMapperTest {
    @Test
    void enumsAreBijective() {
        assertThat(Stream.of(RequestMethod.values()).map(Enum::name))
                .containsExactlyInAnyOrderElementsOf(
                        Stream.of(io.meshcloud.openapispecforspring.model.path.RequestMethod.values())
                                .map(Enum::name)
                                .toList());
    }
}
