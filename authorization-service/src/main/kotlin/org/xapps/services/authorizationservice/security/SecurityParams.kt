package org.xapps.services.authorizationservice.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security")
data class SecurityParams(
    val origins: Origins,
    val jwtGeneration: JwtGeneration
)

data class Origins(
    val urls: List<String>,
    var maxAge: Long,
    val allowedMethods: List<String>,
    val allowedHeaders: List<String>,
)

data class JwtGeneration(
    val key: String,
    val validity: Long,
    val type: String
)