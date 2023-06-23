# Forniture Store
Microservices-based application

### Features
- Authorization service
  * Credentials CRUD operations
  * Role based
  * Login
- Customer service
  * Customer CRUD operations
- Forniture service
  * Forniture CRUD operations
- Cart service
  * Cart CRUD operations
- Payment service
  * Payment operations
  * Simple service, does not use any sandbox for payment
- Delivery service
  * Delivery operations
  * Only read operations
- Support service
  * Simple chat service for customer and support

### Architecture design
<p float="left">
<img src="https://github.com/CamiloDelReal/project-forniture-store-microservices-3/blob/main/design/deployment-design.jpg" width="80%" height="80%" />
</p>

### Technologies used
- Spring Boot
  * Web application
  * Security
  * Validation
  * Actuator
  * JPA
  * R2DBC
  * WebFlux
  * Messaging
  * Retry
  * WebClient
  * Micrometer
- Spring Cloud
  * Configuration service
  * Discovery service
  * Gateway
  * Bus
  * Request/Response tracing
  * Feign
- Eureka
  * Discovery service provider
- RabbitMQ
	* Bus communication to update configuration in client services
  * Communication between services
- Kafka
  * Communication between services
- Zipkin
  * Distributed tracing system
- Prometheus and Grafana
  * Alert and monitoring
- FileBeats, Logstash, Elasticsearch and Kibana
  * Logs
- Datasources
  * MySQL
  * Redis
  * MongoDB
  * PostgreSQL
  * Elasticsearch
- Kotlin
- Json Web Token
- Docker
  * Deployment
- Test
  * JUnit5
  * TestNG
  * TestContainers
  * WireMock

### Others
- Script for packaging services
- Script for building docker images for services
- Docker compose deployment script

### ToDoS
- Forniture service
  * Forniture seeder
- Cart service
  * Integration tests for messaging
- Payment service
  * Integration tests for messaging
- Delivery service
  * Integration tests
- Support service
  * Integration tests for chat monitoring endpoint
- Finishing Postman collection
- Web UI
