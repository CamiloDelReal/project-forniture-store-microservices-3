package org.xapps.services.cartservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty

data class FornitureResponse(
    @JsonProperty(value = ID_PROPERTY)
    var id: String,

    @JsonProperty(value = NAME_PROPERTY)
    var name: String,

    @JsonProperty(value = DESCRIPTION_PROPERTY)
    var description: String,

    @JsonProperty(value = PRICE_PROPERTY)
    var price: Float,

    @JsonProperty(value = SMALL_PICTURE_PATH_PROPERTY)
    var smallPicturePath: String?,

    @JsonProperty(value = LARGE_PICTURE_PATH_PROPERTY)
    var largePicturePath: String?
) {

    companion object {
        const val ID_PROPERTY = "id"
        const val NAME_PROPERTY = "name"
        const val DESCRIPTION_PROPERTY = "description"
        const val PRICE_PROPERTY = "price"
        const val SMALL_PICTURE_PATH_PROPERTY = "smallPicturePath"
        const val LARGE_PICTURE_PATH_PROPERTY = "largePicturePath"
    }

}