package org.xapps.services.supportservice.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Credential(
    @JsonProperty(value = USERNAME_PROPERTY)
    var username: String,

    @JsonProperty(value = ROLES_PROPERTY)
    var roles: List<String>,

    @JsonProperty(value = CUSTOMER_ID_PROPERTY)
    var customerId: Long,
) {

    companion object {
        const val USERNAME_PROPERTY = "username"
        const val ROLES_PROPERTY = "roles"
        const val CUSTOMER_ID_PROPERTY = "customerId"

        const val ADMINISTRATOR = "Administrator"
        const val SUPPORTER = "Supporter"
        const val CUSTOMER = "Customer"
    }

    fun hasCustomerOrAdminRole(): Boolean =
        roles.any { it in listOf(ADMINISTRATOR, CUSTOMER) }

    fun hasSupporterOrAdminRole(): Boolean =
        roles.any { it in listOf(ADMINISTRATOR, SUPPORTER) }

    fun isCustomer(): Boolean =
        roles.contains(CUSTOMER)

}