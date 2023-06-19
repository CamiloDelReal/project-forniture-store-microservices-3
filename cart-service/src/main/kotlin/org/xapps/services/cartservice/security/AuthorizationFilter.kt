package org.xapps.services.cartservice.security

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class AuthorizationFilter constructor(
    private val objectMapper: ObjectMapper,
    private val securityParams: SecurityParams
) : GenericFilterBean() {

    override fun doFilter(servletRequest: ServletRequest, servletResponse: ServletResponse, chain: FilterChain) {
        val request = servletRequest as HttpServletRequest
        val response = servletResponse as HttpServletResponse
        val authzHeaders = request.getHeaders(HttpHeaders.AUTHORIZATION)
        var authentication: UsernamePasswordAuthenticationToken? = null

        while (authentication == null && authzHeaders.hasMoreElements()) {
            try {
                authentication = getAuthentication(authzHeaders.nextElement())
            } catch (ex: Exception) {
                continue
            }
        }
        if (authentication == null) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
        } else {
            SecurityContextHolder.getContext().authentication = authentication
        }
        chain.doFilter(request, response)
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