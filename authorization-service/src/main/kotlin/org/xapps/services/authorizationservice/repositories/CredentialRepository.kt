package org.xapps.services.authorizationservice.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.xapps.services.authorizationservice.entities.Credential

@Repository
interface CredentialRepository : JpaRepository<Credential, Long> {

    fun findByUsername(username: String): Credential?

    fun existsByCustomerId(customerId: Long): Boolean

    fun existsByIdNotAndUsername(id: Long, username: String): Boolean

    fun existsByIdNotAndCustomerId(id: Long, customerId: Long): Boolean

    fun findByCustomerId(customerId: Long): Credential?

}