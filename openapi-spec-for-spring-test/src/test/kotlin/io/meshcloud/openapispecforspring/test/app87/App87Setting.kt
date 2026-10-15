package io.meshcloud.openapispecforspring.test.app87

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class App87Setting(
  val key: String,
  @field:Schema(
    nullable = true,
    oneOf = [String::class, Long::class, Boolean::class, Array<String>::class, App87Secret::class, App87ShelfRef::class],
  )
  val value: Any?,
  @field:Schema(anyOf = [String::class, App87ShelfRef::class])
  val fallback: Any,
  val location: App87Location,
)

data class App87Secret(
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val plaintext: String,
  @JsonProperty(access = JsonProperty.Access.READ_ONLY) val hash: String,
)

data class App87ShelfRef(val shelfId: String)

@Schema(oneOf = [App87Room::class, App87ShelfRef::class])
interface App87Location

data class App87Room(val floor: Int) : App87Location
