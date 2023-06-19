package org.xapps.service.customerservice.dtos

data class CredentialDeletedMessage(
    val successful: Boolean = true,
    val customerId: Long = 0
)