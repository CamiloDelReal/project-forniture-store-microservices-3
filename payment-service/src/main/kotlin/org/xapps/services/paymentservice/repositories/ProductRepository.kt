package org.xapps.services.paymentservice.repositories

import org.springframework.data.r2dbc.repository.R2dbcRepository
import org.springframework.stereotype.Repository
import org.xapps.services.paymentservice.entities.Product

@Repository
interface ProductRepository : R2dbcRepository<Product, Long>