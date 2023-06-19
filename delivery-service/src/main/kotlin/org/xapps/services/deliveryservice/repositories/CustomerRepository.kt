package org.xapps.services.deliveryservice.repositories

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.xapps.services.deliveryservice.dtos.CustomerResponse

@FeignClient("\${customer.service.url}")
interface CustomerRepository {
    @GetMapping(
        path = ["/customers/id/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun readById(
        @PathVariable("id") id: Long
    ): CustomerResponse

}