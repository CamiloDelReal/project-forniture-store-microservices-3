package org.xapps.services.authorizationservice.integrations.authorization

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.assertEquals
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.testcontainers.junit.jupiter.Testcontainers
import org.testng.annotations.Test
import org.xapps.services.authorizationservice.common.Containers
import org.xapps.services.authorizationservice.common.Utils
import org.xapps.services.authorizationservice.dtos.*
import org.xapps.services.authorizationservice.entities.Role
import org.xapps.services.authorizationservice.security.SecurityParams
import org.xapps.services.authorizationservice.seeders.SeederParams
import java.time.Instant
import java.util.*
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EnableWebMvc
@AutoConfigureMockMvc
class AuthorizationControllerServerSideTests : AbstractTestNGSpringContextTests() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var seederParams: SeederParams

    @Autowired
    private lateinit var securityParams: SecurityParams

    private val mapper = ObjectMapper()
    private var administratorAuthorization: Authorization? = null

    @Test
    fun `login with root credentials is successful`() {
        val loginRequest = Login(seederParams.rootCredential.username, seederParams.rootCredential.password)
        val result = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(result.response.contentAsString, Authorization::class.java)
        administratorAuthorization = authorization
    }

    @Test
    fun `unauthorized access after deleting a token from the cache`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user301", "123456")
        val createRequest = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credentialResponse = mapper.readValue(createRequest.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResult.response.contentAsString, Authorization::class.java)
        mockMvc.perform(get("/credentials/id/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
        mockMvc.perform(delete("/authorization/token/credential/{id}", credentialResponse!!.id)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
        mockMvc.perform(get("/credentials/id/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "login with root credentials is successful"
    ])
    fun `unauthorized access after disabling a credential`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user302", "123456", listOf(Role.ADMINISTRATOR))
        val createRequest = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization?.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credentialResponse = mapper.readValue(createRequest.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResult.response.contentAsString, Authorization::class.java)
        val credentialUpdateRequest = CredentialUpdateRequest(enabled = false)
        mockMvc.perform(put("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isOk)
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized)
        mockMvc.perform(get("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "login with root credentials is successful"
    ])
    fun `unauthorized access after a credential expires`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user303", "123456", listOf(Role.ADMINISTRATOR))
        val createRequest = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization?.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credentialResponse = mapper.readValue(createRequest.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResult.response.contentAsString, Authorization::class.java)
        val credentialUpdateRequest = CredentialUpdateRequest(credentialsNonExpired = false)
        mockMvc.perform(put("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isOk)
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized)
        mockMvc.perform(get("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "login with root credentials is successful"
    ])
    fun `unauthorized access after an account expires`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user304", "123456", listOf(Role.ADMINISTRATOR))
        val createRequest = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization?.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credentialResponse = mapper.readValue(createRequest.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResult.response.contentAsString, Authorization::class.java)
        val credentialUpdateRequest = CredentialUpdateRequest(accountNonExpired = false)
        mockMvc.perform(put("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isOk)
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized)
        mockMvc.perform(get("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "login with root credentials is successful"
    ])
    fun `unauthorized access after locking an account`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user305", "123456", listOf(Role.ADMINISTRATOR))
        val createRequest = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization?.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credentialResponse = mapper.readValue(createRequest.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResult.response.contentAsString, Authorization::class.java)
        val credentialUpdateRequest = CredentialUpdateRequest(accountNonLocked = false)
        mockMvc.perform(put("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isOk)
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized)
        mockMvc.perform(get("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "login with root credentials is successful"
    ])
    fun `token validation`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user306", "123456", listOf(Role.ADMINISTRATOR))
        val createRequest = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization?.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credentialResponse = mapper.readValue(createRequest.response.contentAsString, CredentialResponse::class.java)
        val validTokenButNotInCache = Utils.generateToken(
            credentialResponse,
            Instant.now().toEpochMilli(),
            Instant.now().toEpochMilli() + securityParams.jwtGeneration.validity,
            securityParams.jwtGeneration.key,
            mapper
        )
        mockMvc.perform(get("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $validTokenButNotInCache"))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "login with root credentials is successful"
    ])
    fun `updating credential status only if administrator`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user307", "123456")
        val createRequest = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credentialResponse = mapper.readValue(createRequest.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResult.response.contentAsString, Authorization::class.java)
        val credentialUpdateRequest = CredentialUpdateRequest(accountNonLocked = false)
        mockMvc.perform(put("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isForbidden)
        mockMvc.perform(put("/credentials/{id}", credentialResponse.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization!!.type} ${administratorAuthorization!!.token}")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isOk)

    }

    @Test
    fun `read customer tokens`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user308", "123456")
        val credentialResponse = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization?.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credential = mapper.readValue(credentialResponse.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResponse = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResponse.response.contentAsString, Authorization::class.java)
        val tokensResponse = mockMvc.perform(get("/authorization/token/customer/{customerId}", credential.customerId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
            .andReturn()
        val tokens = mapper.readValue(tokensResponse.response.contentAsString, Array<TokenResponse>::class.java)
        assertEquals(1, tokens.size)
    }

    @Test
    fun `read credential tokens`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user309", "123456")
        val credentialResponse = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization?.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credential = mapper.readValue(credentialResponse.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResponse = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResponse.response.contentAsString, Authorization::class.java)
        val tokensResponse = mockMvc.perform(get("/authorization/token/credential/{id}", credential.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
            .andReturn()
        val tokens = mapper.readValue(tokensResponse.response.contentAsString, Array<TokenResponse>::class.java)
        assertEquals(1, tokens.size)
    }

    @Test
    fun `validate token`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user310", "123456")
        val credentialResponse = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization?.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credential = mapper.readValue(credentialResponse.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResponse = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResponse.response.contentAsString, Authorization::class.java)
        val tokenValidateRequestSuccess = TokenValidateRequest(value = authorization.token)
        mockMvc.perform(post("/authorization/token/validate")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(tokenValidateRequestSuccess)))
            .andExpect(status().isOk)
        val tokenValidateRequestInvalid1 = TokenValidateRequest(value = Utils.generateToken(
            credential,
            Instant.now().toEpochMilli() + 1000,
            Instant.now().toEpochMilli() + securityParams.jwtGeneration.validity,
            securityParams.jwtGeneration.key,
            mapper
        ))
        mockMvc.perform(post("/authorization/token/validate")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(tokenValidateRequestInvalid1)))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `delete token`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "user311", "123456")
        val credentialResponse = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization?.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credential = mapper.readValue(credentialResponse.response.contentAsString, CredentialResponse::class.java)
        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResponse = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResponse.response.contentAsString, Authorization::class.java)
        val tokensResponse = mockMvc.perform(get("/authorization/token/credential/{id}", credential.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
            .andReturn()
        val tokens = mapper.readValue(tokensResponse.response.contentAsString, Array<TokenResponse>::class.java)
        mockMvc.perform(delete("/authorization/token/{id}", tokens[0].id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
        val tokenValidateRequest = TokenValidateRequest(value = authorization.token)
        mockMvc.perform(post("/authorization/token/validate")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(tokenValidateRequest)))
            .andExpect(status().isUnauthorized)
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { Containers.MYSQL_CONTAINER?.jdbcUrl }
            registry.add("spring.datasource.username") { Containers.MYSQL_CONTAINER?.username }
            registry.add("spring.datasource.password") { Containers.MYSQL_CONTAINER?.password }
            registry.add("spring.data.redis.host", Containers.REDIS_CONTAINER::getHost)
            registry.add("spring.data.redis.port") { Containers.REDIS_CONTAINER.getMappedPort(6379) }
            registry.add("spring.data.redis.password") { "" }
        }
    }
}