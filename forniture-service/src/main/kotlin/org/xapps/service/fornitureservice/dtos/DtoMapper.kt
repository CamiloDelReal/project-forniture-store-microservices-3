package org.xapps.service.fornitureservice.dtos

import org.xapps.service.fornitureservice.entities.Comment
import org.xapps.service.fornitureservice.entities.Forniture

fun Comment.toResponse(
    overrideId: Long? = null,
    overrideFornitureId: String? = null,
    overrideCustomerId: Long? = null,
    overrideEvaluation: Int? = null,
    overrideValue: String? = null
): CommentResponse =
    CommentResponse(
        id = overrideId ?: id,
        fornitureId = overrideFornitureId ?: fornitureId,
        customerId = overrideCustomerId ?: customerId,
        evaluation = overrideEvaluation ?: evaluation,
        value = overrideValue ?: value
    )

fun FornitureCreateRequest.toForniture(
    overrideName: String? = null,
    overrideDescription: String? = null,
    overridePrice: Float? = null,
    overrideSmallPicturePath: String? = null,
    overrideLargePicturePath: String? = null
): Forniture =
    Forniture(
        name = overrideName ?: name,
        description = overrideDescription ?: description,
        price = overridePrice ?: price,
        smallPicturePath = overrideSmallPicturePath ?: smallPicturePath,
        largePicturePath = overrideLargePicturePath ?: largePicturePath
    )

fun Forniture.toResponse(
    overrideId: String? = null,
    overrideName: String? = null,
    overrideDescription: String? = null,
    overridePrice: Float? = null,
    overrideSmallPicturePath: String? = null,
    overrideLargePicturePath: String? = null
): FornitureResponse =
    FornitureResponse(
        id = overrideId ?: id ?: "",
        name = overrideName ?: name,
        description = overrideDescription ?: description,
        price = overridePrice ?: price,
        smallPicturePath = overrideSmallPicturePath ?: smallPicturePath,
        largePicturePath = overrideLargePicturePath ?: largePicturePath
    )