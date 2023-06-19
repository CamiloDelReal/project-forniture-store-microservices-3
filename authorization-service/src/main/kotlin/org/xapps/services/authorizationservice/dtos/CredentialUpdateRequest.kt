package org.xapps.services.authorizationservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.authorizationservice.entities.Credential

data class CredentialUpdateRequest(
    @JsonProperty(value = Credential.CUSTOMER_ID_PROPERTY)
    var customerId: Long? = null,

    @JsonProperty(value = Credential.USERNAME_PROPERTY)
    var username: String? = null,

    @JsonProperty(value = Credential.PASSWORD_PROPERTY)
    val password: String? = null,

    @JsonProperty(value = Credential.ROLES_PROPERTY)
    var roles: List<String>? = null,

    @JsonProperty(value = Credential.ENABLED_PROPERTY)
    var enabled: Boolean? = null,

    @JsonProperty(value = Credential.ACCOUNT_NON_EXPIRED_PROPERTY)
    var accountNonExpired: Boolean? = null,

    @JsonProperty(value = Credential.CREDENTIALS_NON_EXPIRED_PROPERTY)
    var credentialsNonExpired: Boolean? = null,

    @JsonProperty(value = Credential.ACCOUNT_NON_LOCKED_PROPERTY)
    var accountNonLocked: Boolean? = null
)