package org.xapps.services.authorizationservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.xapps.services.authorizationservice.dtos.*
import org.xapps.services.authorizationservice.entities.Credential
import org.xapps.services.authorizationservice.entities.Permission
import org.xapps.services.authorizationservice.entities.Role
import org.xapps.services.authorizationservice.repositories.CredentialRepository
import org.xapps.services.authorizationservice.repositories.PermissionRepository
import org.xapps.services.authorizationservice.repositories.RoleRepository
import org.xapps.services.authorizationservice.services.exceptions.IdNotFoundException
import org.xapps.services.authorizationservice.services.exceptions.MultipleCredentialsException
import org.xapps.services.authorizationservice.services.exceptions.UsernameNotAvailableException

@Service
class CredentialService @Autowired constructor(
    private val credentialRepository: CredentialRepository,
    private val roleRepository: RoleRepository,
    private val permissionRepository: PermissionRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return credentialRepository.findByUsername(username)?.let { credential ->
            User(
                credential.username,
                credential.password,
                credential.enabled,
                credential.accountNonExpired,
                credential.credentialsNonExpired,
                credential.accountNonLocked,
                credential.roles.map { SimpleGrantedAuthority(it.value) })
        } ?: run {
            throw UsernameNotFoundException("Username $username not found")
        }
    }

    fun readAll(from: Int, size: Int): List<CredentialResponse> =
        credentialRepository.findAll(
            PageRequest.of(
                from,
                size,
                Sort.by(
                    Sort.Direction.ASC,
                    Credential.USERNAME_PROPERTY
                )
            )
        ).get().map { it.toResponse() }.toList()

    fun read(id: Long): CredentialResponse =
        credentialRepository.findByIdOrNull(id)?.let { credential -> credential.toResponse() } ?: run {
            throw IdNotFoundException("Credential with Id $id not found")
        }

    fun readByCustomerId(customerId: Long): CredentialResponse =
        credentialRepository.findByCustomerId(customerId)?.let { credential -> credential.toResponse() } ?: run {
            throw IdNotFoundException("Credential with Customer Id $customerId not found")
        }

    fun create(request: CredentialCreateRequest): CredentialResponse =
        credentialRepository.findByUsername(request.username)?.let {
            throw UsernameNotAvailableException("Username ${request.username} is not available")
        } ?: run {
            if (credentialRepository.existsByCustomerId(request.customerId)) {
                throw MultipleCredentialsException("Customer with Id ${request.customerId} already has a credential")
            } else {
                val roles = if (request.roles.isNullOrEmpty()) {
                    listOf(roleRepository.findByValue(Role.CUSTOMER)!!)
                } else {
                    roleRepository.findByValueIn(request.roles!!).let { allRequestedRoles ->
                        allRequestedRoles.ifEmpty {
                            listOf(roleRepository.findByValue(Role.CUSTOMER)!!)
                        }
                    }
                }
                val credential = request.toCredential(passwordEncoder, roles)
                credentialRepository.save(credential)
                credential.toResponse()
            }
        }

    fun update(id: Long, request: CredentialUpdateRequest): CredentialResponse =
        credentialRepository.findByIdOrNull(id)?.let { credential ->
            request.username?.let { username ->
                if (credentialRepository.existsByIdNotAndUsername(id, username)) {
                    throw UsernameNotAvailableException("Username $username is not available")
                } else {
                    credential.username = username
                }
            }
            request.password?.let { password ->
                credential.password = passwordEncoder.encode(password)
            }
            request.customerId?.let { customerId ->
                if (credentialRepository.existsByIdNotAndCustomerId(id, customerId)) {
                    throw MultipleCredentialsException("Customer with Id $customerId already has a credential")
                } else {
                    credential.customerId = customerId
                }
            }
            request.roles?.let { roles ->
                roleRepository.findByValueIn(roles).let { allRequestedRoles ->
                    if (allRequestedRoles.isNotEmpty()) {
                        permissionRepository.deleteRolesByCredentialId(id)
                        credential.roles = allRequestedRoles
                        permissionRepository.saveAll(allRequestedRoles.map { role ->
                            Permission(Permission.CredentialRoleId(credentialId = id, roleId = role.id))
                        })
                    }
                }
            }
            request.enabled?.let { enabled ->
                credential.enabled = enabled
            }
            request.accountNonExpired?.let { accountNonExpired ->
                credential.accountNonExpired = accountNonExpired
            }
            request.credentialsNonExpired?.let { credentialsNonExpired ->
                credential.credentialsNonExpired = credentialsNonExpired
            }
            request.accountNonLocked?.let { accountNonLocked ->
                credential.accountNonLocked = accountNonLocked
            }
            credentialRepository.save(credential)
            credential.toResponse()
        } ?: run {
            throw IdNotFoundException("Credential with Id $id not found")
        }

    fun delete(id: Long) =
        if(credentialRepository.existsById(id)) {
            permissionRepository.deleteRolesByCredentialId(id)
            credentialRepository.deleteById(id)
        } else {
            throw IdNotFoundException("Credential with Id $id not found")
        }

    fun deleteByCustomerId(customerId: Long) =
        credentialRepository.findByCustomerId(customerId)?.let { credential ->
            permissionRepository.deleteRolesByCredentialId(credential.id)
            credentialRepository.deleteById(credential.id)
        } ?: run {
            throw IdNotFoundException("Credential with customer Id $customerId not found")
        }


    fun requiresAdminRole(request: CredentialCreateRequest): Boolean =
        request.roles?.any { it in listOf(Role.SUPPORTER, Role.ADMINISTRATOR) } == true

    fun requiresAdminRole(request: CredentialUpdateRequest): Boolean =
        request.roles?.any { it in listOf(Role.SUPPORTER, Role.ADMINISTRATOR) } == true

    fun hasAdminRole(request: Credential): Boolean =
        request.roles.find { it.value == Role.ADMINISTRATOR } != null

    fun hasAdminRole(request: CredentialResponse): Boolean =
        request.roles.find { it == Role.ADMINISTRATOR } != null

    fun hasAdminRole(request: CredentialCreateRequest): Boolean =
        request.roles?.find { it == Role.ADMINISTRATOR } != null

    fun hasAdminRole(request: CredentialUpdateRequest): Boolean =
        request.roles?.find { it == Role.ADMINISTRATOR } != null

    fun hasStatusUpdates(request: CredentialUpdateRequest): Boolean =
        (request.enabled != null || request.credentialsNonExpired != null || request.accountNonExpired != null || request.accountNonLocked != null)

}