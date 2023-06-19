package org.xapps.services.supportservice.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
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
    private val objectMapper: ObjectMapper,
    private val securityParams: SecurityParams
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
            val claims: Claims = Jwts.parser()
                .setSigningKey(securityParams.jwtGeneration.key)
                .parseClaimsJws(token)
                .body
            val subject: String = claims.subject
            val credential = objectMapper.readValue(subject, Credential::class.java)
            val authorities = credential.roles.map { SimpleGrantedAuthority(it) }
            UsernamePasswordAuthenticationToken(credential, null, authorities)
        } catch (ex: Exception) {
            null
        }
    }

}

//class AuthorizationFilter constructor(
//    private val objectMapper: ObjectMapper,
//    private val securityParams: SecurityParams
//) : OncePerRequestFilter() {
//
//    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
//        val authzHeaders = request.getHeaders(HttpHeaders.AUTHORIZATION)
//        var authentication: UsernamePasswordAuthenticationToken? = null
//
//        while (authentication == null && authzHeaders.hasMoreElements()) {
//            try {
//                authentication = getAuthentication(authzHeaders.nextElement())
//            } catch (ex: Exception) {
//                continue
//            }
//        }
//        if (authentication == null) {
//            response.status = HttpServletResponse.SC_UNAUTHORIZED
//        } else {
//            SecurityContextHolder.getContext().authentication = authentication
//        }
//        filterChain.doFilter(request, response)
//    }
//
//    private fun getAuthentication(authorizationHeaderValue: String): UsernamePasswordAuthenticationToken? {
//        return try {
//            val token = authorizationHeaderValue.replace(securityParams.jwtGeneration.type, "")
//            val claims: Claims = Jwts.parser()
//                .setSigningKey(securityParams.jwtGeneration.key)
//                .parseClaimsJws(token)
//                .body
//            val subject: String = claims.subject
//            val credential = objectMapper.readValue(subject, Credential::class.java)
//            val authorities = credential.roles.map { SimpleGrantedAuthority(it) }
//            UsernamePasswordAuthenticationToken(credential, null, authorities)
//        } catch (ex: Exception) {
//            null
//        }
//    }
//
//}