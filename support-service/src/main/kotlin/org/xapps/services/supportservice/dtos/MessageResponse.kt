package org.xapps.services.supportservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.supportservice.entities.Message

data class MessageResponse(
    @JsonProperty(value = Message.ID_PROPERTY)
    val id: String,

    @JsonProperty(value = Message.TIMESTAMP_PROPERTY)
    val timestamp: Long,

    @JsonProperty(value = Message.VALUE_PROPERTY)
    val value: String
)
