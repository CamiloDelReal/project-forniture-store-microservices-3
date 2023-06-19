package org.xapps.services.configurationservice.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfiguration @Autowired constructor() {

    @Bean
    fun provideSecurityFilterChain(
        http: HttpSecurity
    ): SecurityFilterChain {
        http.csrf()
            .disable()
            .authorizeHttpRequests()
            .anyRequest()
            .authenticated()
            .and()
            .httpBasic()
            .and()
            .formLogin()
        return http.build()
    }

}