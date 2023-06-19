package org.xapps.services.authorizationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.xapps.services.authorizationservice.security.SecurityParams
import org.xapps.services.authorizationservice.seeders.SeederParams

@SpringBootApplication
@EnableConfigurationProperties(value = [
    SeederParams::class,
    SecurityParams::class
])
@EnableRedisRepositories
class AuthorizationServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthorizationServiceApplication>(*args)
}
