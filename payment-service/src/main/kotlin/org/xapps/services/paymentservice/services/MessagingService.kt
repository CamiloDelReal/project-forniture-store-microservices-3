package org.xapps.services.paymentservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Lazy
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import org.xapps.services.messaging.dtos.PaymentCompletionRequest
import org.xapps.services.messaging.dtos.PaymentRequest
import java.util.*

@Service
class MessagingService @Autowired constructor(
    @Lazy
    private val paymentService: PaymentService,

    private val paymentKafkaTemplate: KafkaTemplate<String, Any>,

    @Value("\${spring.kafka.topics.paymentCompletion}")
    private val paymentCompletionTopic: String
) {

    @KafkaListener(topics = ["\${spring.kafka.topics.requestPayment}"])
    fun processPaymentRequest(request: PaymentRequest) {
        println("Payment request received  $request")
        paymentService.processPayment(request)
    }

    fun sendPaymentCompletion(request: PaymentCompletionRequest) {
        paymentKafkaTemplate.send(paymentCompletionTopic, UUID.randomUUID().toString(), request)
    }

}