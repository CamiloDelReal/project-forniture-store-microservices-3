package org.xapps.services.deliveryservice.entities

import jakarta.persistence.*

@Entity
@Table(name = Delivery.TABLE_NAME)
data class Delivery(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_FIELD)
    var id: Long = 0,

    @Column(name = TIMESTAMP_FIELD)
    var timestamp: Long,

    @Column(name = INVOICE_ID_FIELD)
    var invoiceId: Long,

    @Column(name = PAYMENT_ID_FIELD)
    var paymentId: String,

    @Column(name = CUSTOMER_ID_FIELD)
    var customerId: Long,

    @Column(name = FIRST_NAME_PROPERTY)
    var firstName: String? = null,

    @Column(name = LAST_NAME_FIELD)
    var lastName: String? = null,

    @Column(name = PHONE_FIELD)
    var phone: String? = null,

    @Column(name = ADDRESS_LINE_1_FIELD)
    var addressLine1: String? = null,

    @Column(name = ADDRESS_LINE_2_FIELD)
    var addressLine2: String? = null,

    @Column(name = COUNTRY_FIELD)
    var country: String? = null,

    @Column(name = CITY_FIELD)
    var city: String? = null,

    @Column(name = POSTAL_CODE_FIELD)
    var postalCode: Int? = null,

    @Column(name = STATUS_FIELD)
    var status: Status,

    @OneToMany
    var products: List<Product>
) {

    companion object {
        const val TABLE_NAME = "deliveries"

        const val ID_FIELD = "id"
        const val ID_PROPERTY = "id"
        const val INVOICE_ID_FIELD = "invoice_id"
        const val INVOICE_ID_PROPERTY = "invoiceId"
        const val PAYMENT_ID_FIELD = "payment_id"
        const val PAYMENT_ID_PROPERTY = "paymentId"
        const val CUSTOMER_ID_FIELD = "customer_id"
        const val CUSTOMER_ID_PROPERTY = "customerId"
        const val FIRST_NAME_FIELD = "first_name"
        const val FIRST_NAME_PROPERTY = "firstName"
        const val LAST_NAME_FIELD = "last_name"
        const val LAST_NAME_PROPERTY = "lastName"
        const val TIMESTAMP_FIELD = "timestamp"
        const val TIMESTAMP_PROPERTY = "timestamp"
        const val PHONE_FIELD = "phone"
        const val PHONE_PROPERTY = "phone"
        const val ADDRESS_LINE_1_FIELD = "address_line_1"
        const val ADDRESS_LINE_1_PROPERTY = "addressLine1"
        const val ADDRESS_LINE_2_FIELD = "address_line_2"
        const val ADDRESS_LINE_2_PROPERTY = "addressLine2"
        const val COUNTRY_FIELD = "country"
        const val COUNTRY_PROPERTY = "country"
        const val CITY_FIELD = "city"
        const val CITY_PROPERTY = "city"
        const val POSTAL_CODE_FIELD = "postal_code"
        const val POSTAL_CODE_PROPERTY = "postalCode"
        const val STATUS_FIELD = "status"
        const val STATUS_PROPERTY = "status"
        const val PRODUCTS_FIELD = "products"
        const val PRODUCTS_PROPERTY = "products"
    }

}
