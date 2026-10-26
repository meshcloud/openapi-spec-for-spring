package io.meshcloud.openapispecforspring.test.app64

import io.swagger.v3.oas.annotations.media.Schema

data class App64Pet(
  @field:Schema(additionalProperties = Schema.AdditionalPropertiesValue.TRUE)
  val traits: Map<String, Any>,
  val notes: Map<String, Any>,
  @field:Schema(additionalProperties = Schema.AdditionalPropertiesValue.TRUE)
  val medicalRecord: Any,
  val tag: App64Tag,
)

@Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
data class App64Tag(val text: String)
