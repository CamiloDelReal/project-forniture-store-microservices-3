package org.xapps.services.paymentservice.services

import org.springframework.stereotype.Service
import org.xapps.services.paymentservice.dtos.CardRequest

@Service
class CardValidatorService {

    fun validateCard(cardRequest: CardRequest): Boolean {
        return true
    }

}