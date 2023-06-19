package org.xapps.services.messaging.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentCompletionRequest(
    @JsonProperty(value = INVOICE_ID_PROPERTY)
    val invoiceId: Long,

    @JsonProperty(value = PAYMENT_ID_PROPERTY)
    val paymentId: String,

    @JsonProperty(value = CUSTOMER_ID_PROPERTY)
    val customerId: Long,

    @JsonProperty(value = PRODUCTS_PROPERTY)
    val products: List<PaidProduct>
) {

    companion object {
        const val INVOICE_ID_PROPERTY = "invoiceId"
        const val PAYMENT_ID_PROPERTY = "paymentId"
        const val CUSTOMER_ID_PROPERTY = "customerId"
        const val PRODUCTS_PROPERTY = "products"
    }

}
