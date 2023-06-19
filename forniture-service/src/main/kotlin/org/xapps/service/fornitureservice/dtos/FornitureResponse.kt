package org.xapps.service.fornitureservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.service.fornitureservice.entities.Forniture

data class FornitureResponse(
    @JsonProperty(value = Forniture.ID_PROPERTY)
    var id: String,

    @JsonProperty(value = Forniture.NAME_PROPERTY)
    var name: String,

    @JsonProperty(value = Forniture.DESCRIPTION_PROPERTY)
    var description: String,

    @JsonProperty(value = Forniture.PRICE_PROPERTY)
    var price: Float,

    @JsonProperty(value = Forniture.SMALL_PICTURE_PATH_PROPERTY)
    var smallPicturePath: String?,

    @JsonProperty(value = Forniture.LARGE_PICTURE_PATH_PROPERTY)
    var largePicturePath: String?
)