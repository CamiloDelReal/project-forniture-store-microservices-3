package org.xapps.services.authorizationservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.authorizationservice.entities.Credential

data class CredentialResponse(
    @JsonProperty(value = Credential.ID_PROPERTY)
    var id: Long = 0,

    @JsonProperty(value = Credential.CUSTOMER_ID_PROPERTY)
    var customerId: Long,

    @JsonProperty(value = Credential.USERNAME_PROPERTY)
    var username: String? = null,

    @JsonProperty(value = Credential.ROLES_PROPERTY)
    var roles: List<String>,

    @JsonProperty(value = Credential.ENABLED_PROPERTY)
    var enabled: Boolean,

    @JsonProperty(value = Credential.ACCOUNT_NON_EXPIRED_PROPERTY)
    var accountNonExpired: Boolean,

    @JsonProperty(value = Credential.CREDENTIALS_NON_EXPIRED_PROPERTY)
    var credentialsNonExpired: Boolean,

    @JsonProperty(value = Credential.ACCOUNT_NON_LOCKED_PROPERTY)
    var accountNonLocked: Boolean
)