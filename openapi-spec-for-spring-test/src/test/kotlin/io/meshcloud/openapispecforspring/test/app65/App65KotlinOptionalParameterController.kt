package io.meshcloud.openapispecforspring.test.app65

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/kotlin-optional-parameters")
class App65KotlinOptionalParameterController {
  @GetMapping("/owners/{owner}/pets")
  fun pets(
    @PathVariable owner: String?,
    @RequestParam species: String,
    @RequestParam name: String?,
    @RequestParam includeAdopted: Boolean = false,
    @RequestParam color: String? = null,
    @RequestHeader("X-Shelter") shelter: String?,
  ): List<String> = listOf()
}
