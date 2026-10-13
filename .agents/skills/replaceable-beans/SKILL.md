---
name: replaceable-beans
description: Places a new part of the spec builder as an SPI interface, a default implementation and an auto-configured bean, or adds an optional integration module. Use when adding a resolver, customizer, filter or name builder, when supporting a new library such as Kotlin or Spring Data, or when a user's own bean does not replace the default.
---

Every part of the spec builder is a bean that applications can replace. Keep that property for anything new.

| Piece | Module | Example |
|---|---|---|
| Interface (SPI) | `openapi-spec-for-spring-api` | `SchemaNameBuilder` |
| Default implementation | `openapi-spec-for-spring-common` | `DefaultSchemaNameBuilder` |
| Bean with `@ConditionalOnMissingBean` | `openapi-spec-for-spring-autoconfigure`, in the matching `OpenApiSpec*AutoConfiguration` | `OpenApiSpecSchemaAutoConfiguration` |

The interface is public API. A signature change there breaks every application that implements it, so prefer a
new interface or a default method.

Beans that the builder uses as a list (type resolvers, customizers, filters) run in `getOrder()` order. Set it
relative to an existing constant, e.g. `laterThan(DEFAULT_ORDER)` from `OpenApiOrderedUtils`, so a resolver for a
specific type runs before `TypeResolverForObject`, which accepts any object.

## Optional integrations

Support for another library goes into its own module, as `openapi-spec-for-spring-hateoas` does for Spring
HATEOAS:

- The module declares the library as `compileOnly`, so applications without it do not get it.
- Its auto-configuration is listed in its own `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
  and has `@ConditionalOnClass` on a class of that library.
- An `openapi-spec-for-spring.<module>.enabled` property turns it off.
- `openapi-spec-for-spring-starter` depends on the module, so applications get it without an extra dependency.
- `openapi-spec-for-spring-test` depends on it too; the integration-tests skill says why.

`OpenApiSpecHateoasAutoConfigurationTest` shows how to test the conditions with `ApplicationContextRunner`.
