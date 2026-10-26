package io.meshcloud.openapispecforspring.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.swagger.v3.parser.OpenAPIV3Parser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class ExpectedSpecsAreValidOpenApiTest {

    // These apps declare incomplete annotations or mappings, which the library writes as given.
    private static final Map<String, List<String>> KNOWN_VIOLATIONS = Map.of(
            "app4.json",
            List.of(
                    "attribute paths.'/get3'(get).responses.200.links.link1.requestBody is not of type `string`",
                    "attribute paths.'/get4'(get).responses.200.content.'application/json'.encoding.propertyInSchema2"
                            + " is unexpected"),
            "app8.json",
            List.of("paths.'/simple/get-instant-2/{pathParam}'. Declared path parameter pathParam needs to be defined"
                    + " as a path parameter in path or operation level"),
            "app15.json",
            List.of(
                    "attribute components.parameters.param2.[param2].content is missing",
                    "attribute paths.'/mapping1'(get).parameters.[param1].content is missing"),
            "app16.json",
            List.of(
                    "attribute components.securitySchemes.scheme2.scheme is missing",
                    "attribute components.securitySchemes.scheme3.in is missing",
                    "attribute components.securitySchemes.scheme3.name is missing",
                    "attribute components.securitySchemes.scheme4.flows is missing"));

    static Stream<Path> expectedSpecs() {
        return Arrays.stream(System.getProperty("expectedSpecDirs").split(File.pathSeparator))
                .map(Path::of)
                .flatMap(ExpectedSpecsAreValidOpenApiTest::listSorted);
    }

    @ParameterizedTest
    @MethodSource("expectedSpecs")
    void isAValidOpenApiDocument(Path spec) throws IOException {
        assertEquals(
                KNOWN_VIOLATIONS.getOrDefault(spec.getFileName().toString(), List.of()),
                validationMessages(Files.readString(spec)));
    }

    private static List<String> validationMessages(String spec) {
        return new OpenAPIV3Parser()
                .readContents(spec).getMessages().stream().sorted().toList();
    }

    private static Stream<Path> listSorted(Path directory) {
        try (Stream<Path> files = Files.list(directory)) {
            return files.sorted().toList().stream();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
