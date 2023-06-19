package org.xapps.service.customerservice.dtos

import org.xapps.service.customerservice.entities.Customer
import org.xapps.service.customerservice.entities.CustomerStatus

fun Customer.toResponse(
    overrideId: Long? = null,
    overrideLastName: String? = null,
    overrideFirstName: String? = null,
    overrideEmail: String? = null,
    overridePhone: String? = null,
    overrideAddressLine1: String? = null,
    overrideAddressLine2: String? = null,
    overrideCountry: String? = null,
    overrideCity: String? = null,
    overridePostalCode: Int? = null,
    overrideUsername: String? = null,
    overrideStatus: CustomerStatus? = null
): CustomerResponse =
    CustomerResponse(
        id = overrideId ?: id,
        lastName = overrideLastName ?: lastName,
        firstName = overrideFirstName ?: firstName,
        email = overrideEmail ?: email,
        phone = overridePhone ?: phone,
        addressLine1 = overrideAddressLine1 ?: addressLine1,
        addressLine2 = overrideAddressLine2 ?: addressLine2,
        country = overrideCountry ?: country,
        city = overrideCity ?: city,
        postalCode = overridePostalCode ?: postalCode,
        username = overrideUsername,
        status = overrideStatus ?: status
    )

fun CustomerCreateRequest.toCustomer(): Customer =
    Customer(
        lastName = lastName,
        firstName = firstName,
        email = email,
        phone = phone,
        addressLine1 = addressLine1,
        addressLine2 = addressLine2,
        country = country,
        city = city,
        postalCode = postalCode,
        status = CustomerStatus.IN_PROGRESS
    )