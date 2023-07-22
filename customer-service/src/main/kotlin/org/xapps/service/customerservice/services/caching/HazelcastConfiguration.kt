package org.xapps.service.customerservice.services.caching

import com.hazelcast.client.config.ClientConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class HazelcastConfiguration {

    @Bean
    fun clientConfig(
        cacheProperties: CacheProperties
    ): ClientConfig =
         ClientConfig().apply {
            cacheProperties.members.forEach {  member ->
                networkConfig.addAddress(member)
            }
            clusterName = cacheProperties.clusterName
        }

}