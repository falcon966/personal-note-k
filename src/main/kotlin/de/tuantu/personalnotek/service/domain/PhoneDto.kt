package de.tuantu.personalnotek.service.domain

import de.tuantu.personalnotek.persistence.model.PhoneEntity
import java.util.*

data class PhoneDto(
    val number: String,
    val id: UUID?,
    val name: String,
) {
    companion object {
        fun from(phone: PhoneEntity): PhoneDto =
            PhoneDto(
                phone.number,
                phone.id,
                phone.name,
            )
    }
}
