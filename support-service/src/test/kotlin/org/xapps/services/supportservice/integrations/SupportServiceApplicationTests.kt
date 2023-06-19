package org.xapps.services.supportservice.integrations

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.web.reactive.config.EnableWebFlux
import org.testcontainers.junit.jupiter.Testcontainers
import org.testng.annotations.Test
import org.xapps.services.supportservice.common.Containers
import org.xapps.services.supportservice.common.Utils
import org.xapps.services.supportservice.controllers.SupportController
import org.xapps.services.supportservice.dtos.ChatResponse
import org.xapps.services.supportservice.dtos.CustomerResponse
import org.xapps.services.supportservice.security.SecurityParams
import reactor.test.StepVerifier

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev", "test")
@Testcontainers
@EnableWebFlux
@AutoConfigureMockMvc
//@AutoConfigureWireMock(port = 0)      // It is not initializing the WireMockServer (?)
class SupportServiceApplicationTests : AbstractTestNGSpringContextTests() {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var securityParams: SecurityParams

    @Autowired
    private lateinit var supportController: SupportController

    private val mapper = ObjectMapper()


    @Test
    fun `requesting support by a user with customer role is successful`() {
        val token = Utils.generateToken(customerId = 123, roles = listOf("Customer"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
            .willReturn(
                WireMock.aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
                .withBody(mapper.writeValueAsString(CustomerResponse(id = 123, lastName = "Doe", firstName = "John")))))

        webTestClient.post()
            .uri("/support/request")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.createdAt").isNotEmpty
            .jsonPath("$.status").isEqualTo("PENDING")
            .jsonPath("$.customer").isEqualTo("John Doe")
            .jsonPath("$.supporter").isEmpty
    }

    @Test
    fun `requesting support by a user with admin role is successful`() {
        val token = Utils.generateToken(customerId = 234, roles = listOf("Administrator"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 234, lastName = "Doe", firstName = "John")))))

        webTestClient.post()
            .uri("/support/request")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.createdAt").isNotEmpty
            .jsonPath("$.status").isEqualTo("PENDING")
            .jsonPath("$.customer").isEqualTo("John Doe")
            .jsonPath("$.supporter").isEmpty
    }

    @Test
    fun `requesting support by a user with support role is rejected`() {
        val token = Utils.generateToken(customerId = 345, roles = listOf("Supporter"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 345, lastName = "Doe", firstName = "John")))))

        webTestClient.post()
            .uri("/support/request")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `providing support by a user with customer role is rejected`() {
        val tokenCustomer1 = Utils.generateToken(customerId = 456, roles = listOf("Customer"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer1")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 123, lastName = "Doe", firstName = "John")))))

        val customerResponse = webTestClient.post()
            .uri("/support/request")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer1")
            .exchange()
            .expectStatus().isOk
            .expectBody<ChatResponse>().returnResult().responseBody!!

        val tokenCustomer2 = Utils.generateToken(customerId = 567, roles = listOf("Customer"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer2")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 321, lastName = "Doe", firstName = "Jane")))))

        webTestClient.post()
            .uri("/support/response/${customerResponse.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer2")
            .exchange()
            .expectStatus().isForbidden
    }

    @Test
    fun `providing support by a user with admin role is successful`() {
        val tokenCustomer = Utils.generateToken(customerId = 456, roles = listOf("Customer"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 123, lastName = "Doe", firstName = "John")))))

        val customerResponse = webTestClient.post()
            .uri("/support/request")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer")
            .exchange()
            .expectStatus().isOk
            .expectBody<ChatResponse>().returnResult().responseBody!!

        val tokenAdmin = Utils.generateToken(customerId = 567, roles = listOf("Administrator"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenAdmin")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 321, lastName = "Doe", firstName = "Jane")))))

        webTestClient.post()
            .uri("/support/response/${customerResponse.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenAdmin")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(customerResponse.id)
            .jsonPath("$.createdAt").isEqualTo(customerResponse.createdAt)
            .jsonPath("$.status").isEqualTo("IN_PROGRESS")
            .jsonPath("$.customer").isEqualTo("John Doe")
            .jsonPath("$.supporter").isEqualTo("Jane Doe")
    }

    @Test
    fun `providing support by a user with support role is successful`() {
        val tokenCustomer = Utils.generateToken(customerId = 123, roles = listOf("Customer"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 123, lastName = "Doe", firstName = "John")))))

        val customerResponse = webTestClient.post()
            .uri("/support/request")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer")
            .exchange()
            .expectStatus().isOk
            .expectBody<ChatResponse>().returnResult().responseBody!!

        val tokenSupporter = Utils.generateToken(customerId = 321, roles = listOf("Supporter"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenSupporter")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 321, lastName = "Doe", firstName = "Jane")))))

        webTestClient.post()
            .uri("/support/response/${customerResponse.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenSupporter")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo(customerResponse.id)
            .jsonPath("$.createdAt").isEqualTo(customerResponse.createdAt)
            .jsonPath("$.status").isEqualTo("IN_PROGRESS")
            .jsonPath("$.customer").isEqualTo("John Doe")
            .jsonPath("$.supporter").isEqualTo("Jane Doe")
    }

    @Test
    fun `monitor chat is successful`() {
        val tokenCustomer = Utils.generateToken(customerId = 123, roles = listOf("Customer"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 123, lastName = "Doe", firstName = "John")))))

        val customerResponse = webTestClient.post()
            .uri("/support/request")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer")
            .exchange()
            .expectStatus().isOk
            .expectBody<ChatResponse>().returnResult().responseBody!!

        val monitorChatFlux = webTestClient.get()
            .uri("/support/monitor/chat/${customerResponse.id}")
            .accept(MediaType.APPLICATION_NDJSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenCustomer")
            .exchange()
            .returnResult(ChatResponse::class.java)
            .responseBody

        val tokenSupporter = Utils.generateToken(customerId = 321, roles = listOf("Supporter"), key = securityParams.jwtGeneration.key)

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlPathEqualTo("/customers/current"))
                .willReturn(
                    WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withHeader(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenSupporter")
                        .withBody(mapper.writeValueAsString(CustomerResponse(id = 321, lastName = "Doe", firstName = "Jane")))))

        val supporterResponse = webTestClient.post()
            .uri("/support/response/${customerResponse.id}")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenSupporter")
            .exchange()
            .expectStatus().isOk
            .expectBody<ChatResponse>().returnResult().responseBody!!

        StepVerifier
            .create(monitorChatFlux)
            .expectNext(ChatResponse(
                id = customerResponse.id,
                createdAt = customerResponse.createdAt,
                status = customerResponse.status,
                customer = customerResponse.customer,
                supporter = customerResponse.supporter
            ))
            .expectNext(ChatResponse(
                id = "supporterResponse.id",
                createdAt = supporterResponse.createdAt,
                status = supporterResponse.status,
                customer = supporterResponse.customer,
                supporter = supporterResponse.supporter
            ))
            .thenCancel()
            .verify()
    }


    companion object {

        const val WIREMOCK_SERVER_PORT = 10999

        val wireMockServer = WireMockServer(WIREMOCK_SERVER_PORT).apply {
            start()
        }

        @JvmStatic
        @DynamicPropertySource
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.mongodb.host") { Containers.MONGODB_CONTAINER.host }
            registry.add("spring.data.mongodb.port") { Containers.MONGODB_CONTAINER.getMappedPort(27017) }
            registry.add("spring.data.mongodb.username") { Containers.USERNAME }
            registry.add("spring.data.mongodb.password") { Containers.PASSWORD }
            registry.add("spring.data.mongodb.database") { Containers.DATABASE }
            registry.add("customer.service.url") { "http://localhost:$WIREMOCK_SERVER_PORT" }
        }
    }

}
