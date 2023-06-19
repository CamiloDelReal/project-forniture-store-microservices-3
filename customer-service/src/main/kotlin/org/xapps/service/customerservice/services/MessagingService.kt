package org.xapps.service.customerservice.services

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.xapps.service.customerservice.dtos.*

@Service
class MessagingService @Autowired constructor(
    @Value("\${messaging.rabbitmq.create-credentials.request.exchange}")
    private val createCredentialsExchange: String,

    @Value("\${messaging.rabbitmq.create-credentials.request.routing-key}")
    private val createCredentialsRoutingKey: String,

    @Value("\${messaging.rabbitmq.delete-credentials.request.exchange}")
    private val deleteCredentialsExchange: String,

    @Value("\${messaging.rabbitmq.delete-credentials.request.routing-key}")
    private val deleteCredentialsRoutingKey: String,

    private val rabbitTemplate: RabbitTemplate,

    @Lazy
    private val customerService: CustomerService
){

    fun publishCreateCredentials(request: CredentialCreateRequest) {
        rabbitTemplate.convertAndSend(createCredentialsExchange, createCredentialsRoutingKey, request)
    }

    fun publishDeleteCredentials(request: CredentialDeleteRequest) {
        rabbitTemplate.convertAndSend(deleteCredentialsExchange, deleteCredentialsRoutingKey, request)
    }

    @RabbitListener(queues = ["\${messaging.rabbitmq.create-credentials.response.queue}"])
    fun processCredentialsCreated(response: CredentialResponseMessage) {
        println("Response receive $response")
        if (response.successful) {
            customerService.updateCustomerStatus(response.customerId)
        } else {
            customerService.delete(response.customerId)
        }
    }

    @RabbitListener(queues = ["\${messaging.rabbitmq.delete-credentials.response.queue}"])
    fun processCredentialsDeleted(response: CredentialDeletedMessage) {
        println("Response delete receive $response")
        if (response.successful) {
            customerService.deleteFromRepository(response.customerId)
        }
    }
}