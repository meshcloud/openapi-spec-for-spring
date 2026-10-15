package io.meshcloud.openapispecforspring.test.app88

import io.swagger.v3.oas.annotations.media.Schema

data class App88Pet(
  @field:Schema(additionalProperties = Schema.AdditionalPropertiesValue.TRUE)
  val traits: Map<String, Any>,
  val notes: Map<String, Any>,
  @field:Schema(additionalProperties = Schema.AdditionalPropertiesValue.TRUE)
  val medicalRecord: Any,
  val tag: App88Tag,
)

@Schema(additionalProperties = Schema.AdditionalPropertiesValue.FALSE)
data class App88Tag(val text: String)
