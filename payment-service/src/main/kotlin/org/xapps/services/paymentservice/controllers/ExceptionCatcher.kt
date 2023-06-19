package org.xapps.services.paymentservice.controllers

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.xapps.services.paymentservice.services.exceptions.InvoiceNotFoundException
import org.xapps.services.paymentservice.services.exceptions.PaymentDeniedException

@ControllerAdvice
class ExceptionCatcher {

    @ExceptionHandler
    fun globalCatcher(ex: Exception): ResponseEntity<Any> {
        return when (ex) {
            // Framework exceptions
            is AccessDeniedException -> ResponseEntity(ex.toErrorResponse("Access denied"), HttpStatus.FORBIDDEN)
            // Jwt Exceptions
            is JwtException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.UNAUTHORIZED)
            // Custom exceptions
            is InvoiceNotFoundException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.NOT_FOUND)
            is PaymentDeniedException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.NOT_ACCEPTABLE)
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