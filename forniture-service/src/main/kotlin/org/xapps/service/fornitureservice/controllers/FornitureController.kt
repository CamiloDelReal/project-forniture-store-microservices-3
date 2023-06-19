package org.xapps.service.fornitureservice.controllers

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.xapps.service.fornitureservice.dtos.FornitureCreateRequest
import org.xapps.service.fornitureservice.dtos.FornitureResponse
import org.xapps.service.fornitureservice.dtos.FornitureUpdateRequest
import org.xapps.service.fornitureservice.services.FornitureService

@RestController
@RequestMapping(path = ["/fornitures"])
class FornitureController @Autowired constructor(
    private val fornitureService: FornitureService
) {

    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun readAll(
        @RequestParam(value = "from", defaultValue = "0") from: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int
    ): ResponseEntity<List<FornitureResponse>> =
        ResponseEntity.ok(fornitureService.readAll(from, size))

    @GetMapping(
        path = ["/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun read(@PathVariable("id") id: String): ResponseEntity<FornitureResponse> =
        ResponseEntity.ok(fornitureService.read(id))

    @GetMapping(
        path = ["/ids/{ids}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun read(@PathVariable("ids") ids: List<String>): ResponseEntity<List<FornitureResponse>> =
        ResponseEntity.ok(fornitureService.read(ids))

    @PostMapping(
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun create(@Valid @RequestBody request: FornitureCreateRequest): ResponseEntity<FornitureResponse> =
        ResponseEntity(fornitureService.create(request), HttpStatus.CREATED)

    @PutMapping(
        path = ["/{id}"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun update(@PathVariable("id") id: String, @Valid @RequestBody request: FornitureUpdateRequest): ResponseEntity<FornitureResponse> =
        ResponseEntity.ok(fornitureService.update(id, request))

    @DeleteMapping(
        path = ["/{id}"]
    )
    fun delete(@PathVariable("id") id: String): ResponseEntity<Void> {
        fornitureService.delete(id)
        return ResponseEntity.ok().build()
    }

}