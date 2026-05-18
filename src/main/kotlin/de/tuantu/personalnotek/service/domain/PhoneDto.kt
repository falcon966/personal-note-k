package de.tuantu.personalnotek.service.domain

import de.tuantu.personalnotek.persistence.model.PersonEntity
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
                number = phone.number,
                id = phone.id,
                name = phone.name,
            )

        fun toEntity(
            phoneDto: PhoneDto,
            personEntity: PersonEntity?,
        ): PhoneEntity =
            PhoneEntity(
                number = phoneDto.number,
                name = phoneDto.name,
                id = phoneDto.id,
                person = personEntity,
            )
    }
}
