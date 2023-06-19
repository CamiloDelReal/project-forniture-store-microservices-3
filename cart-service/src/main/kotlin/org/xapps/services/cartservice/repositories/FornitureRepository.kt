package org.xapps.services.cartservice.repositories

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.xapps.services.cartservice.dtos.FornitureResponse

@FeignClient("\${forniture.service.url}")
interface FornitureRepository {
    @GetMapping(
        path = ["/fornitures/ids/{ids}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun readFornituresByIds(
        @PathVariable("ids") ids: List<String>
    ): List<FornitureResponse>
}