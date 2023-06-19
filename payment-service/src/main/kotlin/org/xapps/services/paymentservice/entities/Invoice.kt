package org.xapps.services.paymentservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = Invoice.TABLE_NAME)
data class Invoice(
    @Id
    @Column(value = ID_FIELD)
    var id: Long = 0,

    @Column(value = PAYMENT_ID_FIELD)
    var paymentId: String,

    @Column(value = CUSTOMER_ID_FIELD)
    var customerId: Long,

    @Column(value = STATUS_FIELD)
    var status: Status
) {

    companion object {
        const val TABLE_NAME = "invoices"

        const val ID_FIELD = "id"
        const val ID_PROPERTY = "id"
        const val PAYMENT_ID_FIELD = "payment_id"
        const val PAYMENT_ID_PROPERTY = "paymentId"
        const val CUSTOMER_ID_FIELD = "customer_id"
        const val CUSTOMER_ID_PROPERTY = "customerId"
        const val STATUS_FIELD = "status"
        const val STATUS_PROPERTY = "status"
    }

}
