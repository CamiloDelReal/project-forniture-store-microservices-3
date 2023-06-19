package org.xapps.services.deliveryservice.controllers

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.*
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.xapps.services.deliveryservice.services.exceptions.IdNotFoundException

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
            is IdNotFoundException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.NOT_FOUND)
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