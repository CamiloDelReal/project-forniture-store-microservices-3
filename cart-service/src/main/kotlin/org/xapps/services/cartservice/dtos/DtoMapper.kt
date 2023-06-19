package org.xapps.services.cartservice.dtos

import org.xapps.services.cartservice.entities.Cart

fun CartCreateRequest.toCart(
    overrideCustomerId: Long? = null,
    overrideFornitures: Map<String, Int>? = null
): Cart =
    Cart(
        customerId = overrideCustomerId ?: customerId,
        fornitures = mutableMapOf<String, Int>().apply {
            putAll(overrideFornitures ?: fornitures)
        }
    )

fun Cart.toResponse(
    overrideCustomerId: Long? = null,
    overrideFornitures: Map<String, Int>? = null
): CartResponse =
    CartResponse(
        customerId = overrideCustomerId ?: customerId,
        fornitures = mutableMapOf<String, Int>().apply {
            putAll(overrideFornitures ?: fornitures)
        }
    )