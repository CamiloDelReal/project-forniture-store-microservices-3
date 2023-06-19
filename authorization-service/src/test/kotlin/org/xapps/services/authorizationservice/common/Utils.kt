package org.xapps.services.authorizationservice.common

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.time.Instant
import java.util.*
import org.xapps.services.authorizationservice.dtos.CredentialResponse

object Utils {
    fun generateToken(
        credential: CredentialResponse,
        currentTimestamp: Long = Instant.now().toEpochMilli(),
        expirationRaw: Long = currentTimestamp + 100000,
        key: String,
        mapper: ObjectMapper = ObjectMapper()
    ): String {
        val expiration = Date(expirationRaw)
        val subject = mapper.writeValueAsString(credential)
        return Jwts.builder()
            .setSubject(subject)
            .setId(UUID.randomUUID().toString())
            .setIssuedAt(Date(currentTimestamp))
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()
    }
}