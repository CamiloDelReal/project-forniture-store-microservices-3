package org.xapps.service.fornitureservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.xapps.service.fornitureservice.security.SecurityParams

@SpringBootApplication
@EnableConfigurationProperties(
    SecurityParams::class
)
class FornitureServiceApplication

fun main(args: Array<String>) {
    runApplication<FornitureServiceApplication>(*args)
}
