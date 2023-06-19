package org.xapps.services.authorizationservice.entities

import jakarta.persistence.*

@Entity
@Table(name = Role.TABLE_NAME)
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_FIELD)
    var id: Long = 0,

    @Column(name = VALUE_FIELD)
    var value: String
) {

    companion object {
        const val TABLE_NAME = "roles"

        const val ID_FIELD = "id"
        const val ID_PROPERTY = "id"
        const val VALUE_FIELD = "value"
        const val VALUE_PROPERTY = "value"

        const val CUSTOMER = "Customer"
        const val SUPPORTER = "Supporter"
        const val CURRIER = "Currier"
        const val SUPERVISOR = "Supervisor"
        const val ADMINISTRATOR = "Administrator"
    }

}