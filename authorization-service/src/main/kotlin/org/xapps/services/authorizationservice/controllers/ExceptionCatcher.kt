package org.xapps.services.authorizationservice.controllers

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.*
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.xapps.services.authorizationservice.services.exceptions.*

@ControllerAdvice
class ExceptionCatcher {

    @ExceptionHandler
    fun globalCatcher(ex: Exception): ResponseEntity<Any> {
        return when (ex) {
            // Framework exceptions
            is AccessDeniedException -> ResponseEntity(ex.toErrorResponse("Access denied"), HttpStatus.UNAUTHORIZED)
            is BadCredentialsException -> ResponseEntity(ex.toErrorResponse("Credential is invalid"), HttpStatus.UNAUTHORIZED)
            is LockedException -> ResponseEntity(ex.toErrorResponse("Account is locked"), HttpStatus.UNAUTHORIZED)
            is AccountExpiredException -> ResponseEntity(ex.toErrorResponse("Account is expired"), HttpStatus.UNAUTHORIZED)
            is CredentialsExpiredException -> ResponseEntity(ex.toErrorResponse("Credential is expired"), HttpStatus.UNAUTHORIZED)
            is DisabledException -> ResponseEntity(ex.toErrorResponse("Credential is not enabled"), HttpStatus.UNAUTHORIZED)
            // Jwt Exceptions
            is JwtException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.UNAUTHORIZED)
            // Custom exceptions
            is InvalidCredentialException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.UNAUTHORIZED)
            is IdNotFoundException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.NOT_FOUND)
            is UsernameNotAvailableException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.BAD_REQUEST)
            is MultipleCredentialsException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.BAD_REQUEST)
            is MissingValueException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.BAD_REQUEST)
            is TokenRevocationException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.UNAUTHORIZED)
            // Default
            else -> ResponseEntity(ex.toErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}

fun Exception.toErrorResponse(overrideMessage: String? = null): ErrorResponse =
    ErrorResponse(overrideMessage ?: message ?: "Error description not available")

data class ErrorResponse(
    val message: String = ""
)