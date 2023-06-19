package org.xapps.services.deliveryservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.xapps.services.deliveryservice.dtos.CustomerResponse
import org.xapps.services.deliveryservice.dtos.DeliveryResponse
import org.xapps.services.deliveryservice.dtos.toResponse
import org.xapps.services.deliveryservice.entities.Delivery
import org.xapps.services.deliveryservice.entities.Status
import org.xapps.services.deliveryservice.repositories.DeliveryRepository
import org.xapps.services.deliveryservice.services.exceptions.IdNotFoundException

@Service
class DeliveryService @Autowired constructor(
    private val deliveryRepository: DeliveryRepository,
    private val customerService: CustomerService
) {

    fun readAll(from: Int, size: Int): List<DeliveryResponse> =
        deliveryRepository.findAll(
            PageRequest.of(
                from,
                size,
                Sort.by(
                    Sort.Direction.ASC,
                    Delivery.TIMESTAMP_FIELD
                )
            )
        ).get().map { it.toResponse() }.toList()

    fun read(id: Long): DeliveryResponse =
        deliveryRepository.findByIdOrNull(id)?.let { delivery ->
            delivery.toResponse()
        } ?: run {
            throw IdNotFoundException("Delivery with Id $id not found")
        }

    fun markDelivered(id: Long): DeliveryResponse =
        deliveryRepository.findByIdOrNull(id)?.let { delivery ->
            delivery.status = Status.DELIVERED
            delivery.toResponse()
        } ?: run {
            throw IdNotFoundException("Delivery with Id $id not found")
        }

    fun resolveDeliveryAddress(id: Long): DeliveryResponse =
        deliveryRepository.findByIdOrNull(id)?.let { delivery ->
            val customer = customerService.readById(delivery.customerId)
            fillDeliveryAddress(delivery, customer)
            deliveryRepository.save(delivery)
            delivery.toResponse()
        } ?: run {
            throw IdNotFoundException("Delivery with Id $id not found")
        }

    private fun fillDeliveryAddress(delivery: Delivery, customer: CustomerResponse?) {
        try {
            delivery.firstName = customer?.firstName
            delivery.lastName = customer?.lastName
            delivery.phone = customer?.phone
            delivery.addressLine1 = customer?.addressLine1
            delivery.addressLine2 = customer?.addressLine2
            delivery.country = customer?.country
            delivery.city = customer?.city
            delivery.postalCode = customer?.postalCode
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun processDelivery(delivery: Delivery) {
        val customer = customerService.readById(delivery.customerId)
        fillDeliveryAddress(delivery, customer)
        deliveryRepository.save(delivery)
    }

}