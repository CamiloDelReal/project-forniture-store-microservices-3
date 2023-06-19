package org.xapps.services.authorizationservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.authorizationservice.entities.Token

data class Authorization(
    @JsonProperty(value = Token.TOKEN_PROPERTY)
    val token: String,

    @JsonProperty(value = Token.TYPE_PROPERTY)
    val type: String,

    @JsonProperty(value = Token.EXPIRATION_PROPERTY)
    val expiration: Long
)