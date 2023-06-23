package org.xapps.services.paymentservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class AuthorizationFilter @Autowired constructor(
    private val securityParams: SecurityParams,
    private val authorizationRepository: AuthorizationRepository
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val response = exchange.response
        val authzHeaders: List<String>? = request.headers[HttpHeaders.AUTHORIZATION]
        var authentication: Authentication? = null
        if(!authzHeaders.isNullOrEmpty()) {
            authentication = authzHeaders.firstNotNullOfOrNull { authzHeader ->
                getAuthentication(authzHeader)
            }
            if (authentication == null) {
                response.statusCode = HttpStatus.UNAUTHORIZED
            }
        }
        return if (authentication != null) {
            chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
        } else {
            chain.filter(exchange)
        }
    }

    private fun getAuthentication(authorizationHeaderValue: String): UsernamePasswordAuthenticationToken? {
        return try {
            val token = authorizationHeaderValue.replace(securityParams.jwtGeneration.type, "")
            val credential = authorizationRepository.validate(TokenValidateRequest(value = token))
            val authorities = credential.roles.map { SimpleGrantedAuthority(it) }
            UsernamePasswordAuthenticationToken(credential, null, authorities)
        } catch (ex: Exception) {
            null
        }
    }

}