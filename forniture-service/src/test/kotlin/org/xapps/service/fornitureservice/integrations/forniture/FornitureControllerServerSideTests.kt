package org.xapps.service.fornitureservice.integrations.forniture

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
import org.xapps.service.fornitureservice.common.Containers
import org.xapps.service.fornitureservice.common.Utils
import org.xapps.service.fornitureservice.dtos.FornitureCreateRequest
import org.xapps.service.fornitureservice.dtos.FornitureResponse
import org.xapps.service.fornitureservice.dtos.FornitureUpdateRequest
import org.xapps.service.fornitureservice.security.SecurityParams

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@EnableWebMvc
@AutoConfigureMockMvc
class FornitureControllerServerSideTests : AbstractTestNGSpringContextTests() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var securityParams: SecurityParams

    private val mapper = ObjectMapper()


    @Test
    fun `create forniture is successful`() {
        val request = FornitureCreateRequest(
            name = "Forniture Name 100",
            description = "Forniture Description 100",
            price = 250.89f,
            smallPicturePath = "/smallpictures/100",
            largePicturePath = "/largepictures/100"
        )
        mockMvc.perform(post("/fornitures")
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
    }

    @Test
    fun `create forniture fails as no administrator authorization`() {
        val request = FornitureCreateRequest(
            name = "Forniture Name 101",
            description = "Forniture Description 101",
            price = 250.89f,
            smallPicturePath = "/smallpictures/101",
            largePicturePath = "/largepictures/101"
        )
        mockMvc.perform(post("/fornitures")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .content(mapper.writeValueAsString(request)))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `update forniture is successful`() {
        val requestCreate = FornitureCreateRequest(
            name = "Forniture Name 102",
            description = "Forniture Description 102",
            price = 280.50f,
            smallPicturePath = "/smallpictures/102",
            largePicturePath = "/largepictures/102"
        )
        val response = mockMvc.perform(post("/fornitures")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}")
            .content(mapper.writeValueAsString(requestCreate)))
            .andExpect(status().isCreated)
            .andReturn()
        val fornitureCreated = mapper.readValue(response.response.contentAsString, FornitureResponse::class.java)
        val requestUpdate = FornitureUpdateRequest(
            name = "${fornitureCreated.name} - Updated",
            description = "Forniture Description 102 - Updated",
            largePicturePath = "/largepictures/102updated"
        )
        mockMvc.perform(put("/fornitures/${fornitureCreated.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}")
            .content(mapper.writeValueAsString(requestUpdate)))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(fornitureCreated.id))
            .andExpect(jsonPath("$.name").value(requestUpdate.name))
            .andExpect(jsonPath("$.description").value(requestUpdate.description))
            .andExpect(jsonPath("$.price").value(fornitureCreated.price))
            .andExpect(jsonPath("$.smallPicturePath").value(fornitureCreated.smallPicturePath))
            .andExpect(jsonPath("$.largePicturePath").value(requestUpdate.largePicturePath))
    }

    @Test
    fun `update forniture fails as no administrator authorization`() {
        val requestCreate = FornitureCreateRequest(
            name = "Forniture Name 103",
            description = "Forniture Description 103",
            price = 80.50f,
            smallPicturePath = "/smallpictures/103",
            largePicturePath = "/largepictures/104"
        )
        val response = mockMvc.perform(post("/fornitures")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}")
            .content(mapper.writeValueAsString(requestCreate)))
            .andExpect(status().isCreated)
            .andReturn()
        val fornitureCreated = mapper.readValue(response.response.contentAsString, FornitureResponse::class.java)
        val requestUpdate = FornitureUpdateRequest(
            name = "${fornitureCreated.name} - Updated"
        )
        mockMvc.perform(put("/fornitures/${fornitureCreated.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .contentType(mapper.writeValueAsString(requestUpdate)))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `delete forniture is successful`() {
        val requestCreate = FornitureCreateRequest(
            name = "Forniture Name 104",
            description = "Forniture Description 104",
            price = 590.50f,
            smallPicturePath = "/smallpictures/104",
            largePicturePath = "/largepictures/104"
        )
        val response = mockMvc.perform(post("/fornitures")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}")
            .content(mapper.writeValueAsString(requestCreate)))
            .andExpect(status().isCreated)
            .andReturn()
        val fornitureCreated = mapper.readValue(response.response.contentAsString, FornitureResponse::class.java)
        mockMvc.perform(delete("/fornitures/${fornitureCreated.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}"))
            .andExpect(status().isOk)
    }

    @Test
    fun `delete forniture fails as no administrator authorization`() {
        val requestCreate = FornitureCreateRequest(
            name = "Forniture Name 105",
            description = "Forniture Description 105",
            price = 520.50f,
            smallPicturePath = "/smallpictures/105",
            largePicturePath = "/largepictures/105"
        )
        val response = mockMvc.perform(post("/fornitures")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}")
            .content(mapper.writeValueAsString(requestCreate)))
            .andExpect(status().isCreated)
            .andReturn()
        val fornitureCreated = mapper.readValue(response.response.contentAsString, FornitureResponse::class.java)
        mockMvc.perform(delete("/fornitures/${fornitureCreated.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isForbidden)
    }

    @Test
    fun `request forniture is successful`() {
        val requestCreate = FornitureCreateRequest(
            name = "Forniture Name 106",
            description = "Forniture Description 106",
            price = 590.50f,
            smallPicturePath = "/smallpictures/106",
            largePicturePath = "/largepictures/106"
        )
        val response = mockMvc.perform(post("/fornitures")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}")
            .content(mapper.writeValueAsString(requestCreate)))
            .andExpect(status().isCreated)
            .andReturn()
        val fornitureCreated = mapper.readValue(response.response.contentAsString, FornitureResponse::class.java)
        mockMvc.perform(get("/fornitures/${fornitureCreated.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(fornitureCreated.id))
            .andExpect(jsonPath("$.name").value(fornitureCreated.name))
            .andExpect(jsonPath("$.description").value(fornitureCreated.description))
            .andExpect(jsonPath("$.smallPicturePath").value(fornitureCreated.smallPicturePath))
            .andExpect(jsonPath("$.largePicturePath").value(fornitureCreated.largePicturePath))
        mockMvc.perform(get("/fornitures/${fornitureCreated.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(fornitureCreated.id))
            .andExpect(jsonPath("$.name").value(fornitureCreated.name))
            .andExpect(jsonPath("$.description").value(fornitureCreated.description))
            .andExpect(jsonPath("$.smallPicturePath").value(fornitureCreated.smallPicturePath))
            .andExpect(jsonPath("$.largePicturePath").value(fornitureCreated.largePicturePath))
    }

    @Test
    fun `request fornitures is successful`() {
        val requestCreate1 = FornitureCreateRequest(
            name = "Forniture Name 107",
            description = "Forniture Description 107",
            price = 590.50f,
            smallPicturePath = "/smallpictures/107",
            largePicturePath = "/largepictures/107"
        )
        val requestCreate2 = FornitureCreateRequest(
            name = "Forniture Name 108",
            description = "Forniture Description 108",
            price = 590.50f,
            smallPicturePath = "/smallpictures/108",
            largePicturePath = "/largepictures/108"
        )
        val response1 = mockMvc.perform(post("/fornitures")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}")
            .content(mapper.writeValueAsString(requestCreate1)))
            .andExpect(status().isCreated)
            .andReturn()
        val fornitureCreated1 = mapper.readValue(response1.response.contentAsString, FornitureResponse::class.java)
        val response2 = mockMvc.perform(post("/fornitures")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}")
            .content(mapper.writeValueAsString(requestCreate2)))
            .andExpect(status().isCreated)
            .andReturn()
        val fornitureCreated2 = mapper.readValue(response2.response.contentAsString, FornitureResponse::class.java)
        mockMvc.perform(get("/fornitures/ids/${fornitureCreated1.id},${fornitureCreated2.id}")
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .accept(MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, "${securityParams.jwtGeneration.type} ${Utils.generateToken(key = securityParams.jwtGeneration.key, useAdminRole = true)}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$[0].id").value(fornitureCreated1.id))
            .andExpect(jsonPath("$[0].name").value(fornitureCreated1.name))
            .andExpect(jsonPath("$[0].description").value(fornitureCreated1.description))
            .andExpect(jsonPath("$[0].smallPicturePath").value(fornitureCreated1.smallPicturePath))
            .andExpect(jsonPath("$[0].largePicturePath").value(fornitureCreated1.largePicturePath))
            .andExpect(jsonPath("$[1].id").value(fornitureCreated2.id))
            .andExpect(jsonPath("$[1].name").value(fornitureCreated2.name))
            .andExpect(jsonPath("$[1].description").value(fornitureCreated2.description))
            .andExpect(jsonPath("$[1].smallPicturePath").value(fornitureCreated2.smallPicturePath))
            .andExpect(jsonPath("$[1].largePicturePath").value(fornitureCreated2.largePicturePath))
    }

    companion object {
        @JvmStatic
        @DynamicPropertySource
        fun setDatasourceProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url") { Containers.MYSQL_CONTAINER?.jdbcUrl }
            registry.add("spring.datasource.username") { Containers.MYSQL_CONTAINER.username }
            registry.add("spring.datasource.password") { Containers.MYSQL_CONTAINER.password }
            registry.add("spring.elasticsearch.uris") { Containers.ELASTICSEARCH_CONTAINER.httpHostAddress }
        }
    }
}