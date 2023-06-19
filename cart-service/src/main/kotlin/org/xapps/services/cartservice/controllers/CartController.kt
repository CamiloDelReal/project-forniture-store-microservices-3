package org.xapps.services.cartservice.controllers

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.xapps.services.cartservice.dtos.CartCreateRequest
import org.xapps.services.cartservice.dtos.CartResponse
import org.xapps.services.cartservice.dtos.CartUpdateRequest
import org.xapps.services.cartservice.services.CartService

@RestController
@RequestMapping(path = ["/carts"])
class CartController @Autowired constructor(
    private val cartService: CartService
) {

    @GetMapping(
        path = ["/{customerId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.customerId == #customerId")
    fun read(@PathVariable("customerId") customerId: Long): ResponseEntity<CartResponse> =
        ResponseEntity.ok(cartService.read(customerId))


    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.customerId == #request.customerId")
    fun create(@Valid @RequestBody request: CartCreateRequest): ResponseEntity<CartResponse> =
        ResponseEntity(cartService.create(request), HttpStatus.CREATED)

    @PutMapping(
        path = ["/{customerId}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.customerId == #customerId")
    fun update(@PathVariable("customerId") customerId: Long, @Valid @RequestBody request: CartUpdateRequest): ResponseEntity<CartResponse> =
        ResponseEntity.ok(cartService.update(customerId, request))

    @DeleteMapping(
        path = ["/{customerId}"]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.customerId == #customerId")
    fun delete(@PathVariable("customerId") customerId: Long): ResponseEntity<Void> {
        cartService.delete(customerId)
        return ResponseEntity.ok().build()
    }

    @PostMapping(
        path = ["/checkout/{customerId}"]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.customerId == #customerId")
    fun checkout(
        @PathVariable("customerId") customerId: Long
    ): ResponseEntity<Void> {
        cartService.checkout(customerId)
        return ResponseEntity.ok().build()
    }

}