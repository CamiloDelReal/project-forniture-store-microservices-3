package org.xapps.service.customerservice.configs

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CommonConfiguration {

    @Bean
    fun provideObjectMapper(): ObjectMapper =
        ObjectMapper()

}