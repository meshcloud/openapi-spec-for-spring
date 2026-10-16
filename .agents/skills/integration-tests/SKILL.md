---
name: integration-tests
description: Writes and updates the AppNN integration tests that compare the served spec with an expected JSON file. Use when adding a feature or fixing a bug in how the spec is built, when an AppNNTest fails with a JSON mismatch, or when a change alters the expected openApiJson files.
---

Each test is a small Spring Boot application in `openapi-spec-for-spring-test`, package
`io.meshcloud.openapispecforspring.test.app<NN>`:

- `App<NN>` is the `@SpringBootApplication`, next to its controllers and DTOs.
- `App<NN>Test extends AbstractOpenApiSpecWebMvcIntTest`. The base class fetches `/v3/api-docs` and compares it
  with `src/test/resources/openApiJson/app<NN>.json`. It derives the file name from the class name, so the two
  names must match.
- Take the next free number: list the `app*` packages and add one.

The comparison is `JSONAssert` in strict mode: key order does not matter, but every extra or missing key fails.
On a mismatch the assertion message ends with the actual JSON. For a new app, copy it into `app<NN>.json` and
then read every line of it, because the file is the specification of the expected output.

When a change alters other apps' expected files, review each diff on its own. An unintended change in an
unrelated app is the usual sign of a regression.

The repository is public. Use an invented domain (books, shelves, pets), never code, names or media types from a
meshcloud product.

Run one test while you iterate, then the full build:

```sh
nix develop path:. --command ./gradlew :openapi-spec-for-spring-test:test --tests '*App57Test'
nix develop path:. --command ./gradlew build
```

A new module needs `implementation(project(':<module>'))` in `openapi-spec-for-spring-test/build.gradle`.
Without it, its coverage is missing from the aggregated report.
