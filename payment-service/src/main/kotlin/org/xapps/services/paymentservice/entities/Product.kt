package org.xapps.services.paymentservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = Product.TABLE_NAME)
data class Product(
    @Id
    @Column(value = ID_FIELD)
    var id: Long = 0,

    @Column(value = UNIQUE_ID_FIELD)
    var uniqueId: String,

    @Column(value = NAME_FIELD)
    var name: String,

    @Column(value = PRICE_FIELD)
    var price: Float,

    @Column(value = COUNT_FIELD)
    var count: Int,
) {

    companion object {
        const val TABLE_NAME = "products"

        const val ID_FIELD = "id"
        const val ID_PROPERTY = "id"
        const val UNIQUE_ID_FIELD = "unique_id"
        const val UNIQUE_ID_PROPERTY = "uniqueId"
        const val NAME_FIELD = "name"
        const val NAME_PROPERTY = "name"
        const val PRICE_FIELD = "price"
        const val PRICE_PROPERTY = "price"
        const val COUNT_FIELD = "count"
        const val COUNT_PROPERTY = "count"
        const val INVOICE_ID_FIELD = "invoice_id"
        const val INVOICE_ID_PROPERTY = "invoiceId"
    }

}
