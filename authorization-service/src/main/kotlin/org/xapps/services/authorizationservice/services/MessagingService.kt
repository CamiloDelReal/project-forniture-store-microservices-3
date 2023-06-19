package org.xapps.services.authorizationservice.services

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.xapps.services.authorizationservice.dtos.*

@Service
class MessagingService @Autowired constructor(
    @Value("\${messaging.rabbitmq.create-credentials.response.exchange}")
    private val createCredentialsExchange: String,

    @Value("\${messaging.rabbitmq.create-credentials.response.routing-key}")
    private val createCredentialsRoutingKey: String,

    @Value("\${messaging.rabbitmq.delete-credentials.response.exchange}")
    private val deleteCredentialsExchange: String,

    @Value("\${messaging.rabbitmq.delete-credentials.response.routing-key}")
    private val deleteCredentialsRoutingKey: String,

    private val rabbitTemplate: RabbitTemplate,

    @Lazy
    private val credentialService: CredentialService
){

    fun publishCredentialsCreated(request: CredentialResponseMessage) {
        rabbitTemplate.convertAndSend(createCredentialsExchange, createCredentialsRoutingKey, request)
    }

    fun publishCredentialsDeleted(request: CredentialDeletedMessage) {
        rabbitTemplate.convertAndSend(deleteCredentialsExchange, deleteCredentialsRoutingKey, request)
    }

    @RabbitListener(queues = ["\${messaging.rabbitmq.create-credentials.request.queue}"])
    fun processCreateCredentials(request: CredentialCreateRequest) {
        try {
            val response = credentialService.create(request)
            publishCredentialsCreated(CredentialResponseMessage(successful = true, customerId = response.customerId, response = response))
        } catch (ex: Exception) {
            publishCredentialsCreated(CredentialResponseMessage(successful = false, customerId = request.customerId))
        }
    }

    @RabbitListener(queues = ["\${messaging.rabbitmq.delete-credentials.request.queue}"])
    fun processDeleteCredentials(request: CredentialDeleteRequest) {
        try {
            credentialService.deleteByCustomerId(request.customerId)
            publishCredentialsDeleted(CredentialDeletedMessage(successful = true, customerId = request.customerId))
        } catch (ex: Exception) {
            publishCredentialsDeleted(CredentialDeletedMessage(successful = false, customerId = request.customerId))
        }
    }

}