package org.xapps.services.cartservice.common

import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName

object Containers {
    @JvmStatic
    @Container
    var REDIS_CONTAINER = GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
        .withReuse(true)
        .withExposedPorts(6379).apply {
            start()
        }
}