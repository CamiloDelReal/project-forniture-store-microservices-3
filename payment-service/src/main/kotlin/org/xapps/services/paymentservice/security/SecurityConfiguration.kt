package org.xapps.services.paymentservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.web.cors.CorsConfiguration
import reactor.core.publisher.Mono


@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfiguration @Autowired constructor(
    private val securityParams: SecurityParams,
    private val authorizationRepository: AuthorizationRepository
){

    @Bean
    fun provideSecurityWebFilterChain(
        http: ServerHttpSecurity
    ): SecurityWebFilterChain {
        http.cors().configurationSource {
            val corsConfiguration = CorsConfiguration()
            corsConfiguration.allowedOrigins = securityParams.origins.urls
            corsConfiguration.allowedMethods = securityParams.origins.allowedMethods
            corsConfiguration.allowedHeaders = securityParams.origins.allowedHeaders
            corsConfiguration.maxAge = securityParams.origins.maxAge
            corsConfiguration
        }

        http
            .exceptionHandling()
            .authenticationEntryPoint { exchange, ex ->
                Mono.fromRunnable {
                    exchange.response.statusCode = HttpStatus.UNAUTHORIZED
                }
            }
            .accessDeniedHandler { exchange, denied ->
                Mono.fromRunnable {
                    exchange.response.statusCode = HttpStatus.FORBIDDEN
                }
            }
            .and()
            .csrf().disable()
            .authorizeExchange()
            .pathMatchers("/actuator/**").permitAll()
            .anyExchange().authenticated()
            .and()
            .addFilterBefore(provideAuthorizationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)

        http.headers().frameOptions().disable()

        return http.build()
    }

    @Bean
    fun provideAuthorizationFilter(): AuthorizationFilter =
        AuthorizationFilter(securityParams, authorizationRepository)
}