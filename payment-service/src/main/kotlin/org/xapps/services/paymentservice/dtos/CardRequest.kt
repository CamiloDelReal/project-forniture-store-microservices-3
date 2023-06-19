package org.xapps.services.paymentservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class CardRequest(
    @JsonProperty(value = NUMBER_PROPERTY)
    val number: String,

    @JsonProperty(value = MONTH_PROPERTY)
    val month: Int,

    @JsonProperty(value = YEAR_PROPERTY)
    val year: Int,

    @JsonProperty(value = CVV_PROPERTY)
    val cvv: Int
) {

    companion object {
        const val NUMBER_PROPERTY = "number"
        const val MONTH_PROPERTY = "month"
        const val YEAR_PROPERTY = "year"
        const val CVV_PROPERTY = "cvv"
    }

}
