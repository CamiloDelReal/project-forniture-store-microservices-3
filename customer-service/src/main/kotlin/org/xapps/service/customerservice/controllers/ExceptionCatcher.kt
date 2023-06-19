package org.xapps.service.customerservice.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import feign.FeignException
import feign.RetryableException
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.*
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.xapps.service.customerservice.services.exceptions.EmailNotAvailableException
import org.xapps.service.customerservice.services.exceptions.IdNotFoundException
import org.xapps.service.customerservice.services.exceptions.PhoneNotAvailableException
import org.xapps.service.customerservice.services.exceptions.UsernameNotAvailableException
import java.net.UnknownHostException
import kotlin.jvm.optionals.getOrNull

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
            is UnknownHostException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.BAD_GATEWAY)
            is RetryableException -> ResponseEntity(ex.toErrorResponse("Authorization service is not available"), HttpStatus.BAD_GATEWAY)
            is FeignException.BadRequest -> ResponseEntity(ex.toErrorResponse("Authorization service is not available"), HttpStatus.BAD_REQUEST)
            // Jwt Exceptions
            is JwtException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.UNAUTHORIZED)
            // Custom exceptions
            is IdNotFoundException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.NOT_FOUND)
            is EmailNotAvailableException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.BAD_REQUEST)
            is PhoneNotAvailableException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.BAD_REQUEST)
            is UsernameNotAvailableException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.BAD_REQUEST)
            // Default
            else -> ResponseEntity(ex.toErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

}

fun Exception.toErrorResponse(overrideMessage: String? = null): ErrorResponse =
    ErrorResponse(overrideMessage ?: message ?: "Error description not available")

@OptIn(ExperimentalStdlibApi::class)
fun FeignException.BadRequest.toErrorResponse(): ErrorResponse =
    responseBody().getOrNull()?.let {
        ObjectMapper().readValue(String(it.array()), ErrorResponse::class.java)
    } ?: run {
        ErrorResponse("Bad request response received from authorization service")
    }

data class ErrorResponse(
    val message: String = ""
)