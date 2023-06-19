package org.xapps.services.authorizationservice.entities

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.io.Serializable

@Entity
@Table(name = Permission.TABLE_NAME)
data class Permission(
    @EmbeddedId
    var id: CredentialRoleId
) {

    @Embeddable
    data class CredentialRoleId(
        @Column(name = CREDENTIAL_ID_FIELD)
        var credentialId: Long,

        @Column(name = ROLE_ID_FIELD)
        var roleId: Long
    ) : Serializable

    companion object {
        const val TABLE_NAME = "permissions"

        const val CREDENTIAL_ID_FIELD = "credential_id"
        const val ROLE_ID_FIELD = "role_id"
    }
}