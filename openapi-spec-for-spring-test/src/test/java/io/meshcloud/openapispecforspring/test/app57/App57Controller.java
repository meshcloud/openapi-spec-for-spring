package io.meshcloud.openapispecforspring.test.app57;

import static io.meshcloud.openapispecforspring.test.app57.App57MediaTypes.BOOK_V1;
import static io.meshcloud.openapispecforspring.test.app57.App57MediaTypes.BOOK_V2;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/books")
class App57Controller {

    @GetMapping(produces = BOOK_V1)
    public ResponseEntity<PagedModel<EntityModel<BookV1>>> listBooksV1() {
        return ResponseEntity.ok(PagedModel.empty());
    }

    @GetMapping(produces = BOOK_V2)
    public ResponseEntity<PagedModel<EntityModel<BookV2>>> listBooksV2() {
        EntityModel<BookV2> book = EntityModel.of(new BookV2("isbn-1", "title", List.of()), Link.of("/books/isbn-1"));
        return ResponseEntity.ok(
                PagedModel.of(List.of(book), new PagedModel.PageMetadata(20, 0, 1), Link.of("/books")));
    }

    @GetMapping(value = "/{isbn}", produces = BOOK_V2)
    public ResponseEntity<EntityModel<BookV2>> getBookV2(@PathVariable String isbn) {
        return ResponseEntity.ok(EntityModel.of(new BookV2(isbn, "title", List.of())));
    }

    @GetMapping(value = "/{isbn}/editions", produces = BOOK_V2)
    public CollectionModel<EntityModel<Edition>> listEditions(@PathVariable String isbn) {
        return CollectionModel.empty();
    }

    @GetMapping(value = "/shelves", produces = MediaTypes.HAL_JSON_VALUE)
    public CollectionModel<Shelf> listShelves() {
        Shelf shelf = new Shelf("hall");
        shelf.add(Link.of("/books/shelves/hall"));
        return CollectionModel.of(List.of(shelf));
    }

    @Value
    @Relation(collectionRelation = "books")
    static class BookV1 {
        String isbn;
        String title;
    }

    @Value
    @Relation(collectionRelation = "books")
    static class BookV2 {
        String isbn;
        String title;
        List<Edition> editions;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "format")
    @JsonSubTypes({
        @JsonSubTypes.Type(value = Paperback.class, name = "paperback"),
        @JsonSubTypes.Type(value = Ebook.class, name = "ebook")
    })
    sealed interface Edition permits Paperback, Ebook {}

    @Value
    static class Paperback implements Edition {
        int pages;
    }

    @Value
    static class Ebook implements Edition {
        String fileFormat;
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    static class Shelf extends RepresentationModel<Shelf> {
        String location;
    }
}
