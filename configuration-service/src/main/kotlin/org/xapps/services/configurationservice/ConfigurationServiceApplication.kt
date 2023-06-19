package org.xapps.services.configurationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class ConfigurationServiceApplication

fun main(args: Array<String>) {
    runApplication<ConfigurationServiceApplication>(*args)
}
