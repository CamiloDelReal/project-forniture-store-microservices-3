package org.xapps.services.authorizationservice.common

import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

object Containers {
    @JvmStatic
    @Container
    var MYSQL_CONTAINER = MySQLContainer("mysql:8.0.32")
        .withUsername("test")
        .withPassword("test")
        .withExposedPorts(3306)
        .withDatabaseName("authorization_service_db")
        .withReuse(true).apply {
            start()
        }

    @JvmStatic
    @Container
    var REDIS_CONTAINER = GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
        .withReuse(true)
        .withExposedPorts(6379).apply {
            start()
        }
}