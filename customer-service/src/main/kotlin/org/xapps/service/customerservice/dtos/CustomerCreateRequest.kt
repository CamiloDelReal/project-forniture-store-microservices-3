package org.xapps.service.customerservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import org.xapps.service.customerservice.entities.Customer

data class CustomerCreateRequest(
    @JsonProperty(value = Customer.USERNAME_PROPERTY)
    @NotNull(message = "Field Username cannot be empty")
    var username: String,

    @JsonProperty(value = Customer.PASSWORD_PROPERTY)
    @NotNull(message = "Field Password cannot be empty")
    var password: String,

    @JsonProperty(value = Customer.LAST_NAME_PROPERTY)
    @NotNull(message = "Field Last Name cannot be empty")
    var lastName: String,

    @JsonProperty(value = Customer.FIRST_NAME_PROPERTY)
    @NotNull(message = "Field First Name cannot be empty")
    var firstName: String,

    @JsonProperty(value = Customer.EMAIL_PROPERTY)
    @NotNull(message = "Field Email cannot be empty")
    var email: String,

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
    var postalCode: Int? = null
)