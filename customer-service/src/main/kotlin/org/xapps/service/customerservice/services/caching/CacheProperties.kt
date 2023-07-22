package org.xapps.service.customerservice.services.caching

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("hazelcast")
data class CacheProperties(
    var clusterName: String = "",
    var members: List<String> = emptyList()
)
