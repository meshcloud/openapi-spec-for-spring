package io.meshcloud.openapispecforspring.test.app63

data class App63Seed(
  val variety: String,
  val count: Int = 1,
)

data class App63SeedRequest(
  val varieties: List<String>,
)
