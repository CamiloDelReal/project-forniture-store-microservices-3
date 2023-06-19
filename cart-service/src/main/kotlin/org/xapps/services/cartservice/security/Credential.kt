package org.xapps.services.cartservice.security

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Credential(
    @JsonProperty(value = CUSTOMER_ID_PROPERTY)
    var customerId: Long,

    @JsonProperty(value = USERNAME_PROPERTY)
    var username: String,

    @JsonProperty(value = ROLES_PROPERTY)
    var roles: List<String>
) {
    companion object {
        const val CUSTOMER_ID_PROPERTY = "customerId"
        const val USERNAME_PROPERTY = "username"
        const val ROLES_PROPERTY = "roles"
    }
}