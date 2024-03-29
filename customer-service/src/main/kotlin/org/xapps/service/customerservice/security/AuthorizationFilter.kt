package org.xapps.service.customerservice.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class AuthorizationFilter constructor(
    private val securityParams: SecurityParams,
    private val authorizationRepository: AuthorizationRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
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
        filterChain.doFilter(request, response)
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