package org.xapps.services.gatewayservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.xapps.services.gatewayservice.security.SecurityParams

@SpringBootApplication
@EnableConfigurationProperties(
    SecurityParams::class
)
class GatewayServiceApplication

fun main(args: Array<String>) {
    runApplication<GatewayServiceApplication>(*args)
}
