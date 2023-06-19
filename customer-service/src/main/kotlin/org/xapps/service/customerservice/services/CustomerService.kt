package org.xapps.service.customerservice.services

import feign.FeignException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.xapps.service.customerservice.dtos.*
import org.xapps.service.customerservice.entities.Customer
import org.xapps.service.customerservice.entities.CustomerStatus
import org.xapps.service.customerservice.repositories.CustomerRepository
import org.xapps.service.customerservice.services.exceptions.*

@Service
class CustomerService @Autowired constructor(
    private val customerRepository: CustomerRepository,
    private val credentialService: CredentialService,
    private val messagingService: MessagingService
) {

    fun readAll(from: Int, size: Int): List<CustomerResponse> =
        customerRepository.findAll(
            PageRequest.of(
                from,
                size,
                Sort.by(
                    Sort.Direction.ASC,
                    Customer.LAST_NAME_PROPERTY
                )
            )
        ).get().map { it.toResponse() }.toList()

    fun read(id: Long, authzHeader: String): CustomerResponse =
        customerRepository.findByIdOrNull(id)?.let { customer ->
            val credentialResponse = credentialService.readByCustomerId(id, authzHeader)
            customer.toResponse(overrideUsername = credentialResponse!!.username)
        } ?: run {
            throw IdNotFoundException("Customer with Id $id not found")
        }

    fun create(request: CustomerCreateRequest): CustomerResponse =
        if(!customerRepository.existsByEmail(request.email)) {
            request.phone?.let { phone ->
                if(customerRepository.existsByPhone(phone)) {
                    throw PhoneNotAvailableException("Phone $phone is not available for new accounts")
                }
            }
            val customer = request.toCustomer()
            customerRepository.save(customer)
            val createCredentialRequest = CredentialCreateRequest(
                customerId = customer.id,
                username = request.username,
                password = request.password
            )
            try {
                val credentialResponse = credentialService.create(createCredentialRequest)
                customer.status = CustomerStatus.CREATED
                customerRepository.save(customer)
            } catch (ex: FeignException.BadRequest) {
                throw UsernameNotAvailableException("Username ${request.username} is not available for new accounts")
            } catch (ex: Exception) {
                messagingService.publishCreateCredentials(createCredentialRequest)
            }
            customer.toResponse(overrideUsername = request.username)
        } else {
            throw EmailNotAvailableException("Email ${request.email} is not available for new accounts")
        }

    fun updateCustomerStatus(id: Long) {
        customerRepository.findByIdOrNull(id)?.let { customer ->
            customer.status = CustomerStatus.CREATED
            customerRepository.save(customer)
        } ?: run {
            throw IdNotFoundException("Customer with Id $id not found")
        }
    }

    fun update(id: Long, request: CustomerUpdateRequest): CustomerResponse =
        customerRepository.findByIdOrNull(id)?.let { customer ->
            if(customer.status == CustomerStatus.CREATED) {
                request.firstName?.let { firstName ->
                    customer.firstName = firstName
                }
                request.lastName?.let { lastName ->
                    customer.lastName = lastName
                }
                request.phone?.let { phone ->
                    if (!customerRepository.existsByIdNotAndPhone(id, phone)) {
                        customer.phone = phone
                    } else {
                        throw PhoneNotAvailableException("Phone $phone is not available")
                    }
                }
                request.email?.let { email ->
                    if (!customerRepository.existsByIdNotAndEmail(id, email)) {
                        customer.email = email
                    } else {
                        throw EmailNotAvailableException("Email $email is not available")
                    }
                }
                request.addressLine1?.let { addressLine1 ->
                    customer.addressLine1 = addressLine1
                }
                request.addressLine2?.let { addressLine2 ->
                    customer.addressLine2 = addressLine2
                }
                request.country?.let { country ->
                    customer.country = country
                }
                request.city?.let { city ->
                    customer.city = city
                }
                request.postalCode?.let { postalCode ->
                    customer.postalCode = postalCode
                }
                customerRepository.save(customer)
                customer.toResponse()
            } else {
                throw CustomerNotCreatedException("Customer with Id $id is still in queue for creation")
            }
        } ?: run {
            throw IdNotFoundException("Customer with Id $id not found")
        }

    fun delete(id: Long, authzHeader: String? = null) =
        if(customerRepository.existsById(id)) {
            try {
                authzHeader?.let {
                    credentialService.deleteByCustomerId(id, authzHeader)
                    customerRepository.deleteById(id)
                } ?: run {
                    messagingService.publishDeleteCredentials(CredentialDeleteRequest(customerId = id))
                }
            } catch (ex: Exception) {
                messagingService.publishDeleteCredentials(CredentialDeleteRequest(customerId = id))
            }
        } else {
            throw IdNotFoundException("Customer with Id $id not found")
        }

    fun deleteFromRepository(id: Long) =
        if(customerRepository.existsById(id)) {
            customerRepository.deleteById(id)
        } else {
            throw IdNotFoundException("Customer with Id $id not found")
        }

}