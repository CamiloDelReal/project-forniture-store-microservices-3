package org.xapps.services.authorizationservice.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import jakarta.persistence.*

@Entity
@Table(name = Credential.TABLE_NAME)
data class Credential(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = ID_FIELD)
    var id: Long = 0,

    @Column(name = CUSTOMER_ID_FIELD)
    var customerId: Long,

    @Column(name = USERNAME_FIELD)
    var username: String? = null,

    @Column(name = PASSWORD_FIELD)
    var password: String? = null,

    @ManyToMany(cascade = [CascadeType.DETACH], fetch = FetchType.EAGER)
    @JoinTable(
        name = Permission.TABLE_NAME,
        joinColumns = [JoinColumn(name = Permission.CREDENTIAL_ID_FIELD, referencedColumnName = ID_FIELD)],
        inverseJoinColumns = [JoinColumn(name = Permission.ROLE_ID_FIELD, referencedColumnName = Role.ID_FIELD)]
    )
    @JsonBackReference
    var roles: List<Role>,

    @Column(name = ENABLED_FIELD)
    var enabled: Boolean,

    @Column(name = ACCOUNT_NON_EXPIRED_FIELD)
    var accountNonExpired: Boolean,

    @Column(name = CREDENTIALS_NON_EXPIRED_FIELD)
    var credentialsNonExpired: Boolean,

    @Column(name = ACCOUNT_NON_LOCKED_FIELD)
    var accountNonLocked: Boolean
) {
    companion object {
        const val TABLE_NAME = "credentials"

        const val ID_FIELD = "id"
        const val ID_PROPERTY = "id"
        const val CUSTOMER_ID_FIELD = "customer_id"
        const val CUSTOMER_ID_PROPERTY = "customerId"
        const val USERNAME_FIELD = "username"
        const val USERNAME_PROPERTY = "username"
        const val PASSWORD_FIELD = "password"
        const val PASSWORD_PROPERTY = "password"
        const val ROLES_PROPERTY = "roles"
        const val ENABLED_FIELD = "enabled"
        const val ENABLED_PROPERTY = "enabled"
        const val ACCOUNT_NON_EXPIRED_FIELD = "account_non_expired"
        const val ACCOUNT_NON_EXPIRED_PROPERTY = "accountNonExpired"
        const val CREDENTIALS_NON_EXPIRED_FIELD = "credentials_non_expired"
        const val CREDENTIALS_NON_EXPIRED_PROPERTY = "credentialsNonExpired"
        const val ACCOUNT_NON_LOCKED_FIELD = "account_non_locked"
        const val ACCOUNT_NON_LOCKED_PROPERTY = "accountNonLocked"
    }
}