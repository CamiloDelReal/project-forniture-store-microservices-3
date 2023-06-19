package org.xapps.service.fornitureservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.service.fornitureservice.entities.Comment

data class CommentResponse(
    @JsonProperty(value = Comment.ID_PROPERTY)
    var id: Long = 0,

    @JsonProperty(value = Comment.FORNITURE_ID_PROPERTY)
    var fornitureId: String,

    @JsonProperty(value = Comment.CUSTOMER_ID_PROPERTY)
    var customerId: Long,

    @JsonProperty(value = Comment.EVALUATION_PROPERTY)
    var evaluation: Int,

    @JsonProperty(value = Comment.VALUE_PROPERTY)
    var value: String
)