package org.xapps.services.supportservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class CustomerResponse(
    @JsonProperty(value = ID_PROPERTY)
    var id: Long? = null,

    @JsonProperty(value = LAST_NAME_PROPERTY)
    var lastName: String? = null,

    @JsonProperty(value = FIRST_NAME_PROPERTY)
    var firstName: String? = null
) {

    companion object {
        const val ID_PROPERTY = "id"
        const val LAST_NAME_PROPERTY = "lastName"
        const val FIRST_NAME_PROPERTY = "firstName"
    }

}