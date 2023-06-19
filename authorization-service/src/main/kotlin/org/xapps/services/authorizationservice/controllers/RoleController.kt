package org.xapps.services.authorizationservice.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.xapps.services.authorizationservice.entities.Role
import org.xapps.services.authorizationservice.services.RoleService

@RestController
@RequestMapping(path = ["/roles"])
class RoleController @Autowired constructor(
    private val roleService: RoleService
){

    @GetMapping(
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAuthority('Administrator')")
    fun readAll(): ResponseEntity<List<Role>> =
        ResponseEntity.ok(roleService.readAll())

}