package org.xapps.services.paymentservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.xapps.services.paymentservice.security.SecurityParams

@SpringBootApplication
@EnableConfigurationProperties(
    SecurityParams::class
)
@EnableR2dbcRepositories
class PaymentServiceApplication

fun main(args: Array<String>) {
    runApplication<PaymentServiceApplication>(*args)
}
