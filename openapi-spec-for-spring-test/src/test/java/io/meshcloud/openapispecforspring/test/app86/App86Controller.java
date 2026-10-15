package io.meshcloud.openapispecforspring.test.app86;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class App86Controller {

    @GetMapping("/shelf")
    Shelf getShelf() {
        throw new UnsupportedOperationException();
    }

    @GetMapping("/labels")
    String[] getLabels() {
        throw new UnsupportedOperationException();
    }

    record Shelf(String[] labels, int[] heights, Book[] books, byte[] photo) {}

    record Book(String title) {}
}
