package org.xapps.services.authorizationservice.controllers

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.xapps.services.authorizationservice.dtos.*
import org.xapps.services.authorizationservice.entities.Credential
import org.xapps.services.authorizationservice.services.AuthorizationService
import org.xapps.services.authorizationservice.services.CredentialService
import org.xapps.services.authorizationservice.utils.logger

@RestController
@RequestMapping(path = ["/authorization"])
class AuthorizationController @Autowired constructor(
    private val authorizationService: AuthorizationService,
    private val credentialService: CredentialService
){

    @PostMapping(
        path = ["/login"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun login(@Valid @RequestBody login: Login): ResponseEntity<Authorization> =
        ResponseEntity.ok(authorizationService.login(login))

    @PostMapping(
        path = ["/token/validate"],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun validate(@Valid @RequestBody request: TokenValidateRequest): ResponseEntity<CredentialResponse> =
        ResponseEntity.ok(authorizationService.isAuthorizationValid(request.value))

    @GetMapping(
        path = ["/token/customer/{customerId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun readAllTokenByCustomer(@PathVariable("customerId") customerId: Long): ResponseEntity<List<TokenResponse>> {
        LOG.debug("Customer $customerId tokens requested")
        val principal = SecurityContextHolder.getContext().authentication.principal as Credential
        return if (credentialService.hasAdminRole(principal) || principal.customerId == customerId) {
            ResponseEntity.ok(authorizationService.readAllTokenByCredential(principal.id))
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @GetMapping(
        path = ["/token/credential/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.id == #id")
    fun readAllTokenByCredential(@PathVariable("id") id: Long): ResponseEntity<List<TokenResponse>> =
        ResponseEntity.ok(authorizationService.readAllTokenByCredential(id))

    @DeleteMapping(
        path = ["/token/{id}"]
    )
    @PreAuthorize("isAuthenticated()")
    fun revokeToken(@PathVariable("id") id: String): ResponseEntity<Void> {
        LOG.debug("Remove token $id requested")
        val principal = SecurityContextHolder.getContext().authentication.principal as Credential
        return if(authorizationService.isOwner(id, principal.id)) {
            authorizationService.revokeToken(id)
            ResponseEntity.ok().build()
        } else {
            ResponseEntity(HttpStatus.FORBIDDEN)
        }
    }

    @DeleteMapping(
        path = ["/token/credential/{id}"]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.id == #id")
    fun revokeTokensByCredential(@PathVariable("id") id: Long): ResponseEntity<Void> {
        LOG.debug("Remove tokens for credential $id requested")
        authorizationService.revokeTokensByCredential(id)
        return ResponseEntity.ok().build()
    }

    companion object {
        private val LOG = logger()
    }
}