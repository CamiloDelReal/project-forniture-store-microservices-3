package org.xapps.services.authorizationservice.dtos

data class CredentialResponseMessage (
    val successful: Boolean = true,
    val customerId: Long,
    val response: CredentialResponse? = null
)