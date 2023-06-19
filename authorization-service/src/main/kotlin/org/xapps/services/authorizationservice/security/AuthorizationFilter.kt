package org.xapps.services.authorizationservice.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import org.xapps.services.authorizationservice.services.AuthorizationService

class AuthorizationFilter constructor(
    private val authorizationService: AuthorizationService
) : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val authzHeaders = request.getHeaders(HttpHeaders.AUTHORIZATION)
        var authentication: UsernamePasswordAuthenticationToken? = null
        while (authentication == null && authzHeaders.hasMoreElements()) {
            try {
                authentication = authorizationService.getAuthentication(authzHeaders.nextElement())
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

}