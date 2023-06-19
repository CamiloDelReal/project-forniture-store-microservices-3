package org.xapps.services.cartservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.redis.core.index.Indexed

@RedisHash(Cart.HASH_NAME)
data class Cart(
    @Id
    @Indexed
    var customerId: Long,

    var fornitures: MutableMap<String, Int>
) {
    companion object {
        const val HASH_NAME = "carts"

        const val CUSTOMER_ID_PROPERTY = "customerId"
        const val FORNITURES_PROPERTY = "fornitures"
    }
}