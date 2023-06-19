package org.xapps.services.paymentservice.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.xapps.services.paymentservice.dtos.CardRequest
import org.xapps.services.paymentservice.dtos.InvoiceResponse
import org.xapps.services.paymentservice.entities.Invoice
import org.xapps.services.paymentservice.services.PaymentService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping(path = ["/payments"])
class PaymentController @Autowired constructor(
    private val paymentService: PaymentService
) {

    @GetMapping(
        path = ["/page"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Supervisor')")
    fun readAll(
        @RequestParam(value = "from", defaultValue = "0") from: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Flux<Invoice> =
        paymentService.readAll(from, size)

    @GetMapping(
        path = ["/id/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Supervisor')")
    fun readWithId(
        @PathVariable("id") id: Long,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authzHeader: String
    ): Mono<Invoice> =
        paymentService.readById(id)

    @GetMapping(
        path = ["/{paymentId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Supervisor')")
    fun readWithPaymentId(
        @PathVariable("paymentId") paymentId: String,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authzHeader: String
    ): Mono<Invoice> =
        paymentService.readByPaymentId(paymentId)

    @GetMapping(
        path = ["/customer/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Supervisor')")
    fun readAllByCustomer(
        @PathVariable("id") customerId: Long,
        @RequestParam(value = "from", defaultValue = "0") from: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): Flux<Invoice> =
        paymentService.readByCustomerId(customerId, from, size)

    @PostMapping(
        path = ["/{paymentId}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun executePayment(
        @PathVariable("paymentId") paymentId: String,
        @RequestBody card: CardRequest
    ): Mono<InvoiceResponse> =
        paymentService.executePayment(paymentId, card)

}