package org.xapps.services.supportservice.configs

import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@Profile("!test")
class WebConfiguration {

    @Bean("customerServiceWebClient")
    @LoadBalanced
    @Profile("!test")
    fun customerServiceWebClient(): WebClient.Builder =
        WebClient.builder()

}