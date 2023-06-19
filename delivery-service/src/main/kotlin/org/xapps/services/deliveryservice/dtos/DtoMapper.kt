package org.xapps.services.deliveryservice.dtos

import org.xapps.services.deliveryservice.entities.Delivery
import org.xapps.services.deliveryservice.entities.Product
import org.xapps.services.deliveryservice.entities.Status

fun Delivery.toResponse(
    overrideId: Long? = null,
    overrideTimestamp: Long? = null,
    overrideInvoiceId: Long? = null,
    overridePaymentId: String? = null,
    overrideStatus: Status? = null,
    overrideCustomerId: Long? = null,
    overrideFirstName: String? = null,
    overrideLastName: String? = null,
    overridePhone: String? = null,
    overrideAddressLine1: String? = null,
    overrideAddressLine2: String? = null,
    overrideCountry: String? = null,
    overrideCity: String? = null,
    overridePostalCode: Int? = null,
    overrideProducts: List<ProductResponse>? = null
): DeliveryResponse =
    DeliveryResponse(
        id = overrideId ?: id,
        timestamp = overrideTimestamp ?: timestamp,
        invoiceId = overrideInvoiceId ?: invoiceId,
        paymentId = overridePaymentId ?: paymentId,
        status = overrideStatus ?: status,
        customerId = overrideCustomerId ?: customerId,
        firstName = overrideFirstName ?: firstName,
        lastName = overrideLastName ?: lastName,
        phone = overridePhone ?: phone,
        addressLine1 = overrideAddressLine1 ?: addressLine1,
        addressLine2 = overrideAddressLine2 ?: addressLine2,
        country = overrideCountry ?: country,
        city = overrideCity ?: city,
        postalCode = overridePostalCode ?: postalCode,
        products = overrideProducts ?: products.map { it.toResponse() }
    )

fun Product.toResponse(
    overrideId: Long? = null,
    overrideUniqueId: String? = null,
    overrideName: String? = null,
    overrideCount: Int? = null
): ProductResponse =
    ProductResponse(
        id = overrideId ?: id,
        uniqueId = overrideUniqueId ?: uniqueId,
        name = overrideName ?: name,
        count = overrideCount ?: count
    )