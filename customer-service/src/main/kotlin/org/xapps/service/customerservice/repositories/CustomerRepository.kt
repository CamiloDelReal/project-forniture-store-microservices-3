package org.xapps.service.customerservice.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.xapps.service.customerservice.entities.Customer

@Repository
interface CustomerRepository : JpaRepository<Customer, Long> {

    fun existsByEmailOrPhone(email: String, phone: String): Boolean

    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): Customer?
    fun existsByIdNotAndEmail(id: Long, email: String): Boolean
    fun findByIdNotAndEmail(id: Long, email: String): Customer?

    fun existsByPhone(phone: String): Boolean
    fun findByPhone(phone: String): Customer?
    fun existsByIdNotAndPhone(id: Long, phone: String): Boolean
    fun findByIdNotAndPhone(id: Long, phone: String): Customer?
}