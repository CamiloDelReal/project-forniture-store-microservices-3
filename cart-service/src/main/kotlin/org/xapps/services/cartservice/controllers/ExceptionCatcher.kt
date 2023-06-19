package org.xapps.services.cartservice.controllers

import feign.FeignException
import feign.RetryableException
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.xapps.services.cartservice.services.exceptions.FornitureServiceException
import org.xapps.services.cartservice.services.exceptions.IdNotFoundException
import org.xapps.services.cartservice.services.exceptions.MessagingServiceException
import java.net.UnknownHostException

@ControllerAdvice
class ExceptionCatcher {

    @ExceptionHandler
    fun globalCatcher(ex: Exception): ResponseEntity<Any> {
        return when (ex) {
            // Framework exceptions
            is AccessDeniedException -> ResponseEntity(ex.toErrorResponse("Access denied"), HttpStatus.FORBIDDEN)
            is UnknownHostException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.BAD_GATEWAY)
            is RetryableException -> ResponseEntity(ex.toErrorResponse("Authorization service is not available"), HttpStatus.BAD_GATEWAY)
            is FeignException.BadRequest -> ResponseEntity(ex.toErrorResponse("Authorization service is not available"), HttpStatus.BAD_REQUEST)
            // Jwt Exceptions
            is JwtException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.UNAUTHORIZED)
            // Custom exceptions
            is IdNotFoundException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.NOT_FOUND)
            is FornitureServiceException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR)
            is MessagingServiceException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR)
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