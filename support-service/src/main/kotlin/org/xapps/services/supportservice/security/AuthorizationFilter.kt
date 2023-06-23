package org.xapps.services.supportservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
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
    private val authorizationService: AuthorizationService
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val response = exchange.response
        val authzHeaders: List<String>? = request.headers[HttpHeaders.AUTHORIZATION]
        return if(!authzHeaders.isNullOrEmpty()) {
                getAuthentication(authzHeaders.first())
                    .flatMap { authentication ->
                        if (authentication != null) {
                            chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                        } else {
                            chain.filter(exchange)
                        }
                    }
        } else {
            chain.filter(exchange)
        }
    }

    private fun getAuthentication(authorizationHeaderValue: String): Mono<UsernamePasswordAuthenticationToken?> {
        return try {
            val token = authorizationHeaderValue.replace(securityParams.jwtGeneration.type, "")
            authorizationService.validate(TokenValidateRequest(token))
                .map { credential ->
                    val authorities = credential.roles.map { SimpleGrantedAuthority(it) }
                    UsernamePasswordAuthenticationToken(credential, null, authorities)
                }
        } catch (ex: Exception) {
            Mono.error(ex)
        }
    }

}