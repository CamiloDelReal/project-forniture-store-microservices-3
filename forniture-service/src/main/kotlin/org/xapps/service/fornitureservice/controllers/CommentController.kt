package org.xapps.service.fornitureservice.controllers

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.xapps.service.fornitureservice.dtos.CommentCreateRequest
import org.xapps.service.fornitureservice.dtos.CommentResponse
import org.xapps.service.fornitureservice.dtos.CommentUpdateRequest
import org.xapps.service.fornitureservice.security.Credential
import org.xapps.service.fornitureservice.services.CommentService

@RestController
@RequestMapping(path = ["/comments"])
class CommentController @Autowired constructor(
    private val commentService: CommentService
) {

    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun readAll(
        @RequestParam(value = "from", defaultValue = "0") from: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<List<CommentResponse>> =
        ResponseEntity.ok(commentService.readAll(from, size))

    @GetMapping(
        path = ["/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun read(@PathVariable("id") id: Long): ResponseEntity<CommentResponse> =
        ResponseEntity.ok(commentService.read(id))

    @GetMapping(
        path = ["/forniture/{fornitureId}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun readByForniture(@PathVariable("fornitureId") fornitureId: String) =
        ResponseEntity.ok(commentService.readByForniture(fornitureId))

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator') or isAuthenticated() and principal.customerId == #request.customerId")
    fun create(@Valid @RequestBody request: CommentCreateRequest): ResponseEntity<CommentResponse> =
        ResponseEntity(commentService.create(request), HttpStatus.CREATED)

    @PutMapping(
        path = ["/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun update(@PathVariable("id") id: Long, @Valid @RequestBody request: CommentUpdateRequest): ResponseEntity<CommentResponse> =
        checkAuthor(id) {
            ResponseEntity.ok(commentService.update(id, request))
        }

    @DeleteMapping(
        path = ["/{id}"]
    )
    @PreAuthorize("isAuthenticated()")
    fun delete(@PathVariable("id") id: Long): ResponseEntity<Void> =
        checkAuthor(id) {
            commentService.delete(id)
            ResponseEntity.ok().build()
        }

    fun <T> checkAuthor(commentId: Long, procedure: () -> ResponseEntity<T>): ResponseEntity<T> {
        val principal = SecurityContextHolder.getContext().authentication.principal
        return if(principal is Credential && (principal.roles.contains("Administrator") || commentService.isAuthor(commentId, principal.customerId))) {
            procedure.invoke()
        } else {
            ResponseEntity<T>(HttpStatus.FORBIDDEN)
        }
    }

}