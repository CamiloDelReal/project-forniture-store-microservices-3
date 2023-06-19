package org.xapps.service.fornitureservice.common

import org.testcontainers.containers.MySQLContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container

object Containers {
    @JvmStatic
    @Container
    var MYSQL_CONTAINER = MySQLContainer("mysql:8.0.32")
        .withUsername("test")
        .withPassword("test")
        .withDatabaseName("forniture_service_db")
        .withExposedPorts(3306)
        .withReuse(true)
        .apply {
            start()
        }

    @JvmStatic
    @Container
    var ELASTICSEARCH_CONTAINER = ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.6.1")
        .withEnv("xpack.security.enabled", "false")
        .withExposedPorts(9200)
        .withReuse(true)
        .waitingFor(Wait.forHttp("/"))
        .apply {
            start()
        }
}