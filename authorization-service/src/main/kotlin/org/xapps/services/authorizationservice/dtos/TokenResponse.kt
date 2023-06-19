package org.xapps.services.authorizationservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.authorizationservice.entities.Token

data class TokenResponse(
    @JsonProperty(value = Token.ID_PROPERTY)
    var id: String,

    @JsonProperty(value = Token.CREDENTIAL_ID_PROPERTY)
    var credentialId: Long,

    @JsonProperty(value = Token.EXPIRATION_PROPERTY)
    var expiration: Long
)