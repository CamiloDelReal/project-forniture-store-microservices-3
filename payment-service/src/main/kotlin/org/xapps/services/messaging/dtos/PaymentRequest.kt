package org.xapps.services.messaging.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentRequest(
    @JsonProperty(value = ID_PROPERTY)
    val id: String,

    @JsonProperty(value = CUSTOMER_ID_PROPERTY)
    val customerId: Long,

    @JsonProperty(value = PRODUCTS_PROPERTY)
    val products: List<ProductRequest>
) {

    companion object {
        const val ID_PROPERTY = "id"
        const val CUSTOMER_ID_PROPERTY = "customerId"
        const val PRODUCTS_PROPERTY = "products"
    }

}
