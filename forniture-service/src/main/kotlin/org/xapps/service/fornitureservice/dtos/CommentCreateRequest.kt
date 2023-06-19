package org.xapps.service.fornitureservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Length
import org.xapps.service.fornitureservice.entities.Comment

data class CommentCreateRequest(
    @JsonProperty(value = Comment.FORNITURE_ID_PROPERTY)
    @NotNull(message = "Field Forniture Id cannot be empty")
    var fornitureId: String,

    @JsonProperty(value = Comment.CUSTOMER_ID_PROPERTY)
    @NotNull(message = "Field Customer Id cannot be empty")
    var customerId: Long,

    @JsonProperty(value = Comment.EVALUATION_PROPERTY)
    @NotNull(message = "Field Evaluation cannot be empty")
    @Min(0)
    @Max(10)
    var evaluation: Int,

    @JsonProperty(value = Comment.VALUE_PROPERTY)
    @NotNull(message = "Field Value cannot be empty")
    @Length(min = 0, max = 256)
    var value: String
)