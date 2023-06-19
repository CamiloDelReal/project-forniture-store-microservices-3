package org.xapps.services.authorizationservice.repositories

import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor
import org.springframework.stereotype.Repository
import org.xapps.services.authorizationservice.entities.Token

@Repository
interface TokenRepository : QueryByExampleExecutor<Token>, CrudRepository<Token, String> {

    fun findAllByCredentialId(credentialId: Long): List<Token>

    fun existsByIdAndCredentialId(id: String, credentialId: Long): Boolean

}