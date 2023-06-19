package org.xapps.service.fornitureservice.repositories

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository
import org.xapps.service.fornitureservice.entities.Forniture

@Repository
interface FornitureRepository: ElasticsearchRepository<Forniture, String>
