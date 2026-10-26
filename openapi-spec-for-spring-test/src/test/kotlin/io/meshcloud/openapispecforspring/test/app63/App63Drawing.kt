package io.meshcloud.openapispecforspring.test.app63

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

data class App63Drawing(val shape: App63Shape)

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = App63Circle::class, name = "circle"),
  JsonSubTypes.Type(value = App63Square::class, name = "square"),
)
sealed interface App63Shape

data class App63Circle(val radius: Int = 1) : App63Shape

data class App63Square(val side: Int) : App63Shape
