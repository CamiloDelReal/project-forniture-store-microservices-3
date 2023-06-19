package org.xapps.services.authorizationservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import org.xapps.services.authorizationservice.entities.Token

data class TokenValidateRequest(
    @JsonProperty(value = Token.VALUE_PROPERTY)
    @NotNull(message = "Field token cannot be empty")
    val value: String
)