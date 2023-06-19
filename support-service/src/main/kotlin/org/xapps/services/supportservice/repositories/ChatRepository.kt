package org.xapps.services.supportservice.repositories

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.data.mongodb.repository.Tailable
import org.springframework.stereotype.Repository
import org.xapps.services.supportservice.entities.Chat
import reactor.core.publisher.Flux

@Repository
interface ChatRepository : ReactiveMongoRepository<Chat, String> {

    @Tailable
    fun findWithTailableCursorById(id: String): Flux<Chat>

    fun readById(id: String): Flux<Chat>
}