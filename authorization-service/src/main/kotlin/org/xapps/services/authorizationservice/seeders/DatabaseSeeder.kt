package org.xapps.services.authorizationservice.seeders

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.xapps.services.authorizationservice.entities.Credential
import org.xapps.services.authorizationservice.entities.Role
import org.xapps.services.authorizationservice.repositories.CredentialRepository
import org.xapps.services.authorizationservice.repositories.RoleRepository

@Component
class DatabaseSeeder @Autowired constructor(
    private val seederParams: SeederParams,
    private val roleRepository: RoleRepository,
    private val credentialRepository: CredentialRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @EventListener
    fun seed(event: ContextRefreshedEvent) {
        var adminRole: Role? = null
        if (roleRepository.count() == 0L) {
            val customerRole = Role(value = Role.CUSTOMER)
            val supporterRole = Role(value = Role.SUPPORTER)
            val currierRole = Role(value = Role.CURRIER)
            val supervisorRole = Role(value = Role.SUPERVISOR)
            adminRole = Role(value = Role.ADMINISTRATOR)
            roleRepository.saveAll(listOf(customerRole, supporterRole, currierRole, supervisorRole, adminRole))
        }
        if (credentialRepository.count() == 0L) {
            if (adminRole == null) {
                adminRole = roleRepository.findByValue(Role.ADMINISTRATOR)
            }
            val adminCredential = Credential(
                customerId = seederParams.rootCredential.customerId,
                username = seederParams.rootCredential.username,
                password = passwordEncoder.encode(seederParams.rootCredential.password),
                roles = listOf(adminRole!!),
                enabled = true,
                accountNonExpired = true,
                credentialsNonExpired = true,
                accountNonLocked = true
            )
            credentialRepository.save(adminCredential)
        }
    }

}