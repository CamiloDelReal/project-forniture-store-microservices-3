package org.xapps.services.deliveryservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.xapps.services.deliveryservice.security.SecurityParams

@SpringBootApplication
@EnableConfigurationProperties(value = [
    SecurityParams::class
])
@EnableFeignClients
class DeliveryServiceApplication

fun main(args: Array<String>) {
    runApplication<DeliveryServiceApplication>(*args)
}
