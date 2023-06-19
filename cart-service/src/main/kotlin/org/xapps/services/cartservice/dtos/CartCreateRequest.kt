package org.xapps.services.cartservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import org.xapps.services.cartservice.entities.Cart

data class CartCreateRequest(
    @JsonProperty(value = Cart.CUSTOMER_ID_PROPERTY)
    @NotNull(message = "Field Customer ID cannot be empty")
    val customerId: Long,

    @JsonProperty(value = Cart.FORNITURES_PROPERTY)
    @NotNull(message = "Field Fornitures cannot be empty")
    @NotEmpty(message = "Field Fornitures cannot be empty")
    val fornitures: Map<String, Int>
)