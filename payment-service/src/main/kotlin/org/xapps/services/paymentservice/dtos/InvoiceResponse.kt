package org.xapps.services.paymentservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.paymentservice.entities.Invoice
import org.xapps.services.paymentservice.entities.Status

data class InvoiceResponse(
    @JsonProperty(value = Invoice.ID_PROPERTY)
    val id: Long,

    @JsonProperty(value = Invoice.PAYMENT_ID_PROPERTY)
    val paymentId: String,

    @JsonProperty(value = Invoice.STATUS_PROPERTY)
    val status: Status
)
