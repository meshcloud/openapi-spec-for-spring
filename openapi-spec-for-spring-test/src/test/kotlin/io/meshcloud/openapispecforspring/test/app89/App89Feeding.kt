package io.meshcloud.openapispecforspring.test.app89

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.swagger.v3.oas.annotations.media.Schema

data class App89Feeding(
  val confirmation: App89Confirmation,
  val marker: App89Marker,
  val extras: App89Extras,
  val food: App89Food,
  val schedule: App89Schedule,
)

object App89Confirmation

class App89Marker

@Schema(additionalProperties = Schema.AdditionalPropertiesValue.TRUE)
class App89Extras

interface App89Food

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = App89NoSchedule::class, name = "none"),
  JsonSubTypes.Type(value = App89DailySchedule::class, name = "daily"),
)
sealed interface App89Schedule

data object App89NoSchedule : App89Schedule

data class App89DailySchedule(val hour: Int) : App89Schedule
