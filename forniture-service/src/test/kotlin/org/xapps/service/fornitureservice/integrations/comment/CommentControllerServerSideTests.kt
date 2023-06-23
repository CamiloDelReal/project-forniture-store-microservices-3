package org.xapps.service.fornitureservice.integrations.comment

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.junit.jupiter.api.Order
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.testcontainers.junit.jupiter.Testcontainers
import org.testng.annotations.Test
import org.xapps.service.fornitureservice.common.Containers
import org.xapps.service.fornitureservice.common.Utils
import org.xapps.service.fornitureservice.dtos.*
import org.xapps.service.fornitureservice.security.Credential
import org.xapps.service.fornitureservice.security.SecurityParams

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EnableWebMvc
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class CommentControllerServerSideTests: AbstractTestNGSpringContextTests() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var securityParams: SecurityParams

    private val mapper = ObjectMapper()
    private lateinit var forniture: FornitureResponse
    private lateinit var comment: CommentResponse
    private val customerId = 666L

    @Test
    fun `create forniture is successful`() {
        val request = FornitureCreateRequest(
            name = "Forniture Name 100",
            description = "Forniture Description 100",
            price = 250.89f,
            smallPicturePath = "/smallpictures/100",
            largePicturePath = "/largepictures/100"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(33,"user", listOf("Administrator")))))
        )
        forniture = mockMvc.perform(post("/fornitures")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}")
            .content(mapper.writeValueAsString(request)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.name").value(request.name))
            .andExpect(jsonPath("$.description").value(request.description))
            .andExpect(jsonPath("$.price").value(request.price))
            .andExpect(jsonPath("$.smallPicturePath").value(request.smallPicturePath))
            .andExpect(jsonPath("$.largePicturePath").value(request.largePicturePath))
            .andReturn().let {
                mapper.readValue(it.response.contentAsString, FornitureResponse::class.java)
            }
    }

    @Test(dependsOnMethods = [
        "create forniture is successful"
    ])
    fun `create comment for a valid forniture fails as no authorization`() {
        val commentRequest = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = customerId,
            evaluation = 8,
            value = "This is a comment"
        )
        mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "create forniture is successful"
    ])
    fun `create comment for a valid forniture and administrator authorization is successful`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true, customerId = customerId)
        val commentRequest = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = customerId,
            evaluation = 8,
            value = "This is a comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(customerId,"user", listOf("Administrator")))))
        )
        comment = mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.fornitureId").value(commentRequest.fornitureId))
            .andExpect(jsonPath("$.customerId").value(commentRequest.customerId))
            .andExpect(jsonPath("$.evaluation").value(commentRequest.evaluation))
            .andExpect(jsonPath("$.value").value(commentRequest.value))
            .andReturn().let {
                mapper.readValue(it.response.contentAsString, CommentResponse::class.java)
            }
    }

    @Test(dependsOnMethods = [
        "create forniture is successful"
    ])
    fun `create comment for a valid forniture and author authorization is successful`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 100)
        val commentRequest = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = 100,
            evaluation = 10,
            value = "This is a 10 comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(100,"user", listOf("Guest")))))
        )
        mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").isNotEmpty)
            .andExpect(jsonPath("$.fornitureId").value(commentRequest.fornitureId))
            .andExpect(jsonPath("$.customerId").value(commentRequest.customerId))
            .andExpect(jsonPath("$.evaluation").value(commentRequest.evaluation))
            .andExpect(jsonPath("$.value").value(commentRequest.value))
    }

    @Test(dependsOnMethods = [
        "create forniture is successful"
    ])
    fun `create comment for a valid forniture fails as author already has one comment for the forniture`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 101)
        val commentRequest1 = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = 101,
            evaluation = 10,
            value = "This is a comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(101,"user", listOf("Guest")))))
        )
        mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest1)))
            .andExpect(status().isCreated)
        val commentRequest2 = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = 101,
            evaluation = 2,
            value = "This is a comment"
        )
        mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest2)))
            .andExpect(status().isBadRequest)
    }

    @Test(dependsOnMethods = [
        "create forniture is successful"
    ])
    fun `create comment for a valid forniture fails as no same author authorization`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = customerId)
        val commentRequest = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = 102,
            evaluation = 10,
            value = "This is a comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(customerId,"user", listOf("Guest")))))
        )
        mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "create comment for a valid forniture and administrator authorization is successful"
    ])
    fun `update comment fails as no authorization`() {
        val commentRequest = CommentUpdateRequest(
            evaluation = 3,
            value = "This is another comment"
        )
        mockMvc.perform(put("/comments/{id}", forniture.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "create comment for a valid forniture and administrator authorization is successful"
    ])
    fun `update comment fails as no author authorization authorization`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 200)
        val commentRequest = CommentUpdateRequest(
            evaluation = 3,
            value = "This is another comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(200,"user", listOf("Guest")))))
        )
        mockMvc.perform(put("/comments/${comment.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "create comment for a valid forniture and administrator authorization is successful"
    ])
    fun `update comment with administrator authorization is successful`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)
        val commentRequest = CommentUpdateRequest(
            evaluation = 7,
            value = "This is an updated comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(33,"user", listOf("Administrator")))))
        )
        comment = mockMvc.perform(put("/comments/${comment.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(comment.id))
            .andExpect(jsonPath("$.fornitureId").value(comment.fornitureId))
            .andExpect(jsonPath("$.customerId").value(comment.customerId))
            .andExpect(jsonPath("$.evaluation").value(commentRequest.evaluation))
            .andExpect(jsonPath("$.value").value(commentRequest.value))
            .andReturn().let {
                mapper.readValue(it.response.contentAsString, CommentResponse::class.java)
            }
    }

    @Test(dependsOnMethods = [
        "create comment for a valid forniture and administrator authorization is successful"
    ])
    fun `update comment with author authorization is successful`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = customerId)
        val commentRequest = CommentUpdateRequest(
            evaluation = 9,
            value = "One more comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(customerId,"user", listOf("Guest")))))
        )
        comment = mockMvc.perform(put("/comments/${comment.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(comment.id))
            .andExpect(jsonPath("$.fornitureId").value(comment.fornitureId))
            .andExpect(jsonPath("$.customerId").value(comment.customerId))
            .andExpect(jsonPath("$.evaluation").value(commentRequest.evaluation))
            .andExpect(jsonPath("$.value").value(commentRequest.value))
            .andReturn().let {
                mapper.readValue(it.response.contentAsString, CommentResponse::class.java)
            }
    }

    @Test
    fun `update comment fails as not found`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = customerId)
        val commentRequest = CommentUpdateRequest(
            evaluation = 6,
            value = "One more comment for a failing request"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(customerId,"user", listOf("Guest")))))
        )
        mockMvc.perform(put("/comments/12345")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isNotFound)
    }

    @Test(dependsOnMethods = [
        "create comment for a valid forniture and administrator authorization is successful",
        "update comment fails as no author authorization authorization",
        "update comment with administrator authorization is successful",
        "update comment with author authorization is successful"
    ])
    fun `delete comment with administrator authorization is successful`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true, customerId = 300)
        val commentRequest = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = 300,
            evaluation = 8,
            value = "This is a comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(300,"user", listOf("Administrator")))))
        )
        val commentToDelete = mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isCreated)
            .andReturn().let {
                mapper.readValue(it.response.contentAsString, CommentResponse::class.java)
            }

        mockMvc.perform(delete("/comments/${commentToDelete.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token"))
            .andExpect(status().isOk)
    }

    @Test
    fun `delete comment with author authorization is successful`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 400)
        val commentRequest = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = 400,
            evaluation = 2,
            value = "This is a comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(400,"user", listOf("Guest")))))
        )
        val commentToDelete = mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isCreated)
            .andReturn().let {
                mapper.readValue(it.response.contentAsString, CommentResponse::class.java)
            }

        mockMvc.perform(delete("/comments/${commentToDelete.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token"))
            .andExpect(status().isOk)
    }

    @Test
    fun `delete comment fails as no author authorization`() {
        val tokenAuthor = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 500)
        val tokenNoAuthor = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 600)
        val commentRequest = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = 500,
            evaluation = 2,
            value = "This is a comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(500,"user", listOf("Guest")))))
        )
        val commentToDelete = mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenAuthor")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isCreated)
            .andReturn().let {
                mapper.readValue(it.response.contentAsString, CommentResponse::class.java)
            }

        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(600,"user", listOf("Guest")))))
        )
        mockMvc.perform(delete("/comments/${commentToDelete.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenNoAuthor"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `delete comment fails as comment does not exist`() {
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 700)
        val commentRequest = CommentCreateRequest(
            fornitureId = forniture.id,
            customerId = 700,
            evaluation = 2,
            value = "This is a comment"
        )
        WireMock.stubFor(WireMock.post(urlEqualTo("/authorization/token/validate"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(Credential(700,"user", listOf("Guest")))))
        )
        val commentToDelete = mockMvc.perform(post("/comments")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(commentRequest)))
            .andExpect(status().isCreated)
            .andReturn().let {
                mapper.readValue(it.response.contentAsString, CommentResponse::class.java)
            }

        mockMvc.perform(delete("/comments/${commentToDelete.id + 9000}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token"))
            .andExpect(status().isNotFound)
    }


    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { Containers.MYSQL_CONTAINER?.jdbcUrl }
            registry.add("spring.datasource.username") { Containers.MYSQL_CONTAINER.username }
            registry.add("spring.datasource.password") { Containers.MYSQL_CONTAINER.password }
            registry.add("spring.elasticsearch.uris") { Containers.ELASTICSEARCH_CONTAINER.httpHostAddress }
            registry.add("authorization.service.url") { "localhost:\${wiremock.server.port}" }
        }
    }
}