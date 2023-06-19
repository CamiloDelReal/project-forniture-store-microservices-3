package org.xapps.services.deliveryservice.entities

import jakarta.persistence.*

@Entity
@Table(name = Product.TABLE_NAME)
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_FIELD)
    var id: Long = 0,

    @Column(name = UNIQUE_ID_FIELD)
    var uniqueId: String,

    @Column(name = NAME_FIELD)
    var name: String,

    @Column(name = COUNT_FIELD)
    var count: Int
) {

    companion object {
        const val TABLE_NAME = "products"

        const val ID_FIELD = "id"
        const val ID_PROPERTY = "id"
        const val UNIQUE_ID_FIELD = "unique_id"
        const val UNIQUE_ID_PROPERTY = "uniqueId"
        const val NAME_FIELD = "name"
        const val NAME_PROPERTY = "name"
        const val COUNT_FIELD = "count"
        const val COUNT_PROPERTY = "count"
    }

}
