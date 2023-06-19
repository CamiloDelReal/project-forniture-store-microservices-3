package org.xapps.services.authorizationservice.integrations.credential

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions.*
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
import org.xapps.services.authorizationservice.common.Containers
import org.xapps.services.authorizationservice.dtos.*
import org.xapps.services.authorizationservice.entities.Role
import org.xapps.services.authorizationservice.security.SecurityParams
import org.xapps.services.authorizationservice.seeders.SeederParams
import kotlin.random.Random

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EnableWebMvc
@AutoConfigureMockMvc
class CredentialControllerServerSideTests : AbstractTestNGSpringContextTests() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var seederParams: SeederParams

    @Autowired
    private lateinit var securityParams: SecurityParams

    private val mapper = ObjectMapper()
    private var credentialCreatedWithDefaultRole: CredentialResponse? = null
    private var credentialCreatedWithDefaultRolePassword: String? = null
    private var administratorAuthorization: Authorization? = null

    @Test
    fun `login with root credentials is successful`() {
        val loginRequest = Login(seederParams.rootCredential.username, seederParams.rootCredential.password)
        val result = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.type").exists())
            .andExpect(jsonPath("$.expiration").exists())
            .andReturn()
        val authorization = mapper.readValue(result.response.contentAsString, Authorization::class.java)
        administratorAuthorization = authorization
    }

    @Test
    fun `login with root credentials fails by wrong password`() {
        val loginRequest = Login(seederParams.rootCredential.username, "invalid")
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `login with root credentials fails by wrong username`() {
        val loginRequest = Login("invalid", seederParams.rootCredential.password)
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `create credentials with default role is successful`() {
        val credentialPassword = "qwerty"
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "johndoe100", credentialPassword)
        val result = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").exists())
            .andExpect(jsonPath("$.roles").exists())
            .andExpect(jsonPath("$.enabled").exists())
            .andExpect(jsonPath("$.accountNonExpired").exists())
            .andExpect(jsonPath("$.credentialsNonExpired").exists())
            .andExpect(jsonPath("$.accountNonLocked").exists())
            .andReturn()
        val credentialResponse = mapper.readValue(result.response.contentAsString, CredentialResponse::class.java)
        assertNotNull(credentialResponse.id)
        assertNotEquals(0, credentialResponse.id)
        assertEquals(credentialCreateRequest.customerId, credentialResponse.customerId)
        assertEquals(credentialCreateRequest.username, credentialResponse.username)
        assertEquals(true, credentialResponse.enabled)
        assertEquals(true, credentialResponse.accountNonExpired)
        assertEquals(true, credentialResponse.credentialsNonExpired)
        assertEquals(true, credentialResponse.accountNonLocked)
        assertNotNull(credentialResponse.roles)
        assertEquals(1, credentialResponse.roles.size)
        assertTrue(credentialResponse.roles.any { it == Role.CUSTOMER })
        credentialCreatedWithDefaultRole = credentialResponse
        credentialCreatedWithDefaultRolePassword = credentialPassword
    }

    @Test
    fun `read credentials`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "alicedoe100", "qwerty")
        val resultCreate = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()
        val credential = mapper.readValue(resultCreate.response.contentAsString, CredentialResponse::class.java)

        val loginRequest = Login(credentialCreateRequest.username, credentialCreateRequest.password)
        val loginResponse = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val authorization = mapper.readValue(loginResponse.response.contentAsString, Authorization::class.java)

        mockMvc.perform(get("/credentials/id/{id}", credential.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(credential.id))
            .andExpect(jsonPath("$.username").value(credential.username))
            .andExpect(jsonPath("$.roles").value(credential.roles))
            .andExpect(jsonPath("$.enabled").value(true))
            .andExpect(jsonPath("$.accountNonExpired").value(true))
            .andExpect(jsonPath("$.credentialsNonExpired").value(true))
            .andExpect(jsonPath("$.accountNonLocked").value(true))

        mockMvc.perform(get("/credentials/current", credential.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(credential.id))
            .andExpect(jsonPath("$.username").value(credential.username))
            .andExpect(jsonPath("$.roles").value(credential.roles))
            .andExpect(jsonPath("$.enabled").value(true))
            .andExpect(jsonPath("$.accountNonExpired").value(true))
            .andExpect(jsonPath("$.credentialsNonExpired").value(true))
            .andExpect(jsonPath("$.accountNonLocked").value(true))

        mockMvc.perform(get("/credentials/customer/{id}", credential.customerId)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(credential.id))
            .andExpect(jsonPath("$.username").value(credential.username))
            .andExpect(jsonPath("$.roles").value(credential.roles))
            .andExpect(jsonPath("$.enabled").value(true))
            .andExpect(jsonPath("$.accountNonExpired").value(true))
            .andExpect(jsonPath("$.credentialsNonExpired").value(true))
            .andExpect(jsonPath("$.accountNonLocked").value(true))

        mockMvc.perform(get("/credentials/current/customer")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(credential.id))
            .andExpect(jsonPath("$.username").value(credential.username))
            .andExpect(jsonPath("$.roles").value(credential.roles))
            .andExpect(jsonPath("$.enabled").value(true))
            .andExpect(jsonPath("$.accountNonExpired").value(true))
            .andExpect(jsonPath("$.credentialsNonExpired").value(true))
            .andExpect(jsonPath("$.accountNonLocked").value(true))

        mockMvc.perform(get("/credentials/id/{id}", credential.id + 100)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${authorization.type} ${authorization.token}"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `create credentials with administrator role fails by no admin authentication`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "janedoe100", "qwerty", listOf(Role.ADMINISTRATOR))
        mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "login with root credentials is successful"
    ])
    fun `create credential with administrator role is successful`() {
        assertNotNull(administratorAuthorization)
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "kathdoe100", "qwerty", listOf(Role.ADMINISTRATOR))
        val result = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").exists())
            .andExpect(jsonPath("$.customerId").exists())
            .andExpect(jsonPath("$.roles").exists())
            .andReturn()
        val credentialResponse = mapper.readValue(result.response.contentAsString, CredentialResponse::class.java)
        assertNotNull(credentialResponse.id)
        assertNotEquals(0, credentialResponse.id)
        assertEquals(credentialCreateRequest.customerId, credentialResponse.customerId)
        assertEquals(credentialCreateRequest.username, credentialResponse.username)
        assertEquals(true, credentialResponse.enabled)
        assertEquals(true, credentialResponse.accountNonExpired)
        assertEquals(true, credentialResponse.credentialsNonExpired)
        assertEquals(true, credentialResponse.accountNonLocked)
        assertNotNull(credentialResponse.roles)
        assertEquals(1, credentialResponse.roles.size)
        assertTrue(credentialResponse.roles.any { it == Role.ADMINISTRATOR })
    }

    @Test
    fun `create credentials fails by username duplicity`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "root", "qwerty")
        mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isBadRequest)
    }

    @Test(dependsOnMethods = [
        "create credentials with default role is successful"
    ])
    fun `update credentials is successful`() {
        assertNotNull(credentialCreatedWithDefaultRole)
        assertNotNull(credentialCreatedWithDefaultRolePassword)
        val password = "12345"
        val loginUpdateRequest = Login(credentialCreatedWithDefaultRole!!.username!!, credentialCreatedWithDefaultRolePassword!!)
        val loginUpdateResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginUpdateRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andReturn()
        val credentialUpdateRequest = CredentialUpdateRequest(username = "annadoe100", password = password)
        val token = mapper.readValue(loginUpdateResult.response.contentAsString, Authorization::class.java).token
        val credentialUpdateResult = mockMvc.perform(put("/credentials/{id}", credentialCreatedWithDefaultRole!!.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.customerId").exists())
            .andExpect(jsonPath("$.username").exists())
            .andExpect(jsonPath("$.enabled").exists())
            .andExpect(jsonPath("$.accountNonExpired").exists())
            .andExpect(jsonPath("$.credentialsNonExpired").exists())
            .andExpect(jsonPath("$.accountNonLocked").exists())
            .andExpect(jsonPath("$.roles").exists())
            .andReturn()
        val credentialResponse = mapper.readValue(credentialUpdateResult.response.contentAsString, CredentialResponse::class.java)
        assertNotNull(credentialResponse.id)
        assertEquals(credentialCreatedWithDefaultRole!!.id, credentialResponse.id)
        assertEquals(credentialCreatedWithDefaultRole!!.customerId, credentialResponse.customerId)
        assertEquals(credentialUpdateRequest.username, credentialResponse.username)
        assertEquals(credentialCreatedWithDefaultRole!!.enabled, credentialResponse.enabled)
        assertEquals(credentialCreatedWithDefaultRole!!.accountNonExpired, credentialResponse.accountNonExpired)
        assertEquals(credentialCreatedWithDefaultRole!!.credentialsNonExpired, credentialResponse.credentialsNonExpired)
        assertEquals(credentialCreatedWithDefaultRole!!.accountNonLocked, credentialResponse.accountNonLocked)
        assertNotNull(credentialResponse.roles)
        assertEquals(1, credentialResponse.roles.size)
        assertTrue(credentialResponse.roles.any { it == Role.CUSTOMER })

        credentialCreatedWithDefaultRole = credentialResponse
        credentialCreatedWithDefaultRolePassword = password
        val loginCheckRequest = Login(credentialCreatedWithDefaultRole!!.username!!, credentialCreatedWithDefaultRolePassword!!)
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginCheckRequest)))
            .andExpect(status().isOk)
    }

    @Test(dependsOnMethods = [
        "create credentials with default role is successful"
    ])
    fun `update credentials with anonymous endpoint is successful`() {
        assertNotNull(credentialCreatedWithDefaultRole)
        assertNotNull(credentialCreatedWithDefaultRolePassword)
        val password = "12345"
        val loginUpdateRequest = Login(credentialCreatedWithDefaultRole!!.username!!, credentialCreatedWithDefaultRolePassword!!)
        val loginUpdateResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginUpdateRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.token").exists())
            .andReturn()
        val credentialUpdateRequest = CredentialUpdateRequest(username = "annadoe2100", password = password)
        val token = mapper.readValue(loginUpdateResult.response.contentAsString, Authorization::class.java).token
        val credentialUpdateResult = mockMvc.perform(put("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.customerId").exists())
            .andExpect(jsonPath("$.username").exists())
            .andExpect(jsonPath("$.enabled").exists())
            .andExpect(jsonPath("$.accountNonExpired").exists())
            .andExpect(jsonPath("$.credentialsNonExpired").exists())
            .andExpect(jsonPath("$.accountNonLocked").exists())
            .andExpect(jsonPath("$.roles").exists())
            .andReturn()
        val credentialResponse = mapper.readValue(credentialUpdateResult.response.contentAsString, CredentialResponse::class.java)
        assertNotNull(credentialResponse.id)
        assertEquals(credentialCreatedWithDefaultRole!!.id, credentialResponse.id)
        assertEquals(credentialCreatedWithDefaultRole!!.customerId, credentialResponse.customerId)
        assertEquals(credentialUpdateRequest.username, credentialResponse.username)
        assertEquals(credentialCreatedWithDefaultRole!!.enabled, credentialResponse.enabled)
        assertEquals(credentialCreatedWithDefaultRole!!.accountNonExpired, credentialResponse.accountNonExpired)
        assertEquals(credentialCreatedWithDefaultRole!!.credentialsNonExpired, credentialResponse.credentialsNonExpired)
        assertEquals(credentialCreatedWithDefaultRole!!.accountNonLocked, credentialResponse.accountNonLocked)
        assertNotNull(credentialResponse.roles)
        assertEquals(1, credentialResponse.roles.size)
        assertTrue(credentialResponse.roles.any { it == Role.CUSTOMER })

        credentialCreatedWithDefaultRole = credentialResponse
        credentialCreatedWithDefaultRolePassword = password
        val loginCheckRequest = Login(credentialCreatedWithDefaultRole!!.username!!, credentialCreatedWithDefaultRolePassword!!)
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginCheckRequest)))
            .andExpect(status().isOk)
    }

    @Test(dependsOnMethods = [
        "create credentials with default role is successful"
    ])
    fun `update credentials fails by wrong id`() {
        val loginUpdateRequest = Login(credentialCreatedWithDefaultRole!!.username!!, credentialCreatedWithDefaultRolePassword!!)
        val loginResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginUpdateRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val credentialUpdateRequest = CredentialUpdateRequest(username = "miadoe100", password = "asdfg")
        val token = mapper.readValue(loginResult.response.contentAsString, Authorization::class.java).token
        mockMvc.perform(put("/credentials/{id}", credentialCreatedWithDefaultRole!!.id + 666)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isUnauthorized)
    }

    @Test(dependsOnMethods = [
        "create credentials with default role is successful"
    ])
    fun `update credentials to administrator role fails by no admin authentication`() {
        assertNotNull(credentialCreatedWithDefaultRole)
        assertNotNull(credentialCreatedWithDefaultRolePassword)
        val loginUpdateRequest = Login(credentialCreatedWithDefaultRole!!.username!!, credentialCreatedWithDefaultRolePassword!!)
        val loginUpdateResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginUpdateRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val token = mapper.readValue(loginUpdateResult.response.contentAsString, Authorization::class.java).token
        val credentialUpdateRequest = CredentialUpdateRequest(username = "bethdoe100", password = "poiuy", roles = listOf(Role.ADMINISTRATOR))
        mockMvc.perform(put("/credentials/{id}", credentialCreatedWithDefaultRole!!.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "create credentials with default role is successful"
    ])
    fun `update credential fails by no authentication`() {
        assertNotNull(credentialCreatedWithDefaultRole)
        val credentialUpdateRequest = CredentialUpdateRequest(username = "annadoe100", password = "12345")
        mockMvc.perform(put("/credentials/{id}", credentialCreatedWithDefaultRole!!.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "login with root credentials is successful",
        "create credentials with default role is successful"
    ])
    fun `update credential with admin authentication is successful`() {
        assertNotNull(credentialCreatedWithDefaultRole)
        assertNotNull(administratorAuthorization)
        val password = "zxcvb"
        val credentialUpdateRequest = CredentialUpdateRequest(username = "sarahdoe100", password = password)
        val credentialUpdateResult = mockMvc.perform(put("/credentials/{id}", credentialCreatedWithDefaultRole!!.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${administratorAuthorization!!.type} ${administratorAuthorization!!.token}")
            .content(mapper.writeValueAsString(credentialUpdateRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").exists())
            .andExpect(jsonPath("$.customerId").exists())
            .andExpect(jsonPath("$.roles").exists())
            .andExpect(jsonPath("$.enabled").exists())
            .andExpect(jsonPath("$.accountNonExpired").exists())
            .andExpect(jsonPath("$.credentialsNonExpired").exists())
            .andExpect(jsonPath("$.accountNonLocked").exists())
            .andReturn()
        val credentialResponse = mapper.readValue(credentialUpdateResult.response.contentAsString, CredentialResponse::class.java)
        assertNotNull(credentialResponse.id)
        assertEquals(credentialCreatedWithDefaultRole!!.id, credentialResponse.id)
        assertEquals(credentialCreatedWithDefaultRole!!.customerId, credentialResponse.customerId)
        assertEquals(credentialUpdateRequest.username, credentialResponse.username)
        assertEquals(credentialCreatedWithDefaultRole!!.enabled, credentialResponse.enabled)
        assertEquals(credentialCreatedWithDefaultRole!!.accountNonExpired, credentialResponse.accountNonExpired)
        assertEquals(credentialCreatedWithDefaultRole!!.credentialsNonExpired, credentialResponse.credentialsNonExpired)
        assertEquals(credentialCreatedWithDefaultRole!!.accountNonLocked, credentialResponse.accountNonLocked)
        assertNotNull(credentialResponse.roles)
        assertEquals(1, credentialResponse.roles.size)
        assertTrue(credentialResponse.roles.any { it == Role.CUSTOMER })
        credentialCreatedWithDefaultRole = credentialResponse
        credentialCreatedWithDefaultRolePassword = password
        val loginCheckRequest = Login(credentialCreatedWithDefaultRole!!.username!!, credentialCreatedWithDefaultRolePassword!!)
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsBytes(loginCheckRequest)))
            .andExpect(status().isOk)
    }

    @Test(dependsOnMethods = [
        "create credentials with default role is successful"
    ])
    fun `delete credential fails by no authentication`() {
        assertNotNull(credentialCreatedWithDefaultRole)
        mockMvc.perform(delete("/credentials/{id}", credentialCreatedWithDefaultRole!!.id))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "create credentials with default role is successful",
        "update credentials to administrator role fails by no admin authentication",
        "update credentials fails by wrong id",
        "update credentials is successful",
        "update credentials with anonymous endpoint is successful",
        "update credential fails by no authentication",
        "update credential with admin authentication is successful",
        "delete credential fails by no authentication"
    ])
    fun `delete credentials is successful`() {
        assertNotNull(credentialCreatedWithDefaultRole)
        assertNotNull(credentialCreatedWithDefaultRolePassword)
        val loginDeleteAndCheckRequest = Login(credentialCreatedWithDefaultRole!!.username!!, credentialCreatedWithDefaultRolePassword!!)
        val loginDeleteResult = mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginDeleteAndCheckRequest)))
            .andExpect(status().isOk)
            .andReturn()
        val token: String = mapper.readValue(loginDeleteResult.response.contentAsString, Authorization::class.java).token
        mockMvc.perform(delete("/credentials/{id}", credentialCreatedWithDefaultRole!!.id)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} $token"))
            .andExpect(status().isOk)
        mockMvc.perform(post("/authorization/login")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(loginDeleteAndCheckRequest)))
            .andExpect(status().isUnauthorized)
        credentialCreatedWithDefaultRole = null
        credentialCreatedWithDefaultRolePassword = null
    }

    @Test
    fun `create credentials with supporter role fails by no admin authentication`() {
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "dianedoe100", "qwerty", listOf(Role.SUPPORTER))
        mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isForbidden)
    }

    @Test(dependsOnMethods = [
        "login with root credentials is successful"
    ])
    fun `create credential with supporter role is successful`() {
        assertNotNull(administratorAuthorization)
        val credentialCreateRequest = CredentialCreateRequest(Random.nextLong(1, 10000), "dianedoe100", "qwerty", listOf(Role.SUPPORTER))
        val result = mockMvc.perform(post("/credentials")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${administratorAuthorization?.token}")
            .content(mapper.writeValueAsString(credentialCreateRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.username").exists())
            .andExpect(jsonPath("$.customerId").exists())
            .andExpect(jsonPath("$.roles").exists())
            .andReturn()
        val credentialResponse = mapper.readValue(result.response.contentAsString, CredentialResponse::class.java)
        assertNotNull(credentialResponse.id)
        assertNotEquals(0, credentialResponse.id)
        assertEquals(credentialCreateRequest.customerId, credentialResponse.customerId)
        assertEquals(credentialCreateRequest.username, credentialResponse.username)
        assertEquals(true, credentialResponse.enabled)
        assertEquals(true, credentialResponse.accountNonExpired)
        assertEquals(true, credentialResponse.credentialsNonExpired)
        assertEquals(true, credentialResponse.accountNonLocked)
        assertNotNull(credentialResponse.roles)
        assertEquals(1, credentialResponse.roles.size)
        assertTrue(credentialResponse.roles.any { it == Role.SUPPORTER })
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