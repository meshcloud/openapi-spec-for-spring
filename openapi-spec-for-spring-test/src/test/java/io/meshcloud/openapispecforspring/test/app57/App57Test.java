package io.meshcloud.openapispecforspring.test.app57;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.meshcloud.openapispecforspring.test.AbstractOpenApiSpecWebMvcIntTest;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.MediaTypes;

class App57Test extends AbstractOpenApiSpecWebMvcIntTest {

    @Test
    void servesThePagedModelInTheDescribedShape() throws Exception {
        mockMvc.perform(get("/books").accept(App57MediaTypes.BOOK_V2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.books[0].isbn").value("isbn-1"))
                .andExpect(jsonPath("$._embedded.books[0]._links.self.href").value("/books/isbn-1"))
                .andExpect(jsonPath("$._links.self.href").value("/books"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void servesTheRepresentationModelWithUnderscoreLinks() throws Exception {
        mockMvc.perform(get("/books/shelves").accept(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.shelfList[0].location").value("hall"))
                .andExpect(jsonPath("$._embedded.shelfList[0]._links.self.href").value("/books/shelves/hall"))
                .andExpect(jsonPath("$._embedded.shelfList[0].links").doesNotExist());
    }
}
