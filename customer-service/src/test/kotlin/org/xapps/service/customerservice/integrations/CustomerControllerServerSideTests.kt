package org.xapps.service.customerservice.integrations

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
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
import org.xapps.service.customerservice.common.Containers
import org.xapps.service.customerservice.common.Utils
import org.xapps.service.customerservice.dtos.CredentialResponse
import org.xapps.service.customerservice.dtos.CustomerCreateRequest
import org.xapps.service.customerservice.dtos.CustomerResponse
import org.xapps.service.customerservice.dtos.CustomerUpdateRequest
import org.xapps.service.customerservice.security.SecurityParams

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EnableWebMvc
@AutoConfigureMockMvc
@AutoConfigureWireMock(port = 0)
class CustomerControllerServerSideTests : AbstractTestNGSpringContextTests() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var securityParams: SecurityParams

    private val mapper = ObjectMapper()

    @Value("\${authorization.service.url}")
    private lateinit var urlll: String

    @Autowired
    private lateinit var env: Environment

    @Test
    fun `create customer is successful`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user100",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user100@gmail.com",
            phone = "+34115355523100",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(100, 100, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.firstName").value(customerCreateRequest.firstName))
            .andExpect(jsonPath("$.lastName").value(customerCreateRequest.lastName))
            .andExpect(jsonPath("$.email").value(customerCreateRequest.email))
            .andExpect(jsonPath("$.phone").value(customerCreateRequest.phone))
            .andExpect(jsonPath("$.addressLine1").value(customerCreateRequest.addressLine1))
            .andExpect(jsonPath("$.addressLine2").value(customerCreateRequest.addressLine2))
            .andExpect(jsonPath("$.country").value(customerCreateRequest.country))
            .andExpect(jsonPath("$.city").value(customerCreateRequest.city))
            .andExpect(jsonPath("$.postalCode").value(customerCreateRequest.postalCode))
            .andExpect(jsonPath("$.username").value(customerCreateRequest.username))
            .andReturn()
    }

    @Test
    fun `create customer fails by wrong credentials or duplicated data`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user101",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user101@gmail.com",
            phone = "+34115355523101",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(101, 101, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)

        // Email duplicated
        mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isBadRequest)

        // Phone duplicated
        customerCreateRequest.email = "user1011@gmail.com"
        mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isBadRequest)

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.BAD_REQUEST.value()))
        )

        urlll
        val gg = env.get("authorization.service.url")
        gg

        // Username duplicated
        customerCreateRequest.phone = "341153555231011"
        mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `read customer is successful`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user102",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user102@gmail.com",
            phone = "+34115355523102",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(102, 102, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        val customerResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()

        val customer = mapper.readValue(customerResponse.response.contentAsString, CustomerResponse::class.java)

        WireMock.stubFor(WireMock.get(urlEqualTo("/credentials/customer/${customer.id}"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(102, customer.id!!, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!!, useAdminRole = false)
        val authz = "${securityParams.jwtGeneration.type} $token"

        mockMvc.perform(get("/customers/current")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(customer.id))
            .andExpect(jsonPath("$.firstName").value(customer.firstName))
            .andExpect(jsonPath("$.lastName").value(customer.lastName))
            .andExpect(jsonPath("$.email").value(customer.email))
            .andExpect(jsonPath("$.phone").value(customer.phone))
            .andExpect(jsonPath("$.addressLine1").value(customer.addressLine1))
            .andExpect(jsonPath("$.addressLine2").value(customer.addressLine2))
            .andExpect(jsonPath("$.country").value(customer.country))
            .andExpect(jsonPath("$.city").value(customer.city))
            .andExpect(jsonPath("$.postalCode").value(customer.postalCode))
            .andExpect(jsonPath("$.username").value(customer.username))

        mockMvc.perform(get("/customers/id/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(customer.id))
            .andExpect(jsonPath("$.firstName").value(customer.firstName))
            .andExpect(jsonPath("$.lastName").value(customer.lastName))
            .andExpect(jsonPath("$.email").value(customer.email))
            .andExpect(jsonPath("$.phone").value(customer.phone))
            .andExpect(jsonPath("$.addressLine1").value(customer.addressLine1))
            .andExpect(jsonPath("$.addressLine2").value(customer.addressLine2))
            .andExpect(jsonPath("$.country").value(customer.country))
            .andExpect(jsonPath("$.city").value(customer.city))
            .andExpect(jsonPath("$.postalCode").value(customer.postalCode))
            .andExpect(jsonPath("$.username").value(customer.username))

        val tokenAdmin = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!!, useAdminRole = true)
        val authzAdmin = "${securityParams.jwtGeneration.type} $tokenAdmin"

        mockMvc.perform(get("/customers/id/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authzAdmin))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(customer.id))
            .andExpect(jsonPath("$.firstName").value(customer.firstName))
            .andExpect(jsonPath("$.lastName").value(customer.lastName))
            .andExpect(jsonPath("$.email").value(customer.email))
            .andExpect(jsonPath("$.phone").value(customer.phone))
            .andExpect(jsonPath("$.addressLine1").value(customer.addressLine1))
            .andExpect(jsonPath("$.addressLine2").value(customer.addressLine2))
            .andExpect(jsonPath("$.country").value(customer.country))
            .andExpect(jsonPath("$.city").value(customer.city))
            .andExpect(jsonPath("$.postalCode").value(customer.postalCode))
            .andExpect(jsonPath("$.username").value(customer.username))
    }

    @Test
    fun `read customer fails by wrong credentials`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user103",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user103@gmail.com",
            phone = "+34115355523103",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(103, 103, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        WireMock.stubFor(WireMock.get(urlEqualTo("/credentials/current"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(102, 102, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        val customerResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()

        val customer = mapper.readValue(customerResponse.response.contentAsString, CustomerResponse::class.java)

        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!! + 99, useAdminRole = false)
        val authz = "${securityParams.jwtGeneration.type} $token"

        mockMvc.perform(get("/customers/current")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz))
            .andExpect(status().isNotFound)

        mockMvc.perform(get("/customers/id/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `update customer is successful`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user104",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user104@gmail.com",
            phone = "+34115355523104",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(104, 104, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        val customerResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()

        val customer = mapper.readValue(customerResponse.response.contentAsString, CustomerResponse::class.java)

        val customerUpdateRequest = CustomerUpdateRequest(
            firstName = "first name 2",
            lastName = "last name 2",
            email = "user104@gmail.com",
            phone = "+34115355523104",
            addressLine1 = "address line 12",
            addressLine2 = "address line 22",
            country = "country2",
            city = "city2",
            postalCode = 1002
        )

        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!!, useAdminRole = false)
        val authz = "${securityParams.jwtGeneration.type} $token"

        mockMvc.perform(put("/customers/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz)
            .content(mapper.writeValueAsString(customerUpdateRequest)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(customer.id))
            .andExpect(jsonPath("$.firstName").value(customerUpdateRequest.firstName))
            .andExpect(jsonPath("$.lastName").value(customerUpdateRequest.lastName))
            .andExpect(jsonPath("$.email").value(customerUpdateRequest.email))
            .andExpect(jsonPath("$.phone").value(customerUpdateRequest.phone))
            .andExpect(jsonPath("$.addressLine1").value(customerUpdateRequest.addressLine1))
            .andExpect(jsonPath("$.addressLine2").value(customerUpdateRequest.addressLine2))
            .andExpect(jsonPath("$.country").value(customerUpdateRequest.country))
            .andExpect(jsonPath("$.city").value(customerUpdateRequest.city))
            .andExpect(jsonPath("$.postalCode").value(customerUpdateRequest.postalCode))
            .andReturn()
    }

    @Test
    fun `update customer fails by wrong credentials or duplicated data`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user105",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user105@gmail.com",
            phone = "+34115355523105",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(105, 105, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        val customerResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()

        val customerCreateRequest2 = CustomerCreateRequest(
            username = "user1052",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user1052@gmail.com",
            phone = "+341153555231052",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(1052, 1052, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest2)))
            .andExpect(status().isCreated)

        val customer = mapper.readValue(customerResponse.response.contentAsString, CustomerResponse::class.java)

        val customerUpdateRequest = CustomerUpdateRequest(
            firstName = "first name 2",
            lastName = "last name 2",
            email = "user105@gmail.com",
            phone = "+34115355523105",
            addressLine1 = "address line 12",
            addressLine2 = "address line 22",
            country = "country2",
            city = "city2",
            postalCode = 1002
        )

        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!!, useAdminRole = false)
        val authz = "${securityParams.jwtGeneration.type} $token"

        // Email duplicated
        customerUpdateRequest.email = "user1052@gmail.com"
        customerUpdateRequest.phone = "+34115355523105"
        mockMvc.perform(put("/customers/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz)
            .content(mapper.writeValueAsString(customerUpdateRequest)))
            .andExpect(status().isBadRequest)

        // Phone duplicated
        customerUpdateRequest.email = "user105@gmail.com"
        customerUpdateRequest.phone = "+341153555231052"
        mockMvc.perform(put("/customers/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz)
            .content(mapper.writeValueAsString(customerUpdateRequest)))
            .andExpect(status().isBadRequest)

        val tokenWrong = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!! + 99, useAdminRole = false)
        val authzWrong = "${securityParams.jwtGeneration.type} $tokenWrong"

        // Wrong credential
        customerUpdateRequest.email = "user1059@gmail.com"
        customerUpdateRequest.phone = "+341153555231059"
        mockMvc.perform(put("/customers/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authzWrong)
            .content(mapper.writeValueAsString(customerUpdateRequest)))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `delete customer is successful`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user106",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user106@gmail.com",
            phone = "+34115355523106",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(106, 106, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        val customerResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()

        val customer = mapper.readValue(customerResponse.response.contentAsString, CustomerResponse::class.java)

        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!!, useAdminRole = false)
        val authz = "${securityParams.jwtGeneration.type} $token"

        WireMock.stubFor(WireMock.delete(urlEqualTo("/credentials/customer/${customer.id}"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value()))
        )

        mockMvc.perform(delete("/customers/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz))
            .andExpect(status().isOk)
    }

    @Test
    fun `delete customer fails by wrong credentials`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user107",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user107@gmail.com",
            phone = "+34115355523107",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(107, 107, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        val customerResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()

        val customer = mapper.readValue(customerResponse.response.contentAsString, CustomerResponse::class.java)

        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!! + 99, useAdminRole = false)
        val authz = "${securityParams.jwtGeneration.type} $token"

        mockMvc.perform(delete("/customers/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `update customer using admin credential`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user108",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user108@gmail.com",
            phone = "+34115355523108",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(108, 108, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        val customerResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()

        val customer = mapper.readValue(customerResponse.response.contentAsString, CustomerResponse::class.java)

        val customerUpdateRequest = CustomerUpdateRequest(
            firstName = "first name 2",
            lastName = "last name 2",
            email = "user1082@gmail.com",
            phone = "+341153555231082",
            addressLine1 = "address line 12",
            addressLine2 = "address line 22",
            country = "country2",
            city = "city2",
            postalCode = 1002
        )

        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!! + 99, useAdminRole = true)
        val authz = "${securityParams.jwtGeneration.type} $token"

        mockMvc.perform(put("/customers/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz)
            .content(mapper.writeValueAsString(customerUpdateRequest)))
            .andExpect(status().isOk)
    }

    @Test
    fun `delete customer using admin credentials`() {
        val customerCreateRequest = CustomerCreateRequest(
            username = "user109",
            password = "123456",
            firstName = "first name",
            lastName = "last name",
            email = "user109@gmail.com",
            phone = "+34115355523109",
            addressLine1 = "address line 1",
            addressLine2 = "address line 2",
            country = "country",
            city = "city",
            postalCode = 100
        )

        WireMock.stubFor(WireMock.post(urlEqualTo("/credentials"))
            .willReturn(aResponse()
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(mapper.writeValueAsString(CredentialResponse(109, 109, customerCreateRequest.username, listOf("Guest"), true, true, true, true))))
        )

        val customerResponse = mockMvc.perform(post("/customers")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(customerCreateRequest)))
            .andExpect(status().isCreated)
            .andReturn()

        val customer = mapper.readValue(customerResponse.response.contentAsString, CustomerResponse::class.java)

        val token = Utils.generateToken(key = securityParams.jwtGeneration.key, customerId = customer.id!! + 99, useAdminRole = true)
        val authz = "${securityParams.jwtGeneration.type} $token"

        WireMock.stubFor(WireMock.delete(urlEqualTo("/credentials/customer/${customer.id}"))
            .willReturn(aResponse()
                .withStatus(HttpStatus.OK.value()))
        )

        mockMvc.perform(delete("/customers/{id}", customer.id)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, authz))
            .andExpect(status().isOk)
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { Containers.MYSQL_CONTAINER?.jdbcUrl }
            registry.add("spring.datasource.username") { Containers.USERNAME }
            registry.add("spring.datasource.password") { Containers.PASSWORD }
            registry.add("authorization.service.url") { "localhost:\${wiremock.server.port}" }
            registry.add("spring.rabbitmq.host") { Containers.RABBIT_CONTAINER.host }
            registry.add("spring.rabbitmq.port") { Containers.RABBIT_CONTAINER.getMappedPort(5672) }
            registry.add("spring.rabbitmq.username") { Containers.USERNAME }
            registry.add("spring.rabbitmq.password") { Containers.PASSWORD }
        }
    }
}