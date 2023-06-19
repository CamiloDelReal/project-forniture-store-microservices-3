package org.xapps.services.supportservice.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class WebTestConfiguration {

    @Bean("customerServiceWebClient")
    fun customerServiceWebClient(): WebClient.Builder =
        WebClient.builder()

}