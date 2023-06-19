package org.xapps.services.paymentservice.repositories

import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import org.xapps.services.paymentservice.entities.Invoice
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface InvoiceRepository : R2dbcRepository<Invoice, Long> {
    fun findByPaymentId(paymentId: String): Mono<Invoice>

    @Query("SELECT * FROM invoices WHERE customer_id = :customerId OFFSET :offset LIMIT :limit")
    fun readCustomerInvoicesPage(customerId: Long, offset: Int, limit: Int): Flux<Invoice>

    @Query("SELECT * FROM invoices OFFSET :offset LIMIT :limit")
    fun readPage(offset: Int, limit: Int): Flux<Invoice>
}