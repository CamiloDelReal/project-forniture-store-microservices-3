package org.xapps.service.fornitureservice.services

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.xapps.service.fornitureservice.dtos.*
import org.xapps.service.fornitureservice.entities.Forniture
import org.xapps.service.fornitureservice.repositories.FornitureRepository
import org.xapps.service.fornitureservice.services.exceptions.IdNotFoundException

@Service
class FornitureService @Autowired constructor(
    private val fornitureRepository: FornitureRepository
) {

    fun readAll(from: Int, size: Int): List<FornitureResponse> =
        fornitureRepository.findAll(
            PageRequest.of(
                from,
                size,
                Sort.by(
                    Sort.Direction.ASC,
                    Forniture.NAME_PROPERTY
                )
            )
        ).get().map { it.toResponse() }.toList()


    fun read(id: String): FornitureResponse =
        fornitureRepository.findByIdOrNull(id)?.let {
            it.toResponse()
        } ?: run {
            throw IdNotFoundException("Forniture with Id $id not found")
        }


    fun read(ids: List<String>): List<FornitureResponse> =
        fornitureRepository.findAllById(ids).mapNotNull { it.toResponse() }

    fun create(request: FornitureCreateRequest): FornitureResponse =
        fornitureRepository.save(request.toForniture()).toResponse()

    fun update(id: String, request: FornitureUpdateRequest): FornitureResponse =
        fornitureRepository.findByIdOrNull(id)?.let { forniture ->
            request.name?.let { name ->
                forniture.name = name
            }
            request.description?.let { description ->
                forniture.description = description
            }
            request.price?.let { price ->
                forniture.price = price
            }
            request.smallPicturePath?.let { smallPicturePath ->
                forniture.smallPicturePath = smallPicturePath
            }
            request.largePicturePath?.let { largePicturePath ->
                forniture.largePicturePath = largePicturePath
            }
            fornitureRepository.save(forniture)
            forniture.toResponse()
        } ?: run {
            throw IdNotFoundException("Forniture with Id $id not found")
        }

    fun delete(id: String) =
        if(fornitureRepository.existsById(id)) {
            fornitureRepository.deleteById(id)
        } else {
            throw IdNotFoundException("Forniture with Id $id not found")
        }

}