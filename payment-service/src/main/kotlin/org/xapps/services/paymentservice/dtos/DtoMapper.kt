package org.xapps.services.paymentservice.dtos

import org.xapps.services.paymentservice.entities.Invoice
import org.xapps.services.paymentservice.entities.Status

fun Invoice.toResponse(
    overrideId: Long? = null,
    overridePaymentId: String? = null,
    overrideStatus: Status? = null
): InvoiceResponse =
    InvoiceResponse(
        id = overrideId ?: id,
        paymentId = overridePaymentId ?: paymentId,
        status = overrideStatus ?: status
    )