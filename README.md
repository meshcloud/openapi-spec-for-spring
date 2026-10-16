# OpenAPI Spec for Spring

[![build](https://github.com/meshcloud/openapi-spec-for-spring/actions/workflows/build.yml/badge.svg?branch=spring-boot-3.x)](https://github.com/meshcloud/openapi-spec-for-spring/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=meshcloud_openapi-spec-for-spring&branch=spring-boot-3.x&metric=alert_status)](https://sonarcloud.io/summary/overall?id=meshcloud_openapi-spec-for-spring&branch=spring-boot-3.x)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=meshcloud_openapi-spec-for-spring&branch=spring-boot-3.x&metric=coverage)](https://sonarcloud.io/summary/overall?id=meshcloud_openapi-spec-for-spring&branch=spring-boot-3.x)

This library builds an [OpenAPI 3](https://github.com/OAI/OpenAPI-Specification) spec at runtime for a
[Spring Boot](https://spring.io/projects/spring-boot) application. It reads the Spring WebMVC handler methods and
their [Swagger annotations](https://github.com/swagger-api/swagger-core/tree/master/modules/swagger-annotations).

Each part of the build is a Spring bean that Spring Boot auto-configures with `@ConditionalOnMissingBean`. To change a
part, define your own bean of the same type. The interfaces you can implement, such as filters and customizers, are in
the [api module](openapi-spec-for-spring-api/src/main/java/io/meshcloud/openapispecforspring/common).

The library does not depend on the swagger-core or swagger-models modules.

## Origin

This project is a hard fork of
[qaware/openapi-generator-for-spring](https://github.com/qaware/openapi-generator-for-spring), written by QAware GmbH.
Thanks to QAware for the original work. meshcloud GmbH maintains this fork under the Apache License 2.0, see
[LICENSE.txt](LICENSE.txt) and [NOTICE](NOTICE).

## Getting started

The artifacts are in the GitHub Packages Maven repository `https://maven.pkg.github.com/meshcloud/openapi-spec-for-spring`.
GitHub Packages needs a GitHub token with the `read:packages` scope, also for public packages.

```groovy
repositories {
  maven {
    url = 'https://maven.pkg.github.com/meshcloud/openapi-spec-for-spring'
    credentials {
      username = providers.gradleProperty('gpr.user').orNull
      password = providers.gradleProperty('gpr.token').orNull
    }
  }
}

dependencies {
  implementation('io.meshcloud.openapispecforspring:openapi-spec-for-spring-boot3-starter:1.0.0')
}
```

Put `gpr.user` and `gpr.token` in `~/.gradle/gradle.properties`.

When the application runs, it serves the spec at `/v3/api-docs` as JSON. It also serves YAML to requests that accept
YAML when the application has `com.fasterxml.jackson.dataformat:jackson-dataformat-yaml` on its classpath. See
the [WebMVC demo](demo/openapi-spec-for-spring-demo-webmvc).

## Branches and versions

Each Spring Boot major version has its own branch, for example `spring-boot-3.x`. The Spring Boot major is part of the
artifactId, for example `openapi-spec-for-spring-boot3-starter`.

A release is a tag `<branch>-v<semver>` on that branch, for example `spring-boot-3.x-v1.0.0`. The artifact version is
the semver part, here `1.0.0`.

## Configuration properties

All properties have the prefix `openapi-spec-for-spring`:

* [OpenApiConfigurationProperties](openapi-spec-for-spring-common/src/main/java/io/meshcloud/openapispecforspring/common/OpenApiConfigurationProperties.java)
* [OpenApiInfoConfigurationProperties](openapi-spec-for-spring-common/src/main/java/io/meshcloud/openapispecforspring/common/info/OpenApiInfoConfigurationProperties.java)
* [Java8TimeTypeResolverConfigurationProperties](openapi-spec-for-spring-common/src/main/java/io/meshcloud/openapispecforspring/common/schema/resolver/type/extension/java8/Java8TimeTypeResolverConfigurationProperties.java)

## Build

The build needs [Nix](https://nixos.org) with flakes:

```shell
nix develop --command ./gradlew build
```

The coverage report of all modules is in `openapi-spec-for-spring-test/build/reports/jacoco/testCodeCoverageReport/`.
