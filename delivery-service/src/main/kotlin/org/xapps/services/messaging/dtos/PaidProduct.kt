package org.xapps.services.messaging.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class PaidProduct(
    @JsonProperty(value = ID_PROPERTY)
    val id: String,

    @JsonProperty(value = NAME_PROPERTY)
    val name: String,

    @JsonProperty(value = COUNT_PROPERTY)
    val count: Int
) {

    companion object {
        const val ID_PROPERTY = "id"
        const val NAME_PROPERTY = "name"
        const val COUNT_PROPERTY = "count"
    }

}
