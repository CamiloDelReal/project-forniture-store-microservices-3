package org.xapps.services.cartservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.cartservice.entities.Cart

data class CartResponse(
    @JsonProperty(value = Cart.CUSTOMER_ID_PROPERTY)
    val customerId: Long,

    @JsonProperty(value = Cart.FORNITURES_PROPERTY)
    val fornitures: Map<String, Int>
)