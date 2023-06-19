package org.xapps.services.paymentservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.xapps.services.messaging.dtos.PaidProduct
import org.xapps.services.messaging.dtos.PaymentCompletionRequest
import org.xapps.services.messaging.dtos.PaymentRequest
import org.xapps.services.paymentservice.dtos.CardRequest
import org.xapps.services.paymentservice.dtos.InvoiceResponse
import org.xapps.services.paymentservice.dtos.toResponse
import org.xapps.services.paymentservice.entities.Invoice
import org.xapps.services.paymentservice.entities.Product
import org.xapps.services.paymentservice.entities.Status
import org.xapps.services.paymentservice.repositories.InvoiceRepository
import org.xapps.services.paymentservice.repositories.ProductRepository
import org.xapps.services.paymentservice.services.exceptions.InvoiceNotFoundException
import org.xapps.services.paymentservice.services.exceptions.PaymentCompletionException
import org.xapps.services.paymentservice.services.exceptions.PaymentDeniedException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Service
class PaymentService @Autowired constructor(
    @Value("\${payments.monitor.retryMaxAttempts}")
    private val retryMaxAttempts: Long,

    @Value("\${payments.monitor.retryMinBackoff}")
    private val retryMinBackoff: Long,

    @Value("\${payments.monitor.retryMaxBackoff}")
    private val retryMaxBackoff: Long,

    private val invoiceRepository: InvoiceRepository,
    private val productRepository: ProductRepository,
    private val cardValidatorService: CardValidatorService,
    private val messagingService: MessagingService
) {

    fun readAll(from: Int, size: Int): Flux<Invoice> =
        invoiceRepository.readPage(from, size)

    fun readById(id: Long): Mono<Invoice> =
        invoiceRepository.findById(id)

    fun readByPaymentId(paymentId: String): Mono<Invoice> =
        invoiceRepository.findByPaymentId(paymentId)
            .retryWhen(
                Retry
                    .backoff(retryMaxAttempts, Duration.ofMillis(retryMinBackoff))
                    .maxBackoff(Duration.ofMillis(retryMaxBackoff))
            )


    fun readByCustomerId(customerId: Long, from: Int, size: Int): Flux<Invoice> =
        invoiceRepository.readCustomerInvoicesPage(customerId, from, size)

    fun processPayment(request: PaymentRequest) {
        val invoice = Invoice(
            paymentId = request.id,
            customerId = request.customerId,
            status = Status.WAITING_PAYMENT
        )
        val products = request.products.map {
            Product(
                uniqueId = it.id,
                name = it.name,
                price = it.price,
                count = it.count
            )
        }
        invoiceRepository.save(invoice).block()
        productRepository.saveAll(products).blockLast()
    }

    fun executePayment(paymentId: String, card: CardRequest): Mono<InvoiceResponse> =
        invoiceRepository.findByPaymentId(paymentId)
            .flatMap { invoice ->
                if(cardValidatorService.validateCard(card)) {
                    invoice.status = Status.COMPLETED
                    invoiceRepository.save(invoice)
                        .map { savedInvoice ->
                            messagingService.sendPaymentCompletion(
                                PaymentCompletionRequest(
                                    invoiceId = savedInvoice.id,
                                    paymentId = savedInvoice.paymentId,
                                    customerId = savedInvoice.customerId,
                                    products = emptyList()
//                                    products = savedInvoice.products.map { product ->
//                                        PaidProduct(
//                                            id = product.uniqueId,
//                                            name = product.name,
//                                            count = product.count
//                                        )
//                                    }
                                )
                            )
                            savedInvoice.toResponse()
                        }
                        .switchIfEmpty(Mono.error(PaymentCompletionException("Error saving payment status")))
                } else {
                    Mono.error(PaymentDeniedException("Card was denied"))
                }
            }
            .switchIfEmpty(
                Mono.error(InvoiceNotFoundException("Invoice with payment id $paymentId not found"))
            )

}