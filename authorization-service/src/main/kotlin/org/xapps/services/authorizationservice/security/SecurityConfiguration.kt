package org.xapps.services.authorizationservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.xapps.services.authorizationservice.services.AuthorizationService


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfiguration @Autowired constructor(
    private val securityParams: SecurityParams,
    @Lazy private val authorizationService: AuthorizationService
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
                "/actuator/**",
                "/authorization/**",
            ).permitAll()
            .requestMatchers(HttpMethod.POST, "/credentials").permitAll()
            .anyRequest().authenticated()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(provideAuthorizationFilter(), BasicAuthenticationFilter::class.java)

        http.headers().frameOptions().disable()

        return http.build()
    }

    @Bean
    fun authenticationManager(userDetailsService: UserDetailsService): AuthenticationManager =
        ProviderManager(listOf(
            DaoAuthenticationProvider().apply {
                setUserDetailsService(userDetailsService)
                setPasswordEncoder(providePasswordEncoder())
            }
        ))

    @Bean
    fun providePasswordEncoder(): PasswordEncoder =
        BCryptPasswordEncoder()

    @Bean
    fun provideAuthorizationFilter(): AuthorizationFilter =
        AuthorizationFilter(authorizationService)
}