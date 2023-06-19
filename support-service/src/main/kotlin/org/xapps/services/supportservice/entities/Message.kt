package org.xapps.services.supportservice.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = Message.COLLECTION_NAME)
data class Message(
    @Id
    @Indexed
    @JsonProperty(value = ID_PROPERTY)
    val id: String? = null,

    @JsonProperty(value = CHAT_ID_PROPERTY)
    val chatId: String,

    @JsonProperty(value = TIMESTAMP_PROPERTY)
    val timestamp: Long,

    @JsonProperty(value = VALUE_PROPERTY)
    val value: String
) {

    companion object {
        const val COLLECTION_NAME = "messages"

        const val ID_PROPERTY = "id"
        const val CHAT_ID_PROPERTY = "chatId"
        const val TIMESTAMP_PROPERTY = "timestamp"
        const val VALUE_PROPERTY = "value"
    }

}