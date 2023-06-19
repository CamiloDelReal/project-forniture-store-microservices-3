package org.xapps.service.customerservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.retry.RetryCallback
import org.springframework.retry.annotation.CircuitBreaker
import org.springframework.retry.annotation.Recover
import org.springframework.retry.annotation.Retryable
import org.springframework.retry.backoff.FixedBackOffPolicy
import org.springframework.retry.policy.SimpleRetryPolicy
import org.springframework.retry.support.RetryTemplate
import org.springframework.stereotype.Service
import org.xapps.service.customerservice.dtos.CredentialCreateRequest
import org.xapps.service.customerservice.dtos.CredentialResponse
import org.xapps.service.customerservice.repositories.CredentialRepository
import java.net.UnknownHostException

@Service
class CredentialService @Autowired constructor(
    private val credentialRepository: CredentialRepository,
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
    fun create(request: CredentialCreateRequest): CredentialResponse? {
        return retryTemplate.execute(RetryCallback<CredentialResponse, RuntimeException> {
            credentialRepository.create(request)
        })
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
    fun readByCustomerId(id: Long, authzHeader: String): CredentialResponse? {
        return retryTemplate.execute(RetryCallback<CredentialResponse, RuntimeException> { credentialRepository.readByCustomerId(id, authzHeader) })
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
    fun deleteByCustomerId(customerId: Long, authzHeader: String) {
        return retryTemplate.execute(RetryCallback<Unit, RuntimeException> { credentialRepository.deleteByCustomerId(customerId, authzHeader) })
    }


    @Recover
    fun createFallback(e: Throwable?): CredentialResponse? {
        throw e!!
    }

    @Recover
    fun createDefaultFallback(e: Throwable?) {
        throw e!!
    }

}