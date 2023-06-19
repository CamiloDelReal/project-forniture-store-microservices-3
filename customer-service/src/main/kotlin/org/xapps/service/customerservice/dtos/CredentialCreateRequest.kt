package org.xapps.service.customerservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class CredentialCreateRequest(
    @JsonProperty(value = Credential.CUSTOMER_ID_PROPERTY)
    @NotNull(message = "Field Customer Id cannot be empty")
    var customerId: Long,

    @JsonProperty(value = Credential.USERNAME_PROPERTY)
    @NotNull(message = "Field username cannot be empty")
    var username: String,

    @JsonProperty(value = Credential.PASSWORD_PROPERTY)
    @NotNull(message = "Field password cannot be empty")
    val password: String,
)