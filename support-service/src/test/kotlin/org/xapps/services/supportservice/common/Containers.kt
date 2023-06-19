package org.xapps.services.supportservice.common

import org.testcontainers.containers.GenericContainer
import org.testcontainers.images.builder.Transferable
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName


object Containers {

    val USERNAME = "test"
    val PASSWORD = "test"
    val DATABASE = "test_chat_app_db"

    @JvmStatic
    @Container
    var MONGODB_CONTAINER = CustomMongoDbContainer(USERNAME, PASSWORD, DATABASE)
//        .withExposedPorts(27017)
//        .withEnv("MONGO_INITDB_ROOT_USERNAME", USERNAME)
//        .withEnv("MONGO_INITDB_ROOT_PASSWORD", PASSWORD)
//        .withEnv("MONGO_INITDB_DATABASE", DATABASE)
//        .withCopyToContainer(
//            Transferable.of("/mongo-init.js"),
//            "/docker-entrypoint-initdb.d/mongo-init.js")
//        .withReuse(true)
//        .apply {
//            start()
//        }
}

class CustomMongoDbContainer (
    username: String,
    password: String,
    database: String
): GenericContainer<CustomMongoDbContainer>(DockerImageName.parse("mongo:6.0.4-jammy")) {
    init {
        withEnv("MONGO_INITDB_ROOT_USERNAME", username)
        withEnv("MONGO_INITDB_ROOT_PASSWORD", password)
        withEnv("MONGO_INITDB_DATABASE", database)
        withExposedPorts(27017)
        withReuse(true)
        val initScript = object{}.javaClass.getResource("/mongo-init.js")?.readBytes()
        withCopyToContainer(
            Transferable.of(initScript),
            "/docker-entrypoint-initdb.d/mongo-init.js")
    }
}