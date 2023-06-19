package org.xapps.services.supportservice.common

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.xapps.services.supportservice.security.Credential
import java.time.Instant
import java.util.*

object Utils {
    fun generateToken(
        currentTimestamp: Long = Instant.now().toEpochMilli(),
        expirationRaw: Long = currentTimestamp + 1800000,
        key: String,
        customerId: Long = 33,
        roles: List<String> = listOf("Customer"),
        mapper: ObjectMapper = ObjectMapper()
    ): String {
        val credential = Credential(
            customerId = customerId,
            username = "user",
            roles = roles
        )

        val expiration = Date(expirationRaw)
        val subject = mapper.writeValueAsString(credential)
        return Jwts.builder()
            .setSubject(subject)
            .setIssuedAt(Date(currentTimestamp))
            .setExpiration(expiration)
            .signWith(SignatureAlgorithm.HS256, key)
            .compact()
    }
}