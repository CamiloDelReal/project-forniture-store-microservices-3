package org.xapps.services.supportservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.xapps.services.supportservice.dtos.ChatResponse
import org.xapps.services.supportservice.dtos.MessageRequest
import org.xapps.services.supportservice.dtos.MessageResponse
import org.xapps.services.supportservice.dtos.toResponse
import org.xapps.services.supportservice.entities.Chat
import org.xapps.services.supportservice.entities.Message
import org.xapps.services.supportservice.entities.Status
import org.xapps.services.supportservice.repositories.ChatRepository
import org.xapps.services.supportservice.repositories.MessageRepository
import org.xapps.services.supportservice.services.exceptions.CustomerNotFound
import org.xapps.services.supportservice.services.exceptions.IdNotFound
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.time.Instant


@Service
class SupportService @Autowired constructor(
    private val chatRepository: ChatRepository,
    private val customerService: CustomerService,
    private val messageRepository: MessageRepository
) {

    fun request(authzHeader: String, customerId: Long): Mono<ChatResponse> =
        customerService.read(authzHeader)
            .flatMap { customer ->
                chatRepository.save(
                    Chat(
                        createdAt = Instant.now().toEpochMilli(),
                        status = Status.PENDING,
                        lastStatusChangedAt = Instant.now().toEpochMilli(),
                        customerId = customer.id!!,
                        customer = "${customer?.firstName} ${customer?.lastName}",
                        supporterId = null
                    )
                ).map { it.toResponse() }
            }
            .switchIfEmpty {
                Mono.error(CustomerNotFound("Customer with id $customerId not found"))
            }


    fun response(authzHeader: String, chatId: String, supporterId: Long): Mono<ChatResponse> =
        customerService.read(authzHeader)
            .flatMap { supporter ->
                chatRepository.findById(chatId)
                    .flatMap { chat ->
                        chat.status = Status.IN_PROGRESS
                        chat.supporterId = supporter.id
                        chat.supporter = "${supporter.firstName} ${supporter.lastName}"
                        chatRepository.save(chat).map { it.toResponse() }
                    }
                    .switchIfEmpty {
                        Mono.error(IdNotFound("Chat with $chatId nor found"))
                    }
            }
            .switchIfEmpty {
                Mono.error(CustomerNotFound("Supporter with id $supporterId not found"))
            }

    fun canCustomerAccessChat(chatId: String, customerId: Long): Mono<Boolean> =
        chatRepository.findById(chatId)
            .flatMap { chat ->
                Mono.just(chat.customerId == customerId)
            }
            .switchIfEmpty {
                Mono.error(IdNotFound("Chat with id $chatId not found"))
            }

    fun monitorChat(chatId: String): Flux<ChatResponse> =
        chatRepository.findWithTailableCursorById(chatId)
            .map { chat ->
                chat.toResponse()
            }
            .switchIfEmpty {
                Flux.error<ChatResponse>(IdNotFound("Chat with Id $chatId not found"))
            }

    fun closeChat(chatId: String): Mono<ChatResponse> =
        chatRepository.findById(chatId)
            .flatMap { chat ->
                chat.status = Status.CLOSED
                chatRepository.save(chat)
                    .map {
                        it.toResponse()
                    }
            }
            .switchIfEmpty {
                Mono.error(IdNotFound("Chat with id $chatId not found"))
            }

    fun monitorChatMessages(chatId: String): Flux<Message> =
        messageRepository.findByChatId(chatId)

    fun sendMessage(chatId: String, request: MessageRequest): Mono<MessageResponse> =
        messageRepository.save(
            Message(
                chatId = chatId,
                timestamp = Instant.now().toEpochMilli(),
                value = request.value
            )
        ).map { it.toResponse() }

}