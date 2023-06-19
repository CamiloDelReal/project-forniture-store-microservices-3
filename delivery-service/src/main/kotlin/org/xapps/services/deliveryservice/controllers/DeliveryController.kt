package org.xapps.services.deliveryservice.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.xapps.services.deliveryservice.dtos.DeliveryResponse
import org.xapps.services.deliveryservice.services.DeliveryService

@RestController
@RequestMapping(path = ["/deliveries"])
class DeliveryController @Autowired constructor(
    private val deliveryService: DeliveryService
) {

    @GetMapping(
        path = ["/page"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Currier')")
    fun readAll(
        @RequestParam(value = "from", defaultValue = "0") from: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<List<DeliveryResponse>> =
        ResponseEntity.ok(deliveryService.readAll(from, size))

    @GetMapping(
        path = ["/id/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Currier')")
    fun readWithId(
        @PathVariable("id") id: Long
    ): ResponseEntity<DeliveryResponse> =
        ResponseEntity.ok(deliveryService.read(id))

    @PostMapping(
        path = ["/delivered/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Currier')")
    fun markDelivered(
        @PathVariable("id") id: Long
    ): ResponseEntity<DeliveryResponse> =
        ResponseEntity.ok(deliveryService.markDelivered(id))

    @PostMapping(
        path = ["/resolveaddress/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Currier')")
    fun resolveDeliveryAddress(
        @PathVariable("id") id: Long
    ): ResponseEntity<DeliveryResponse> =
        ResponseEntity.ok(deliveryService.resolveDeliveryAddress(id))

}