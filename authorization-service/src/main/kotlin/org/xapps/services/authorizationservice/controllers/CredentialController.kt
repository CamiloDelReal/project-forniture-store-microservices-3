package org.xapps.services.authorizationservice.controllers

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.xapps.services.authorizationservice.dtos.CredentialCreateRequest
import org.xapps.services.authorizationservice.dtos.CredentialResponse
import org.xapps.services.authorizationservice.dtos.CredentialUpdateRequest
import org.xapps.services.authorizationservice.entities.Credential
import org.xapps.services.authorizationservice.services.CredentialService

@RestController
@RequestMapping(path = ["credentials"])
class CredentialController @Autowired constructor(
    private val credentialService: CredentialService
) {

    @GetMapping(
        path = ["/page"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Supporter')")
    fun readAll(
        @RequestParam(value = "from", defaultValue = "0") from: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<List<CredentialResponse>> =
        ResponseEntity.ok(credentialService.readAll(from, size))

    @GetMapping(
        path = ["/current"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun read(): ResponseEntity<CredentialResponse> {
        val principal = SecurityContextHolder.getContext().authentication.principal as Credential
        return ResponseEntity.ok(credentialService.read(principal.id))
    }

    @GetMapping(
        path = ["/current/customer"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun readByCustomer(): ResponseEntity<CredentialResponse> {
        val principal = SecurityContextHolder.getContext().authentication.principal as Credential
        return ResponseEntity.ok(credentialService.readByCustomerId(principal.customerId))
    }

    @GetMapping(
        path = ["/id/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Supporter') or isAuthenticated() and principal.id == #id")
    fun readById(@PathVariable("id") id: Long): ResponseEntity<CredentialResponse> =
        ResponseEntity.ok(credentialService.read(id))

    @GetMapping(
        path = ["/customer/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Supporter') or isAuthenticated() and principal.customerId == #id")
    fun readByCustomerId(@PathVariable("id") id: Long): ResponseEntity<CredentialResponse> =
        ResponseEntity.ok(credentialService.readByCustomerId(id))

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(@Valid @RequestBody request: CredentialCreateRequest): ResponseEntity<CredentialResponse> {
        val requestingAdminRole = credentialService.requiresAdminRole(request)
        val principal = SecurityContextHolder.getContext().authentication.principal
        return if (!requestingAdminRole || (principal != null && principal is Credential && credentialService.hasAdminRole(principal))) {
            ResponseEntity(credentialService.create(request), HttpStatus.CREATED)
        } else {
            ResponseEntity<CredentialResponse>(HttpStatus.FORBIDDEN)
        }
    }

    @PutMapping(
        path = ["/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.id == #id")
    fun updateWithId(@PathVariable("id") id: Long, @RequestBody request: CredentialUpdateRequest): ResponseEntity<CredentialResponse> {
        val requestingAdminRole = credentialService.requiresAdminRole(request)
        val requestingStatusUpdate = credentialService.hasStatusUpdates(request)
        val principal = SecurityContextHolder.getContext().authentication.principal as Credential
        return if (
            (!requestingAdminRole && !requestingStatusUpdate) ||
            (credentialService.hasAdminRole(principal))) {
            ResponseEntity(credentialService.update(id, request), HttpStatus.OK)
        } else {
            ResponseEntity<CredentialResponse>(HttpStatus.FORBIDDEN)
        }
    }

    @PutMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun update(@RequestBody request: CredentialUpdateRequest): ResponseEntity<CredentialResponse> {
        val requestingAdminRole = credentialService.requiresAdminRole(request)
        val requestingStatusUpdate = credentialService.hasStatusUpdates(request)
        val principal = SecurityContextHolder.getContext().authentication.principal as Credential
        return if (
            (!requestingAdminRole && !requestingStatusUpdate) ||
            (credentialService.hasAdminRole(principal))) {
            ResponseEntity(credentialService.update(principal.id, request), HttpStatus.OK)
        } else {
            ResponseEntity<CredentialResponse>(HttpStatus.FORBIDDEN)
        }
    }

    @DeleteMapping(
        path = ["/{id}"]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.id == #id")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> {
        credentialService.delete(id)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping(
        path = ["/customer/{id}"]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.id == #id")
    fun deleteByCustomerId(@PathVariable("id") id: Long): ResponseEntity<Void> {
        credentialService.deleteByCustomerId(id)
        return ResponseEntity.ok().build()
    }

}

