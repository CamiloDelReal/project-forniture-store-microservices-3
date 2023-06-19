package org.xapps.services.deliveryservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.retry.RetryCallback
import org.springframework.retry.annotation.CircuitBreaker
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import org.xapps.services.deliveryservice.dtos.CustomerResponse
import org.xapps.services.deliveryservice.repositories.CustomerRepository
import java.net.UnknownHostException

@Service
class CustomerService @Autowired constructor(
    private val customerRepository: CustomerRepository,
    @Value("\${circuitBreaker.retry.backOffPeriod}")
    private val retryBackoffPeriod: Long,
    @Value("\${circuitBreaker.retry.maxAttempts}")
    private val retryMaxAttemps: Int
) {

    private val retryTemplate = RetryTemplate().apply {
        val fixedBackOffPolicy = FixedBackOffPolicy()
        fixedBackOffPolicy.backOffPeriod = retryBackoffPeriod
        setBackOffPolicy(fixedBackOffPolicy)

        val retryPolicy = SimpleRetryPolicy()
        retryPolicy.maxAttempts = retryMaxAttemps
        setRetryPolicy(retryPolicy)
    }

    @CircuitBreaker(
        maxAttemptsExpression = "\${circuitBreaker.breaker.maxAttempts}",
        openTimeoutExpression = "\${circuitBreaker.breaker.openTimeout}",
        resetTimeoutExpression = "\${circuitBreaker.breaker.resetTimeout}"
    )
    @Retryable(
        maxAttemptsExpression = "\${circuitBreaker.retry.maxAttempts}",
        value = [UnknownHostException::class]
    )
    fun readById(id: Long): CustomerResponse? {
        return retryTemplate.execute(RetryCallback<CustomerResponse, RuntimeException> { customerRepository.readById(id) })
    }

    @Recover
    fun createFallback(e: Throwable?): CustomerResponse? {
        throw e!!
    }

}