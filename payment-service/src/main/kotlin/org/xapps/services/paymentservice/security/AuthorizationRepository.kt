package org.xapps.services.paymentservice.security

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping

@FeignClient("\${authorization.service.url}")
interface AuthorizationRepository {

    @PostMapping(
        path = ["/authorization/token/validate"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun validate(request: TokenValidateRequest): Credential

}