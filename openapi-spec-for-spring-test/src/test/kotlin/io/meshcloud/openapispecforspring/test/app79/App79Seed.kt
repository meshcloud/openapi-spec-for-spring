package io.meshcloud.openapispecforspring.test.app79

data class App79Seed(
  val variety: String,
  val count: Int = 1,
)

data class App79SeedRequest(
  val varieties: List<String>,
)
