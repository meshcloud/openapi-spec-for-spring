package io.meshcloud.openapispecforspring.test.app57;

import java.util.List;
import org.springframework.http.MediaType;

final class App57MediaTypes {
    static final String BOOK_V1 = "application/vnd.library.book.v1.hal+json";
    static final String BOOK_V2 = "application/vnd.library.book.v2.hal+json";

    static final List<MediaType> ALL = List.of(MediaType.valueOf(BOOK_V1), MediaType.valueOf(BOOK_V2));

    private App57MediaTypes() {}
}
