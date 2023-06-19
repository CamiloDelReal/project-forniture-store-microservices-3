package org.xapps.services.supportservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories
import org.springframework.retry.annotation.EnableRetry
import org.xapps.services.supportservice.security.SecurityParams

@SpringBootApplication
@EnableConfigurationProperties(value = [
    SecurityParams::class
])
@EnableReactiveMongoRepositories
@EnableRetry(proxyTargetClass=true)
class SupportServiceApplication

fun main(args: Array<String>) {
    runApplication<SupportServiceApplication>(*args)
}
