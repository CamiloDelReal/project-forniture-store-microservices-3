package org.xapps.services.deliveryservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import org.xapps.services.deliveryservice.entities.Delivery
import org.xapps.services.deliveryservice.entities.Product
import org.xapps.services.deliveryservice.entities.Status
import org.xapps.services.messaging.dtos.PaymentCompletionRequest
import java.time.Instant

@Service
class MessagingService @Autowired constructor(
    private val deliveryService: DeliveryService
) {

    @KafkaListener(topics = ["\${spring.kafka.topics.paymentCompletion}"])
    fun processPaymentRequest(request: PaymentCompletionRequest) {
        println("Completion request received  $request")
        val delivery = Delivery(
            timestamp = Instant.now().toEpochMilli(),
            invoiceId = request.invoiceId,
            paymentId = request.paymentId,
            customerId = request.customerId,
            status = Status.PENDING,
            products = request.products.map {
                Product(
                    uniqueId = it.id,
                    name = it.name,
                    count = it.count
                )
            }
        )
        deliveryService.processDelivery(delivery)
    }

}