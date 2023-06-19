package org.xapps.services.authorizationservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import org.xapps.services.authorizationservice.entities.Credential

data class Login(
    @JsonProperty(value = Credential.USERNAME_PROPERTY)
    @NotNull(message = "Field Username cannot be empty")
    val username: String,

    @JsonProperty(value = Credential.PASSWORD_PROPERTY)
    @NotNull(message = "Field Password cannot be empty")
    val password: String
)