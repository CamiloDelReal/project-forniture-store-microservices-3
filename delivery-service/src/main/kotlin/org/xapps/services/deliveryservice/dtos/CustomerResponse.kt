package org.xapps.services.deliveryservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class CustomerResponse(
    @JsonProperty(value = ID_PROPERTY)
    var id: Long? = null,

    @JsonProperty(value = LAST_NAME_PROPERTY)
    var lastName: String? = null,

    @JsonProperty(value = FIRST_NAME_PROPERTY)
    var firstName: String? = null,

    @JsonProperty(value = PHONE_PROPERTY)
    var phone: String? = null,

    @JsonProperty(value = ADDRESS_LINE_1_PROPERTY)
    var addressLine1: String? = null,

    @JsonProperty(value = ADDRESS_LINE_2_PROPERTY)
    var addressLine2: String? = null,

    @JsonProperty(value = COUNTRY_PROPERTY)
    var country: String? = null,

    @JsonProperty(value = CITY_PROPERTY)
    var city: String? = null,

    @JsonProperty(value = POSTAL_CODE_PROPERTY)
    var postalCode: Int? = null
) {

    companion object {
        const val ID_PROPERTY = "id"
        const val LAST_NAME_PROPERTY = "lastName"
        const val FIRST_NAME_PROPERTY = "firstName"
        const val PHONE_PROPERTY = "phone"
        const val ADDRESS_LINE_1_PROPERTY = "addressLine1"
        const val ADDRESS_LINE_2_PROPERTY = "addressLine2"
        const val COUNTRY_PROPERTY = "country"
        const val CITY_PROPERTY = "city"
        const val POSTAL_CODE_PROPERTY = "postalCode"
    }

}