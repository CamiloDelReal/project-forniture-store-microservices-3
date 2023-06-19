package org.xapps.service.fornitureservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.service.fornitureservice.entities.Comment

data class CommentUpdateRequest(
    @JsonProperty(value = Comment.EVALUATION_PROPERTY)
    var evaluation: Int? = null,

    @JsonProperty(value = Comment.VALUE_PROPERTY)
    var value: String? = null
)