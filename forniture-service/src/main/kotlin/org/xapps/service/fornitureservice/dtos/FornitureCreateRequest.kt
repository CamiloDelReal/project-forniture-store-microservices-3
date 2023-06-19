package org.xapps.service.fornitureservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import org.xapps.service.fornitureservice.entities.Forniture

data class FornitureCreateRequest(
    @JsonProperty(value = Forniture.NAME_PROPERTY)
    @NotNull(message = "Field Name cannot be empty")
    var name: String,

    @JsonProperty(value = Forniture.DESCRIPTION_PROPERTY)
    @NotNull(message = "Field Description cannot be empty")
    var description: String,

    @JsonProperty(value = Forniture.PRICE_PROPERTY)
    @NotNull(message = "Field Price cannot be empty")
    var price: Float,

    @JsonProperty(value = Forniture.SMALL_PICTURE_PATH_PROPERTY)
    var smallPicturePath: String? = null,

    @JsonProperty(value = Forniture.LARGE_PICTURE_PATH_PROPERTY)
    var largePicturePath: String? = null
)