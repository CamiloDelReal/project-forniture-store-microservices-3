package org.xapps.service.customerservice.common

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.xapps.service.customerservice.security.Credential
import java.time.Instant
import java.util.*

object Utils {
    fun generateToken(
        currentTimestamp: Long = Instant.now().toEpochMilli(),
        expirationRaw: Long = currentTimestamp + 1800000,
        key: String,
        customerId: Long = 33,
        useAdminRole: Boolean = true,
        mapper: ObjectMapper = ObjectMapper()
    ): String {
        val credential = Credential(
            customerId = customerId,
            username = "user",
            roles = if (useAdminRole) listOf("Administrator") else listOf("Guest")
        )

        println("Credentials $credential")

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