package org.xapps.services.gatewayservice.routers

import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod

@Configuration
class MainRouter {

    @Bean
    fun provideRouteLocator(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            .route { predicateSpec ->
                predicateSpec
                    .path("/authorization/login")
                    .and()
                    .method(HttpMethod.POST)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://authorizationservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/authorization/token/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.DELETE)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://authorizationservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/credentials/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://authorizationservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/roles/**")
                    .and()
                    .method(HttpMethod.GET)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://authorizationservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/customers/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://customerservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/fornitures/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://fornitureservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/comments/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://fornitureservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/carts/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://cartservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/payments/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://paymentservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/deliveries/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://currierservice")
            }
            .route { predicateSpec ->
                predicateSpec
                    .path("/support/**")
                    .and()
                    .method(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE)
                    .filters { filterSpec ->
                        filterSpec
                            .rewritePath("/(?<segment>.*)", "/\${segment}")
                    }
                    .uri("lb://supportservice")
            }
            .build()
    }
}