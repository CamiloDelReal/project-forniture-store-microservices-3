package org.xapps.services.cartservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.xapps.services.cartservice.dtos.*
import org.xapps.services.cartservice.repositories.CartRepository
import org.xapps.services.cartservice.services.exceptions.FornitureServiceException
import org.xapps.services.cartservice.services.exceptions.IdNotFoundException
import org.xapps.services.cartservice.services.exceptions.MessagingServiceException
import org.xapps.services.messaging.dtos.PaymentRequest
import org.xapps.services.messaging.dtos.ProductRequest
import java.util.UUID

@Service
class CartService @Autowired constructor(
    private val cartRepository: CartRepository,
    private val fornitureService: FornitureService,
    private val messagingService: MessagingService
) {

    fun read(customerId: Long): CartResponse =
        cartRepository.findByIdOrNull(customerId)?.let { cart ->
            cart.toResponse()
        } ?: run {
            throw IdNotFoundException("Cart with Id $customerId not found")
        }

    fun create(request: CartCreateRequest): CartResponse {
        val cart = cartRepository.findByIdOrNull(request.customerId)?.let { customerCart ->
            customerCart.fornitures.apply {
                clear()
                putAll(request.fornitures)
            }
            customerCart
        } ?: run {
            request.toCart()
        }
        cartRepository.save(cart)
        return cart.toResponse()
    }

    fun update(customerId: Long, request: CartUpdateRequest): CartResponse =
        cartRepository.findByIdOrNull(customerId)?.let { cart ->
            cart.fornitures.putAll(request.fornitures)
            cart.toResponse()
        } ?: run {
            throw IdNotFoundException("Cart with Id $customerId not found")
        }

    fun delete(customerId: Long) =
        if(cartRepository.existsById(customerId)) {
            cartRepository.deleteById(customerId)
        } else {
            throw IdNotFoundException("Cart with Id $customerId not found")
        }

    fun checkout(customerId: Long) {
        cartRepository.findByIdOrNull(customerId)?.let { cart ->
            val fornitureKeys = cart.fornitures.keys.toList()
            val fornitures = fornitureService.readFornituresByIds(fornitureKeys)?.associateBy({it.id}, {it})
            fornitures?.let { validatedFornitures ->
                if (fornitureKeys.size == fornitures.size) {
                    val products = fornitureKeys.map { key ->
                        ProductRequest(
                            id = key,
                            name = validatedFornitures[key]!!.name,
                            price = validatedFornitures[key]!!.price,
                            count = cart.fornitures[key]!!
                        )
                    }
                    val paymentRequest = PaymentRequest(
                        id = UUID.randomUUID().toString(),
                        customerId = customerId,
                        products = products
                    )
                    try {
                        messagingService.sendPaymentRequest(paymentRequest)
                    } catch (ex: Exception) {
                        throw MessagingServiceException(ex.message)
                    }
                    cartRepository.deleteById(customerId)
                } else {
                    throw FornitureServiceException("Not all products from cart are being returned from forniture service")
                }
            } ?: run {
                throw FornitureServiceException("Products from cart are not being returned from forniture service")
            }
        } ?: run {
            throw IdNotFoundException("Cart with Id $customerId not found")
        }
    }

}