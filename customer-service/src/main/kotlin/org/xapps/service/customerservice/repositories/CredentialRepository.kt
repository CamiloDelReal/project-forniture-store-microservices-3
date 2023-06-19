package org.xapps.service.customerservice.repositories

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.xapps.service.customerservice.dtos.CredentialCreateRequest
import org.xapps.service.customerservice.dtos.CredentialResponse

@FeignClient("\${authorization.service.url}")
interface CredentialRepository {
    @GetMapping(
        path = ["/credentials/customer/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun readByCustomerId(
        @PathVariable("id") id: Long,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authzHeader: String
    ): CredentialResponse

    @PostMapping(
        path = ["/credentials"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(request: CredentialCreateRequest): CredentialResponse

    @DeleteMapping(
        path = ["/credentials/customer/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun deleteByCustomerId(
        @PathVariable("id") id: Long,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authzHeader: String
    )
}