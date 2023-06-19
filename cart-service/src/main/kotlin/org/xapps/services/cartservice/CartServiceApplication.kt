package org.xapps.services.cartservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.retry.annotation.EnableRetry
import org.xapps.services.cartservice.security.SecurityParams

@SpringBootApplication
@EnableConfigurationProperties(
    SecurityParams::class
)
@EnableFeignClients
@EnableRetry(proxyTargetClass=true)
class CartServiceApplication

fun main(args: Array<String>) {
    runApplication<CartServiceApplication>(*args)
}
