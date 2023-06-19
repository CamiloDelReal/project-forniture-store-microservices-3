package org.xapps.services.cartservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.cartservice.entities.Cart

data class CartUpdateRequest(
    @JsonProperty(Cart.FORNITURES_PROPERTY)
    val fornitures: Map<String, Int>
)