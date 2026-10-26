package io.meshcloud.openapispecforspring.test.app64

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import io.swagger.v3.oas.annotations.media.Schema

data class App64Feeding(
  val confirmation: App64Confirmation,
  val marker: App64Marker,
  val extras: App64Extras,
  val food: App64Food,
  val schedule: App64Schedule,
)

object App64Confirmation

class App64Marker

@Schema(additionalProperties = Schema.AdditionalPropertiesValue.TRUE)
class App64Extras

interface App64Food

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes(
  JsonSubTypes.Type(value = App64NoSchedule::class, name = "none"),
  JsonSubTypes.Type(value = App64DailySchedule::class, name = "daily"),
)
sealed interface App64Schedule

data object App64NoSchedule : App64Schedule

data class App64DailySchedule(val hour: Int) : App64Schedule
