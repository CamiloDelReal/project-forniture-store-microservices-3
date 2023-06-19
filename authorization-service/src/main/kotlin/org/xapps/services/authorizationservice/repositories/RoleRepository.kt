package org.xapps.services.authorizationservice.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.xapps.services.authorizationservice.entities.Role

@Repository
interface RoleRepository : JpaRepository<Role, Long> {

    fun findByValue(value: String): Role?

    fun findByValueIn(values: List<String>): List<Role>

}