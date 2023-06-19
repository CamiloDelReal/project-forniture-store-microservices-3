package org.xapps.service.fornitureservice.entities

import jakarta.persistence.*

@Entity
@Table(name = Comment.TABLE_NAME)
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_FIELD)
    var id: Long = 0,

    @Column(name = FORNITURE_ID_FIELD)
    var fornitureId: String,

    @Column(name = CUSTOMER_ID_FIELD)
    var customerId: Long,

    @Column(name = EVALUATION_FIELD)
    var evaluation: Int,

    @Column(name = VALUE_FIELD)
    var value: String
) {

    companion object {
        const val TABLE_NAME = "comments"

        const val ID_FIELD = "id"
        const val ID_PROPERTY = "id"
        const val FORNITURE_ID_FIELD = "forniture_id"
        const val FORNITURE_ID_PROPERTY = "fornitureId"
        const val CUSTOMER_ID_FIELD = "customer_id"
        const val CUSTOMER_ID_PROPERTY = "customerId"
        const val EVALUATION_FIELD = "evaluation"
        const val EVALUATION_PROPERTY = "evaluation"
        const val VALUE_FIELD = "value"
        const val VALUE_PROPERTY = "value"
    }

}