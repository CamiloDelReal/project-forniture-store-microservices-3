package org.xapps.services.gatewayservice.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security")
data class SecurityParams(
    val origins: Origins
)

data class Origins(
    val urls: List<String>,
    var maxAge: Long,
    val allowedMethods: List<String>,
    val allowedHeaders: List<String>,
)
