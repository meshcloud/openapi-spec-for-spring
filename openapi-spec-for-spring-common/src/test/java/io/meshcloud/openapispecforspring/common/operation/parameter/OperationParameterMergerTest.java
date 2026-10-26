/*-
 * #%L
 * OpenAPI Spec for Spring :: Common
 * %%
 * Copyright (C) 2026 meshcloud GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package io.meshcloud.openapispecforspring.common.operation.parameter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.meshcloud.openapispecforspring.common.paths.HandlerMethod;
import io.meshcloud.openapispecforspring.model.media.Schema;
import io.meshcloud.openapispecforspring.model.parameter.Parameter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OperationParameterMergerTest {

    private final Map<HandlerMethod, List<Parameter>> parametersByHandlerMethod = new LinkedHashMap<>();

    @Test
    void keepsEqualDescriptions() {
        withHandlerMethod("bookV1", query("title").description("The title.").build());
        withHandlerMethod("bookV2", query("title").description("The title.").build());

        assertThat(merge())
                .singleElement()
                .extracting(Parameter::getDescription)
                .isEqualTo("The title.");
    }

    @Test
    void takesTheDescriptionOfTheHandlerMethodThatHasOne() {
        withHandlerMethod("bookV1", query("title").description(" ").build());
        withHandlerMethod("bookV2", query("title").description("The title.").build());
        withHandlerMethod("bookV3", query("title").build());

        assertThat(merge())
                .singleElement()
                .extracting(Parameter::getDescription)
                .isEqualTo("The title.");
    }

    @Test
    void failsOnDifferentDescriptions() {
        withHandlerMethod("bookV1", query("title").description("The title.").build());
        withHandlerMethod(
                "bookV2", query("title").description("The full title.").build());

        assertThatThrownBy(this::merge)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cannot merge the parameters named title in query of GET /books from the handler methods"
                        + " bookV1, bookV2, because their values of description differ: [The title., The full title.]");
    }

    @Test
    void failsOnDifferentExamples() {
        withHandlerMethod("bookV1", query("title").example("Dune").build());
        withHandlerMethod("bookV2", query("title").example("Emma").build());

        assertThatThrownBy(this::merge).hasMessageContaining("their values of example differ: [Dune, Emma]");
    }

    @Test
    void deprecatesWhenEveryCopyIsDeprecated() {
        withHandlerMethod("bookV1", query("title").deprecated(true).build());
        withHandlerMethod("bookV2", query("title").deprecated(true).build());

        assertThat(merge()).singleElement().extracting(Parameter::getDeprecated).isEqualTo(true);
    }

    @Test
    void doesNotDeprecateWhenOnlySomeCopiesAreDeprecated() {
        withHandlerMethod("bookV1", query("title").build());
        withHandlerMethod("bookV2", query("title").deprecated(true).build());

        assertThat(merge()).singleElement().extracting(Parameter::getDeprecated).isNull();
    }

    @Test
    void requiresAParameterOnlyWhenEveryHandlerMethodRequiresIt() {
        withHandlerMethod(
                "bookV1",
                query("title").required(true).build(),
                query("author").required(true).build());
        withHandlerMethod("bookV2", query("title").required(true).build());

        assertThat(merge())
                .extracting(Parameter::getName, Parameter::getRequired)
                .containsExactly(tuple("title", true), tuple("author", false));
    }

    @Test
    void keepsTheParameterOfASingleHandlerMethodAsItIs() {
        Parameter title = query("title").description("The title.").build();
        withHandlerMethod("book", title);

        assertThat(merge()).singleElement().isSameAs(title);
    }

    private List<Parameter> merge() {
        return OperationParameterMerger.merge(
                "GET /books", List.copyOf(parametersByHandlerMethod.keySet()), parametersByHandlerMethod::get);
    }

    private void withHandlerMethod(String identifier, Parameter... parameters) {
        HandlerMethod handlerMethod = mock(HandlerMethod.class);
        when(handlerMethod.getIdentifier()).thenReturn(identifier);
        parametersByHandlerMethod.put(handlerMethod, List.of(parameters));
    }

    private static Parameter.ParameterBuilder query(String name) {
        return Parameter.builder()
                .name(name)
                .in("query")
                .schema(Schema.builder().type("string").build());
    }
}
