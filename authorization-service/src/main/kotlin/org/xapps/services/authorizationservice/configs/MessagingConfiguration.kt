package org.xapps.services.authorizationservice.configs

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingConfiguration constructor(
    @Value("\${messaging.rabbitmq.host}")
    private val host: String,

    @Value("\${messaging.rabbitmq.port}")
    private val port: Int,

    @Value("\${messaging.rabbitmq.username}")
    private val username: String,

    @Value("\${messaging.rabbitmq.password}")
    private val password: String,

    @Value("\${messaging.rabbitmq.create-credentials.response.queue}")
    private val createCredentialsQueue: String,

    @Value("\${messaging.rabbitmq.create-credentials.response.exchange}")
    private val createCredentialsExchange: String,

    @Value("\${messaging.rabbitmq.create-credentials.response.routing-key}")
    private val createCredentialsRoutingKey: String,

    @Value("\${messaging.rabbitmq.delete-credentials.response.queue}")
    private val deleteCredentialsQueue: String,

    @Value("\${messaging.rabbitmq.delete-credentials.response.exchange}")
    private val deleteCredentialsExchange: String,

    @Value("\${messaging.rabbitmq.delete-credentials.response.routing-key}")
    private val deleteCredentialsRoutingKey: String
) {

    @Bean("credentialsConnectionFactory")
    fun connectionFactory(): ConnectionFactory? {
        val connectionFactory = CachingConnectionFactory()
        connectionFactory.host = host
        connectionFactory.port = port
        connectionFactory.username = username
        connectionFactory.setPassword(password)
        return connectionFactory
    }

    @Bean("credentialsCreatedQueue")
    fun provideCreateCredentialsQueue(): Queue? {
        return Queue(createCredentialsQueue)
    }

    @Bean("credentialsCreatedExchange")
    fun provideCreateCredentialsExchange(): TopicExchange? {
        return TopicExchange(createCredentialsExchange)
    }

    @Bean("createCredentialsBinding")
    fun provideCreateCredentialsBinding(
        @Qualifier("credentialsCreatedQueue") queue: Queue?,
        @Qualifier("credentialsCreatedExchange") exchange: TopicExchange?
    ): Binding? {
        return BindingBuilder.bind(queue).to(exchange).with(createCredentialsRoutingKey)
    }

    @Bean("deleteCredentialsQueue")
    fun provideDeleteCredentialsQueue(): Queue? {
        return Queue(deleteCredentialsQueue)
    }

    @Bean("deleteCredentialsExchange")
    fun provideDeleteCredentialsExchange(): TopicExchange? {
        return TopicExchange(deleteCredentialsExchange)
    }

    @Bean("deleteCredentialsBinding")
    fun provideDeleteCredentialsBinding(
        @Qualifier("deleteCredentialsQueue") queue: Queue?,
        @Qualifier("deleteCredentialsExchange") exchange: TopicExchange?
    ): Binding? {
        return BindingBuilder.bind(queue).to(exchange).with(deleteCredentialsRoutingKey)
    }

    @Bean("messageConverter")
    fun provideMessageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun provideTemplate(
        @Qualifier("credentialsConnectionFactory") connectionFactory: ConnectionFactory,
        @Qualifier("messageConverter") messageConverter: MessageConverter
    ): AmqpTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter
        return template
    }
}