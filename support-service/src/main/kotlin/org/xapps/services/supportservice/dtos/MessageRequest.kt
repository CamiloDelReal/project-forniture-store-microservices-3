package org.xapps.services.supportservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.supportservice.entities.Message

data class MessageRequest(
    @JsonProperty(value = Message.VALUE_PROPERTY)
    val value: String
)