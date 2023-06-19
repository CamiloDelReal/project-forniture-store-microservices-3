package org.xapps.services.authorizationservice.services

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import org.xapps.services.authorizationservice.dtos.*
import org.xapps.services.authorizationservice.entities.Token
import org.xapps.services.authorizationservice.repositories.CredentialRepository
import org.xapps.services.authorizationservice.repositories.TokenRepository
import org.xapps.services.authorizationservice.security.SecurityParams
import org.xapps.services.authorizationservice.services.exceptions.InvalidCredentialException
import org.xapps.services.authorizationservice.services.exceptions.TokenInvalidException
import org.xapps.services.authorizationservice.services.exceptions.TokenRevocationException
import java.time.Instant
import java.util.*

@Service
class AuthorizationService @Autowired constructor(
    private val authenticationManager: AuthenticationManager,
    private val credentialRepository: CredentialRepository,
    private val tokenRepository: TokenRepository,
    private val securityParams: SecurityParams,
    private val objectMapper: ObjectMapper
) {

    fun login(login: Login): Authorization? {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(login.username, login.password)
        )
        return credentialRepository.findByUsername(login.username)?.let { credential ->
            val currentTimestamp = Instant.now().toEpochMilli()
            val expirationRaw = currentTimestamp + securityParams.jwtGeneration.validity
            val expiration = Date(expirationRaw)
            val subject = objectMapper.writeValueAsString(credential.toResponse())
            val id = UUID.randomUUID().toString()
            val token = Jwts.builder()
                .setSubject(subject)
                .setId(id)
                .setIssuedAt(Date(currentTimestamp))
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, securityParams.jwtGeneration.key)
                .compact()
            tokenRepository.save(Token(id, credential.id, expirationRaw))
            Authorization(token, securityParams.jwtGeneration.type, expirationRaw)
        } ?: run {
            throw InvalidCredentialException("Username ${login.username} not found")
        }
    }

    fun getAuthentication(authorizationHeaderValue: String): UsernamePasswordAuthenticationToken? {
        val token = authorizationHeaderValue.replace(securityParams.jwtGeneration.type, "").trim()
        val claims: Claims = Jwts.parser()
            .setSigningKey(securityParams.jwtGeneration.key)
            .parseClaimsJws(token)
            .body
        return if(claims.id.isNullOrEmpty() || claims.id.isBlank()) {
            throw TokenInvalidException("Token does not have Id")
        } else if(tokenRepository.existsById(claims.id)) {
            val subject: String = claims.subject
            val uncheckedCredential = objectMapper.readValue(subject, CredentialResponse::class.java)
            credentialRepository.findByUsername(uncheckedCredential.username!!)?.let { credential ->
                if (!credential.enabled) {
                    throw DisabledException("Credential is not enabled")
                } else if (!credential.credentialsNonExpired) {
                    throw CredentialsExpiredException("Credential is expired")
                } else if (!credential.accountNonExpired) {
                    throw AccountExpiredException("Account is expired")
                } else if (!credential.accountNonLocked) {
                    throw LockedException("Account is locked")
                } else {
                    val authorities = credential.roles.map { SimpleGrantedAuthority(it.value) }
                    UsernamePasswordAuthenticationToken(credential, null, authorities)
                }
            } ?: run {
                throw InvalidCredentialException("Credential with username ${uncheckedCredential.username} does not exist")
            }
        } else {
            throw TokenRevocationException("Authorization token is revoked")
        }

    }

    fun isAuthorizationValid(token: String) {
        getAuthentication(token)
    }

    fun revokeToken(id: String) {
        if (tokenRepository.existsById(id)) {
            tokenRepository.deleteById(id)
        } else {
            throw TokenRevocationException("No token found with Id $id")
        }
    }

    fun revokeTokensByCredential(credentialId: Long) {
        tokenRepository.findAllByCredentialId(credentialId).apply {
            tokenRepository.deleteAll(this)
        }
    }

    fun isOwner(id: String, credentialId: Long): Boolean =
        tokenRepository.existsByIdAndCredentialId(id, credentialId)

    fun readAllTokenByCredential(credentialId: Long): List<TokenResponse> =
        tokenRepository.findAllByCredentialId(credentialId).map { it.toResponse() }
}