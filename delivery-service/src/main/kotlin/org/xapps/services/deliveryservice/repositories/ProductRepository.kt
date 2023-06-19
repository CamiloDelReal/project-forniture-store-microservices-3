package org.xapps.services.deliveryservice.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.xapps.services.deliveryservice.entities.Product

@Repository
interface ProductRepository: JpaRepository<Product, Long>