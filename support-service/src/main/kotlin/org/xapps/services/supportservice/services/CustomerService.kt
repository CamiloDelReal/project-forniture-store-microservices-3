package org.xapps.services.supportservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.xapps.services.supportservice.dtos.CustomerResponse
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Service
class CustomerService @Autowired constructor(
    private val customerServiceWebClient: WebClient.Builder,
    @Value("\${customer.service.url}")
    private val baseUrl: String,
    @Value("\${customer.service.paths.currentCustomer}")
    private val currentCustomerPath: String,
    @Value("\${circuitBreaker.retry.maxAttempts}")
    private val retryMaxAttempts: Long,
    @Value("\${circuitBreaker.retry.backOffPeriod}")
    private val retryBackoffPeriod: Long
) {

    fun read(authzHeader: String): Mono<CustomerResponse> {
        return customerServiceWebClient.build()
            .get()
            .uri("$baseUrl$currentCustomerPath")
            .header(HttpHeaders.AUTHORIZATION, authzHeader)
            .retrieve()
            .bodyToMono(CustomerResponse::class.java)
            .retryWhen(Retry.backoff(retryMaxAttempts, Duration.ofSeconds(retryBackoffPeriod)))
    }

}