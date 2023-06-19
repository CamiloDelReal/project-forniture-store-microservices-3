package org.xapps.service.customerservice.dtos

data class CredentialResponseMessage(
    val successful: Boolean = true,
    val customerId: Long = 0,
    val response: CredentialResponse? = null
)