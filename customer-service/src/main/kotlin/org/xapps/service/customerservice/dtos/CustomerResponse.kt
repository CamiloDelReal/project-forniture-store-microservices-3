package org.xapps.service.customerservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.service.customerservice.entities.Customer
import org.xapps.service.customerservice.entities.CustomerStatus
import java.io.Serializable

data class CustomerResponse(
    @JsonProperty(value = Customer.ID_PROPERTY)
    var id: Long? = null,

    @JsonProperty(value = Customer.LAST_NAME_PROPERTY)
    var lastName: String? = null,

    @JsonProperty(value = Customer.FIRST_NAME_PROPERTY)
    var firstName: String? = null,

    @JsonProperty(value = Customer.EMAIL_PROPERTY)
    var email: String? = null,

    @JsonProperty(value = Customer.PHONE_PROPERTY)
    var phone: String? = null,

    @JsonProperty(value = Customer.ADDRESS_LINE_1_PROPERTY)
    var addressLine1: String? = null,

    @JsonProperty(value = Customer.ADDRESS_LINE_2_PROPERTY)
    var addressLine2: String? = null,

    @JsonProperty(value = Customer.COUNTRY_PROPERTY)
    var country: String? = null,

    @JsonProperty(value = Customer.CITY_PROPERTY)
    var city: String? = null,

    @JsonProperty(value = Customer.POSTAL_CODE_PROPERTY)
    var postalCode: Int? = null,

    @JsonProperty(value = Customer.USERNAME_PROPERTY)
    var username: String? = null,

    @JsonProperty(value = Customer.STATUS_PROPERTY)
    var status: CustomerStatus
): Serializable