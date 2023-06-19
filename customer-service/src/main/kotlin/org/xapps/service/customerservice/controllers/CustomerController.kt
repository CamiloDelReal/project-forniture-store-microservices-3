package org.xapps.service.customerservice.controllers

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.xapps.service.customerservice.dtos.*
import org.xapps.service.customerservice.security.Credential
import org.xapps.service.customerservice.services.CustomerService

@RestController
@RequestMapping(path = ["/customers"])
class CustomerController @Autowired constructor(
    private val customerService: CustomerService
) {

    @GetMapping(
        path = ["/page"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator')")
    fun readAll(
        @RequestParam(value = "from", defaultValue = "0") from: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<List<CustomerResponse>> =
        ResponseEntity.ok(customerService.readAll(from, size))

    @GetMapping(
        path = ["/current"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun read(@RequestHeader(HttpHeaders.AUTHORIZATION) authzHeader: String): ResponseEntity<CustomerResponse> {
        val principal = SecurityContextHolder.getContext().authentication.principal as Credential
        return ResponseEntity.ok(customerService.read(principal.customerId, authzHeader))
    }

    @GetMapping(
        path = ["/id/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.customerId == #id")
    fun readWithId(
        @PathVariable("id") id: Long,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authzHeader: String
    ): ResponseEntity<CustomerResponse> =
        ResponseEntity.ok(customerService.read(id, authzHeader))

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(@Valid @RequestBody request: CustomerCreateRequest): ResponseEntity<CustomerResponse> =
        ResponseEntity(customerService.create(request), HttpStatus.CREATED)

    @PutMapping(
        path = ["/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.customerId == #id")
    fun update(
        @PathVariable("id") id: Long,
        @Valid @RequestBody request: CustomerUpdateRequest
    ): ResponseEntity<CustomerResponse> =
        ResponseEntity(customerService.update(id, request), HttpStatus.OK)

    @DeleteMapping(
        path = ["/{id}"]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.customerId == #id")
    fun delete(
        @PathVariable("id") id: Long,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authzHeader: String
    ): ResponseEntity<Void> {
        customerService.delete(id, authzHeader)
        return ResponseEntity.ok().build()
    }

}