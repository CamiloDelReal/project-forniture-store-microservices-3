package org.xapps.services.cartservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.xapps.services.messaging.dtos.PaymentRequest
import java.util.*

@Service
class MessagingService @Autowired constructor(
    private val paymentKafkaTemplate: KafkaTemplate<String, Any>,

    @Value("\${spring.kafka.topics.requestPayment}")
    private val paymentRequestTopic: String
){

    fun sendPaymentRequest(request: PaymentRequest) {
        paymentKafkaTemplate.send(paymentRequestTopic, UUID.randomUUID().toString(), request)
    }

}