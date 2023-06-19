package org.xapps.services.supportservice.repositories

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Tailable
import org.springframework.stereotype.Repository
import org.xapps.services.supportservice.entities.Message
import reactor.core.publisher.Flux

@Repository
interface MessageRepository : ReactiveMongoRepository<Message, String> {

    @Tailable
    fun findByChatId(chatId: String): Flux<Message>

}