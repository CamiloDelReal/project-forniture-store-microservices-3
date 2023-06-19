package org.xapps.service.fornitureservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.service.fornitureservice.entities.Forniture

data class FornitureUpdateRequest(
    @JsonProperty(value = Forniture.NAME_PROPERTY)
    var name: String? = null,

    @JsonProperty(value = Forniture.DESCRIPTION_PROPERTY)
    var description: String? = null,

    @JsonProperty(value = Forniture.PRICE_PROPERTY)
    var price: Float? = null,

    @JsonProperty(value = Forniture.SMALL_PICTURE_PATH_PROPERTY)
    var smallPicturePath: String? = null,

    @JsonProperty(value = Forniture.LARGE_PICTURE_PATH_PROPERTY)
    var largePicturePath: String? = null
)