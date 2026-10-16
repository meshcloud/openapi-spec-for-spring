package io.meshcloud.openapispecforspring.test.app78

import com.fasterxml.jackson.databind.JsonNode
import io.swagger.v3.oas.annotations.media.Schema

data class App78Plant(
  @Schema(description = "The botanical name")
  val name: String,
  val nickname: String?,
  val height: Int = 0,
  val measurement: Any?,
  val attributes: Map<String, Any>,
  val notes: JsonNode?,
) {
  var gardener: String? = null
}
