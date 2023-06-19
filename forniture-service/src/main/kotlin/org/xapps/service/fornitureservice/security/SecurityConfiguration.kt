package org.xapps.service.fornitureservice.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val securityParams: SecurityParams
) {

    @Bean
    fun provideSecurityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain {
        http.cors().configurationSource {
            val corsConfiguration = CorsConfiguration()
            corsConfiguration.allowedOrigins = securityParams.origins.urls
            corsConfiguration.allowedMethods = securityParams.origins.allowedMethods
            corsConfiguration.allowedHeaders = securityParams.origins.allowedHeaders
            corsConfiguration.maxAge = securityParams.origins.maxAge
            corsConfiguration
        }

        http
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers(
                "/actuator/**"
            ).permitAll()
            .requestMatchers(
                HttpMethod.GET,
                "/fornitures/**",
                "/comments/**"
            ).permitAll()
            .requestMatchers(
                HttpMethod.POST,
                "/fornitures/**",
                "/comments/**"
            ).authenticated()
            .requestMatchers(
                HttpMethod.PUT,
                "/fornitures/**",
                "/comments/**"
            ).authenticated()
            .requestMatchers(
                HttpMethod.DELETE,
                "/fornitures/**",
                "/comments/**"
            ).authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(provideAuthorizationFilter(), BasicAuthenticationFilter::class.java)

        http.headers().frameOptions().disable()

        return http.build()
    }

    @Bean
    fun provideAuthorizationFilter(): AuthorizationFilter =
        AuthorizationFilter(objectMapper, securityParams)
}