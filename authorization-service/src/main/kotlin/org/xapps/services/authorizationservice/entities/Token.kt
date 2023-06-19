package org.xapps.services.authorizationservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed
import java.io.Serializable

@RedisHash(Token.HASH_NAME)
data class Token(
    @Id
    @Indexed
    var id: String,

    @Indexed
    var credentialId: Long,

    @Indexed
    var expiration: Long
): Serializable {

    companion object {
        const val HASH_NAME = "tokens"

        const val ID_PROPERTY = "id"
        const val VALUE_PROPERTY = "value"
        const val TOKEN_PROPERTY = "token"
        const val TYPE_PROPERTY = "type"
        const val EXPIRATION_PROPERTY = "expiration"
        const val CREDENTIAL_ID_PROPERTY = "credentialId"
    }

}