package org.xapps.service.customerservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.retry.annotation.EnableRetry
import org.xapps.service.customerservice.security.SecurityParams

@SpringBootApplication
@EnableConfigurationProperties(value = [
    SecurityParams::class
])
@EnableFeignClients
@EnableRetry(proxyTargetClass=true)
class CustomerServiceApplication

fun main(args: Array<String>) {
    runApplication<CustomerServiceApplication>(*args)
}
