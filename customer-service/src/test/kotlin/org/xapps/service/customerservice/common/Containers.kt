package org.xapps.service.customerservice.common

import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.RabbitMQContainer
import org.testcontainers.junit.jupiter.Container

object Containers {

    val USERNAME = "test"
    val PASSWORD = "test"

    @JvmStatic
    @Container
    var MYSQL_CONTAINER = MySQLContainer("mysql:8.0.32")
        .withUsername(USERNAME)
        .withPassword(PASSWORD)
        .withDatabaseName("forniture_service_db")
        .withExposedPorts(3306)
        .withReuse(true)
        .apply {
            start()
        }

    @JvmStatic
    @Container
    var RABBIT_CONTAINER = RabbitMQContainer("rabbitmq:3.9.28-management")
        .withExposedPorts(5672)
        .withUser(USERNAME, PASSWORD)
        .withReuse(true)
        .withQueue("create_credentials_request_queue")
        .withQueue("create_credentials_response_queue")
        .withQueue("delete_credentials_request_queue")
        .withQueue("delete_credentials_response_queue")
        .apply {
            start()
        }
}