package org.xapps.service.fornitureservice.controllers

import co.elastic.clients.elasticsearch._types.ElasticsearchException
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.xapps.service.fornitureservice.services.exceptions.CommentNotAvailableException
import org.xapps.service.fornitureservice.services.exceptions.ElasticsearchRepositoryException
import org.xapps.service.fornitureservice.services.exceptions.IdNotFoundException

@ControllerAdvice
class ExceptionCatcher {

    @ExceptionHandler
    fun globalCatcher(ex: Exception): ResponseEntity<Any> {
        return when (ex) {
            // Framework exceptions
            is AccessDeniedException -> ResponseEntity(ex.toErrorResponse("Access denied"), HttpStatus.FORBIDDEN)
            // Elasticsearch exceptions
            is ElasticsearchException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR)
            // Jwt Exceptions
            is JwtException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.UNAUTHORIZED)
            // Custom exceptions
            is IdNotFoundException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.NOT_FOUND)
            is CommentNotAvailableException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.BAD_REQUEST)
            is ElasticsearchRepositoryException -> ResponseEntity(ex.toErrorResponse(), HttpStatus.INTERNAL_SERVER_ERROR)
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