package org.xapps.services.paymentservice.security

import com.fasterxml.jackson.annotation.JsonProperty

data class TokenValidateRequest(
    @JsonProperty(value = VALUE_PROPERTY)
    val value: String
) {

    companion object {
        const val VALUE_PROPERTY = "value"
    }

}