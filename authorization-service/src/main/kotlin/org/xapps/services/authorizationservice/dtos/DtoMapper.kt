package org.xapps.services.authorizationservice.dtos

import org.springframework.security.crypto.password.PasswordEncoder
import org.xapps.services.authorizationservice.entities.Credential
import org.xapps.services.authorizationservice.entities.Role
import org.xapps.services.authorizationservice.entities.Token

fun Credential.toResponse(
    overrideId: Long? = null,
    overrideUsername: String? = null,
    overrideCustomerId: Long? = null,
    overrideRoles: List<String>? = null,
    overrideEnabled: Boolean? = null,
    overrideAccountNonExpired: Boolean? = null,
    overrideAccountNonLocked: Boolean? = null
): CredentialResponse =
    CredentialResponse(
        id = overrideId ?: id,
        username = overrideUsername ?: username,
        customerId = overrideCustomerId ?: customerId,
        roles = overrideRoles ?: roles.map { it.value },
        enabled = overrideEnabled ?: enabled,
        accountNonExpired = overrideAccountNonExpired ?: accountNonExpired,
        credentialsNonExpired = overrideAccountNonExpired ?: credentialsNonExpired,
        accountNonLocked = overrideAccountNonLocked ?: accountNonLocked
    )


fun CredentialCreateRequest.toCredential(
    passwordEncoder: PasswordEncoder,
    roles: List<Role>
): Credential =
    Credential(
        username = username,
        password = passwordEncoder.encode(password),
        customerId = customerId,
        roles = roles,
        enabled = true,
        accountNonExpired = true,
        credentialsNonExpired = true,
        accountNonLocked = true
    )

fun Token.toResponse(
    overrideId: String? = null,
    overrideCredentialId: Long? = null,
    overrideExpiration: Long? = null
): TokenResponse =
    TokenResponse(
        id = overrideId ?: id,
        credentialId = overrideCredentialId ?: credentialId,
        expiration = overrideExpiration ?: expiration
    )