package io.meshcloud.openapispecforspring.test.app59;

import kotlin.Unit;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/void")
class App59VoidController {
    @DeleteMapping("/kotlin-unit/{id}")
    ResponseEntity<Unit> deleteReturningKotlinUnit(@PathVariable String id) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/java-void/{id}")
    ResponseEntity<Void> deleteReturningJavaVoid(@PathVariable String id) {
        return ResponseEntity.noContent().build();
    }
}
