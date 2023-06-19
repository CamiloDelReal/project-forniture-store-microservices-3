package org.xapps.services.supportservice.dtos

import org.xapps.services.supportservice.entities.Chat
import org.xapps.services.supportservice.entities.Message
import org.xapps.services.supportservice.entities.Status

fun Chat.toResponse(
    overrideId: String? = null,
    overrideCreateAt: Long? = null,
    overrideStatus: Status? = null,
    overrideCustomer: String? = null,
    overrideSupporter: String? = null
): ChatResponse =
    ChatResponse(
        id = overrideId ?: id ?: "invalid",
        createdAt = overrideCreateAt ?: createdAt,
        status = overrideStatus ?: status,
        customer = overrideCustomer ?: customer,
        supporter = overrideSupporter ?: supporter
    )

fun Message.toResponse(
    overrideId: String? = null,
    overrideTimestamp: Long? = null,
    overrideValue: String? = null
): MessageResponse =
    MessageResponse(
        id = overrideId ?: id ?: "invalid",
        timestamp = overrideTimestamp ?: timestamp,
        value = overrideValue ?: value
    )