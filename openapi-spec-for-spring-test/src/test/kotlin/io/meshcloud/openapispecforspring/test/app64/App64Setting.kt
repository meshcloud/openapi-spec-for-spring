package io.meshcloud.openapispecforspring.test.app64

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema

data class App64Setting(
  val key: String,
  @field:Schema(
    nullable = true,
    oneOf = [String::class, Long::class, Boolean::class, Array<String>::class, App64Secret::class, App64ShelfRef::class],
  )
  val value: Any?,
  @field:Schema(anyOf = [String::class, App64ShelfRef::class])
  val fallback: Any,
  val location: App64Location,
)

data class App64Secret(
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val plaintext: String,
  @JsonProperty(access = JsonProperty.Access.READ_ONLY) val hash: String,
)

data class App64ShelfRef(val shelfId: String)

@Schema(oneOf = [App64Room::class, App64ShelfRef::class])
interface App64Location

data class App64Room(val floor: Int) : App64Location
