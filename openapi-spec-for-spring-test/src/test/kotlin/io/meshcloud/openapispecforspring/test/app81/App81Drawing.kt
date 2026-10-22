package io.meshcloud.openapispecforspring.test.app81

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class App81Drawing(val shape: App81Shape)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = App81Circle::class, name = "circle"),
  JsonSubTypes.Type(value = App81Square::class, name = "square"),
)
sealed interface App81Shape

data class App81Circle(val radius: Int = 1) : App81Shape

data class App81Square(val side: Int) : App81Shape
