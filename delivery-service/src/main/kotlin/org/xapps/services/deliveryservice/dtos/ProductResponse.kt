package org.xapps.services.deliveryservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.deliveryservice.entities.Product

data class ProductResponse(
    @JsonProperty(value = Product.ID_PROPERTY)
    val id: Long,

    @JsonProperty(value = Product.UNIQUE_ID_PROPERTY)
    val uniqueId: String,

    @JsonProperty(value = Product.NAME_PROPERTY)
    val name: String,

    @JsonProperty(value = Product.COUNT_PROPERTY)
    val count: Int
)
