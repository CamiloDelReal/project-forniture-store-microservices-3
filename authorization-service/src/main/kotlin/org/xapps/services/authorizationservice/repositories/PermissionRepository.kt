package org.xapps.services.authorizationservice.repositories

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.xapps.services.authorizationservice.entities.Permission


@Repository
interface PermissionRepository : JpaRepository<Permission, Permission.CredentialRoleId> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM permissions WHERE credential_id = :credentialId", nativeQuery = true)
    fun deleteRolesByCredentialId(credentialId: Long): Int

}