package org.xapps.service.fornitureservice.repositories

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.xapps.service.fornitureservice.entities.Comment

@Repository
interface CommentRepository : JpaRepository<Comment, Long> {

    fun findAllByCustomerId(customerId: Long): List<Comment>
    fun findAllByFornitureId(fornitureId: String): List<Comment>

    fun findAllByFornitureIdIn(fornitureIds: List<String>): List<Comment>

    fun existsByFornitureIdAndCustomerId(fornitureId: String, customerId: Long): Boolean

}