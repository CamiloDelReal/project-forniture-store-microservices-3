package org.xapps.services.authorizationservice.configs

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class CommonConfiguration @Autowired constructor(
    private val redisTemplate: RedisTemplate<Any, Any>
) {

    init {
        redisTemplate.setEnableTransactionSupport(true)
    }

    @Bean
    fun provideObjectMapper(): ObjectMapper =
        ObjectMapper()

}