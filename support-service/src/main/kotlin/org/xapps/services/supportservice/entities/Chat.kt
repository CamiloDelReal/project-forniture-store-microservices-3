package org.xapps.services.supportservice.entities

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = Chat.COLLECTION_NAME)
data class Chat(
    @Id
    @Indexed
    @JsonProperty(value = ID_PROPERTY)
    val id: String? = null,

    @JsonProperty(value = CREATED_AT_PROPERTY)
    val createdAt: Long,

    @JsonProperty(value = STATUS_PROPERTY)
    var status: Status,

    @JsonProperty(value = LAST_STATUS_CHANGED_AT_PROPERTY)
    var lastStatusChangedAt: Long,

    @JsonProperty(value = CUSTOMER_ID_PROPERTY)
    val customerId: Long,

    @JsonProperty(value = CUSTOMER_PROPERTY)
    val customer: String,

    @JsonProperty(value = SUPPORTER_ID_PROPERTY)
    var supporterId: Long? = null,

    @JsonProperty(value = SUPPORTER_PROPERTY)
    var supporter: String? = null
) {

    companion object {
        const val COLLECTION_NAME = "chats"

        const val ID_PROPERTY = "id"
        const val CREATED_AT_PROPERTY = "createdAt"
        const val STATUS_PROPERTY = "status"
        const val LAST_STATUS_CHANGED_AT_PROPERTY = "lastStatusChangedAt"
        const val CUSTOMER_ID_PROPERTY = "customerId"
        const val SUPPORTER_ID_PROPERTY = "supporterId"
        const val CUSTOMER_PROPERTY = "customer"
        const val SUPPORTER_PROPERTY = "supporter"
    }

}