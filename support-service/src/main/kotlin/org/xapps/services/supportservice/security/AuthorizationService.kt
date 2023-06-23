package org.xapps.services.supportservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.Duration

@Service
class AuthorizationService @Autowired constructor(
    private val webClient: WebClient.Builder,
    @Value("\${authorization.service.url}")
    private val baseUrl: String,
    @Value("\${authorization.service.paths.validate}")
    private val validatePath: String,
    @Value("\${circuitBreaker.retry.maxAttempts}")
    private val retryMaxAttempts: Long,
    @Value("\${circuitBreaker.retry.backOffPeriod}")
    private val retryBackoffPeriod: Long
) {

    fun validate(request: TokenValidateRequest): Mono<Credential> {
        return webClient.build()
            .post()
            .uri("$baseUrl$validatePath")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Credential::class.java)
            .retryWhen(Retry.backoff(retryMaxAttempts, Duration.ofSeconds(retryBackoffPeriod)))
    }

}