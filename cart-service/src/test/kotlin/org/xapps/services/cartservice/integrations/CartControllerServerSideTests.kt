package org.xapps.services.cartservice.integrations

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.testcontainers.junit.jupiter.Testcontainers
import org.testng.annotations.Test
import org.xapps.services.cartservice.common.Containers
import org.xapps.services.cartservice.common.Utils
import org.xapps.services.cartservice.dtos.CartCreateRequest
import org.xapps.services.cartservice.dtos.CartUpdateRequest
import org.xapps.services.cartservice.security.SecurityParams

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EnableWebMvc
@AutoConfigureMockMvc
class CartControllerServerSideTests : AbstractTestNGSpringContextTests() {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var securityParams: SecurityParams

    private val mapper = ObjectMapper()

    @Test
    fun `create a cart is successful`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 100,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 100)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.customerId").value(cartCreateRequest.customerId))
            .andExpect(jsonPath("$.fornitures").value(cartCreateRequest.fornitures))
    }

    @Test
    fun `create a cart fails as customer with wrong credential`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 101,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 1001)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `create a cart fails as no authorization`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 102,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `create a cart with administrator authorization is successful`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 102,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true, customerId = 1002)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.customerId").value(cartCreateRequest.customerId))
            .andExpect(jsonPath("$.fornitures").value(cartCreateRequest.fornitures))
    }

    @Test
    fun `update a cart is successful`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 103,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 103)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        val cartUpdateRequest = CartUpdateRequest(
            fornitures = mapOf(
                "forniture-id-1000" to 2,
                "forniture-id-3000" to 5
            )
        )
        val updatedCartFornitures = mapOf(
            "forniture-id-1000" to 2,
            "forniture-id-2000" to 1,
            "forniture-id-3000" to 5
        )
        mockMvc.perform(put("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartUpdateRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.customerId").value(cartCreateRequest.customerId))
            .andExpect(jsonPath("$.fornitures").value(updatedCartFornitures))
    }

    @Test
    fun `update a cart fails as customer with wrong credential`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 104,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 104)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        val cartUpdateRequest = CartUpdateRequest(
            fornitures = mapOf(
                "forniture-id-1000" to 2,
                "forniture-id-3000" to 5
            )
        )
        val tokenWrong = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 1004)
        mockMvc.perform(put("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenWrong")
            .content(mapper.writeValueAsString(cartUpdateRequest)))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `update a cart fails as no authorization`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 105,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 105)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        val cartUpdateRequest = CartUpdateRequest(
            fornitures = mapOf(
                "forniture-id-1000" to 2,
                "forniture-id-3000" to 5
            )
        )
        mockMvc.perform(put("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(cartUpdateRequest)))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `update a cart with administrator authorization is successful`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 106,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 106)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        val tokenAdmin = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true, customerId = 1006)
        val cartUpdateRequest = CartUpdateRequest(
            fornitures = mapOf(
                "forniture-id-1000" to 2,
                "forniture-id-3000" to 5
            )
        )
        val updatedCartFornitures = mapOf(
            "forniture-id-1000" to 2,
            "forniture-id-2000" to 1,
            "forniture-id-3000" to 5
        )
        mockMvc.perform(put("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenAdmin")
            .content(mapper.writeValueAsString(cartUpdateRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.customerId").value(cartCreateRequest.customerId))
            .andExpect(jsonPath("$.fornitures").value(updatedCartFornitures))
    }

    @Test
    fun `delete a cart is successful`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 107,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 107)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        mockMvc.perform(delete("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token"))
            .andExpect(status().isOk)
    }

    @Test
    fun `delete a cart fails as customer with wrong credential`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 108,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 108)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        val tokenWrong = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 1008)
        mockMvc.perform(delete("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenWrong"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `delete a cart fails as no authorization`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 109,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 109)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        mockMvc.perform(delete("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `delete a cart with administrator authorization is successful`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 110,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 110)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        val tokenAdmin = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true, customerId = 1010)
        mockMvc.perform(delete("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenAdmin"))
            .andExpect(status().isOk)
    }

    @Test
    fun `read a cart is successful`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 111,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 111)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        mockMvc.perform(get("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.customerId").value(cartCreateRequest.customerId))
            .andExpect(jsonPath("$.fornitures").value(cartCreateRequest.fornitures))
    }

    @Test
    fun `read a cart fails as customer with wrong credential`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 112,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 112)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        val tokenWrong = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 1012)
        mockMvc.perform(get("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenWrong"))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `read a cart fails as no authorization`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 113,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 113)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        mockMvc.perform(get("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `read a cart with administrator authorization is successful`() {
        val cartCreateRequest = CartCreateRequest(
            customerId = 114,
            fornitures = mapOf(
                "forniture-id-1000" to 3,
                "forniture-id-2000" to 1
            )
        )
        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = false, customerId = 114)
        mockMvc.perform(post("/carts")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(cartCreateRequest)))
            .andExpect(status().isCreated)
        val tokenAdmin = Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true, customerId = 1014)
        mockMvc.perform(get("/carts/${cartCreateRequest.customerId}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $tokenAdmin"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.customerId").value(cartCreateRequest.customerId))
            .andExpect(jsonPath("$.fornitures").value(cartCreateRequest.fornitures))
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.data.redis.host", Containers.REDIS_CONTAINER::getHost)
            registry.add("spring.data.redis.port") { Containers.REDIS_CONTAINER.getMappedPort(6379) }
            registry.add("spring.data.redis.password") { "" }
        }
    }
}