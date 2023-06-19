package org.xapps.services.supportservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import org.xapps.services.supportservice.entities.Chat
import org.xapps.services.supportservice.entities.Status

data class ChatResponse(
    @JsonProperty(Chat.ID_PROPERTY)
    val id: String,

    @JsonProperty(value = Chat.CREATED_AT_PROPERTY)
    val createdAt: Long,

    @JsonProperty(value = Chat.STATUS_PROPERTY)
    var status: Status,

    @JsonProperty(value = Chat.CUSTOMER_PROPERTY)
    val customer: String,

    @JsonProperty(value = Chat.SUPPORTER_PROPERTY)
    val supporter: String? = null
)