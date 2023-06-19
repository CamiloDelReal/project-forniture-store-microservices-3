package org.xapps.services.supportservice.controllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.xapps.services.supportservice.dtos.ChatResponse
import org.xapps.services.supportservice.dtos.MessageRequest
import org.xapps.services.supportservice.dtos.MessageResponse
import org.xapps.services.supportservice.entities.Message
import org.xapps.services.supportservice.security.Credential
import org.xapps.services.supportservice.services.SupportService
import org.xapps.services.supportservice.services.exceptions.CredentialsNotProvided
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toFlux

@RestController
@RequestMapping(path = ["/support"])
class SupportController @Autowired constructor(
    private val supportService: SupportService
) {
    @PostMapping(
        path = ["/request"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Customer')")
    fun request(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authzHeader: String
    ): Mono<ChatResponse> =
        ReactiveSecurityContextHolder.getContext()
            .flatMap { context ->
                val principal = context.authentication?.principal as Credential
                supportService.request(authzHeader, principal.customerId)
            }
            .switchIfEmpty {
                Mono.error(CredentialsNotProvided("Request requires Customer credentials to proceed"))
            }

    @PostMapping(
        path = ["/response/{id}"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("isAuthenticated() and hasAnyAuthority('Administrator', 'Supporter')")
    fun response(
        @RequestHeader(HttpHeaders.AUTHORIZATION) authzHeader: String,
        @PathVariable("id") chatId: String
    ): Mono<ChatResponse> =
        ReactiveSecurityContextHolder.getContext()
            .flatMap { context ->
                val principal = context.authentication?.principal as Credential
                supportService.response(authzHeader, chatId, principal.customerId)
            }
            .switchIfEmpty {
                Mono.error(CredentialsNotProvided("Request requires Supporter credentials to proceed"))
            }

    @GetMapping(
        path = ["/monitor/chat/{id}"],
        produces = [MediaType.APPLICATION_NDJSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun monitorChat(
        @PathVariable("id") chatId: String
    ): Flux<ChatResponse> =
        validateAccessToChatFlux(chatId) {
            supportService.monitorChat(chatId)
        }

    @GetMapping(
        path = ["/monitor/chat/{id}/messages"],
        produces = [MediaType.APPLICATION_NDJSON_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun monitorChatMessages(
        @PathVariable("id") chatId: String
    ): Flux<Message> =
        validateAccessToChatFlux(chatId) {
            supportService.monitorChatMessages(chatId)
        }

    @PostMapping(
        path = ["/chat/{id}/messages"]
    )
    @PreAuthorize("isAuthenticated()")
    fun sendMessage(
        @PathVariable("id") chatId: String,
        @RequestBody request: MessageRequest
    ): Mono<MessageResponse> =
        validateAccessToChatMono(chatId) {
            supportService.sendMessage(chatId, request)
        }

    @PostMapping(
        path = ["/chat/{id}/close"]
    )
    @PreAuthorize("isAuthenticated()")
    fun closeChat(
        @PathVariable("id") chatId: String
    ): Mono<ChatResponse> =
        validateAccessToChatMono(chatId) {
            supportService.closeChat(chatId)
        }

    private fun <T> validateAccessToChatFlux(chatId: String, event: () -> Flux<T>): Flux<T> =
        ReactiveSecurityContextHolder.getContext()
            .toFlux()
            .flatMap {  context ->
                val principal = context.authentication?.principal as Credential
                if(principal.isCustomer()) {
                    supportService.canCustomerAccessChat(chatId, principal.customerId)
                        .toFlux()
                        .flatMap { canMonitor ->
                            if (canMonitor) {
                                event.invoke()
                            } else {
                                Flux.error(CredentialsNotProvided("Customer is not authorized to monitor the chat $chatId"))
                            }
                        }
                } else {
                    event.invoke()
                }
            }
            .switchIfEmpty {
                Flux.error<T>(CredentialsNotProvided("Request requires credentials to proceed"))
            }

    private fun <T> validateAccessToChatMono(chatId: String, event: () -> Mono<T>): Mono<T> =
        ReactiveSecurityContextHolder.getContext()
            .flatMap {  context ->
                val principal = context.authentication?.principal as Credential
                if(principal.isCustomer()) {
                    supportService.canCustomerAccessChat(chatId, principal.customerId)
                        .flatMap { canMonitor ->
                            if (canMonitor) {
                                event.invoke()
                            } else {
                                Mono.error(CredentialsNotProvided("Customer is not authorized to monitor the chat $chatId"))
                            }
                        }
                } else {
                    event.invoke()
                }
            }
            .switchIfEmpty {
                Mono.error(CredentialsNotProvided("Request requires credentials to proceed"))
            }
}