package org.xapps.services.authorizationservice.seeders

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "seeder")
data class SeederParams(
    val rootCredential: RootCredential
)

data class RootCredential(
    val username: String,
    val password: String,
    val customerId: Long
)