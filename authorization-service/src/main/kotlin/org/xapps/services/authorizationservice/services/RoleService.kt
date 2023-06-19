package org.xapps.services.authorizationservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.xapps.services.authorizationservice.entities.Role
import org.xapps.services.authorizationservice.repositories.RoleRepository

@Service
class RoleService @Autowired constructor(
    private val roleRepository: RoleRepository
) {

    fun readAll(): List<Role> =
        roleRepository.findAll()

}