package org.xapps.services.deliveryservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.deliveryservice.entities.Delivery
import org.xapps.services.deliveryservice.entities.Status

data class DeliveryResponse(
    @JsonProperty(value = Delivery.ID_PROPERTY)
    private val id: Long,

    @JsonProperty(value = Delivery.TIMESTAMP_PROPERTY)
    private val timestamp: Long,

    @JsonProperty(value = Delivery.INVOICE_ID_PROPERTY)
    private val invoiceId: Long,

    @JsonProperty(value = Delivery.PAYMENT_ID_PROPERTY)
    private val paymentId: String,

    @JsonProperty(value = Delivery.CUSTOMER_ID_PROPERTY)
    private val customerId: Long,

    @JsonProperty(value = Delivery.FIRST_NAME_PROPERTY)
    private val firstName: String? = null,

    @JsonProperty(value = Delivery.LAST_NAME_PROPERTY)
    private val lastName: String? = null,

    @JsonProperty(value = Delivery.PHONE_PROPERTY)
    private val phone: String? = null,

    @JsonProperty(value = Delivery.ADDRESS_LINE_1_PROPERTY)
    private val addressLine1: String? = null,

    @JsonProperty(value = Delivery.ADDRESS_LINE_2_PROPERTY)
    private val addressLine2: String? = null,

    @JsonProperty(value = Delivery.COUNTRY_PROPERTY)
    private val country: String? = null,

    @JsonProperty(value = Delivery.CITY_PROPERTY)
    private val city: String? = null,

    @JsonProperty(value = Delivery.POSTAL_CODE_PROPERTY)
    private val postalCode: Int? = null,

    @JsonProperty(value = Delivery.STATUS_PROPERTY)
    private val status: Status,

    @JsonProperty(value = Delivery.PRODUCTS_PROPERTY)
    private val products: List<ProductResponse>
)
