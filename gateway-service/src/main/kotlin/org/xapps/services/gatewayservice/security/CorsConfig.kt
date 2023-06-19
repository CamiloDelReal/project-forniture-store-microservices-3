package org.xapps.services.gatewayservice.security
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource


@Configuration
class CorsConfig @Autowired constructor(
    private val securityParams: SecurityParams
) {

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration()
        corsConfig.allowedOrigins = securityParams.origins.urls
        corsConfig.maxAge = securityParams.origins.maxAge
        corsConfig.allowedMethods = securityParams.origins.allowedMethods
        corsConfig.allowedHeaders = securityParams.origins.allowedHeaders
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)
        return CorsWebFilter(source)
    }

}