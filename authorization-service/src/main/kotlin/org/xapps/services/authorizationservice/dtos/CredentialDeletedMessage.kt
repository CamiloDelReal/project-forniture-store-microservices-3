package org.xapps.services.authorizationservice.dtos

data class CredentialDeletedMessage(
    val successful: Boolean = true,
    val customerId: Long = 0
)