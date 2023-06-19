package org.xapps.service.fornitureservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.xapps.service.fornitureservice.dtos.CommentCreateRequest
import org.xapps.service.fornitureservice.dtos.CommentResponse
import org.xapps.service.fornitureservice.dtos.CommentUpdateRequest
import org.xapps.service.fornitureservice.dtos.toResponse
import org.xapps.service.fornitureservice.entities.Comment
import org.xapps.service.fornitureservice.entities.Forniture
import org.xapps.service.fornitureservice.repositories.CommentRepository
import org.xapps.service.fornitureservice.services.exceptions.CommentNotAvailableException
import org.xapps.service.fornitureservice.services.exceptions.IdNotFoundException

@Service
class CommentService @Autowired constructor(
    private val commentRepository: CommentRepository
) {

    fun readAll(from: Int, size: Int): List<CommentResponse> =
        commentRepository.findAll(
            PageRequest.of(
                from,
                size,
                Sort.by(
                    Sort.Direction.ASC,
                    Forniture.NAME_PROPERTY
                )
            )
        ).map { it.toResponse() }.toList()

    fun read(id: Long): CommentResponse? =
        commentRepository.findByIdOrNull(id)?.let { comment ->
            comment.toResponse()
        } ?: run {
            throw IdNotFoundException("Comment with Id $id not found")
        }

    fun readByCustomer(customerId: Long): List<CommentResponse> =
        commentRepository.findAllByCustomerId(customerId).map { it.toResponse() }

    fun readByForniture(fornitureId: String): List<CommentResponse> =
        commentRepository.findAllByFornitureId(fornitureId).map { it.toResponse() }

    fun create(request: CommentCreateRequest): CommentResponse =
        if (!commentRepository.existsByFornitureIdAndCustomerId(request.fornitureId, request.customerId)) {
            val comment = Comment(
                fornitureId = request.fornitureId,
                customerId = request.customerId,
                evaluation = request.evaluation,
                value = request.value
            )
            commentRepository.save(comment)
            comment.toResponse()
        } else {
            throw CommentNotAvailableException("Only one comment per forniture is allowed for a customer")
        }

    fun update(id: Long, request: CommentUpdateRequest): CommentResponse =
        commentRepository.findByIdOrNull(id)?.let { comment ->
            request.evaluation?.let { evaluation ->
                comment.evaluation = evaluation
            }
            request.value?.let { value ->
                comment.value = value
            }
            commentRepository.save(comment)
            comment.toResponse()
        } ?: run {
            throw IdNotFoundException("Comment with Id $id not found")
        }

    fun delete(id: Long) =
        if(commentRepository.existsById(id)) {
            commentRepository.deleteById(id)
        } else {
            throw IdNotFoundException("Comment with Id $id not found")
        }

    fun isAuthor(id: Long, customerId: Long): Boolean =
        commentRepository.findByIdOrNull(id)?.let {
            it.customerId == customerId
        } ?: run {
            throw IdNotFoundException("Comment with Id $id not found")
        }
}