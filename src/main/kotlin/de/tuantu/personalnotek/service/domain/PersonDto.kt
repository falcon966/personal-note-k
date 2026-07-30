package de.tuantu.personalnotek.service.domain

import de.tuantu.personalnotek.persistence.model.PersonEntity
import java.util.UUID

data class PersonDto(
    val id: UUID?,
    val firstname: String,
    val lastname: String,
    val email: String?,
    val phones: List<PhoneDto>,
    val notes: List<NoteDto>
) {
    companion object {
        fun from(
            person: PersonEntity,
            phones: List<PhoneDto> = emptyList(),
            notes: List<NoteDto> = emptyList()
        ): PersonDto =
            PersonDto(
                id = person.id,
                firstname = person.firstName,
                lastname = person.lastName,
                email = person.email,
                phones = phones,
                notes = notes
            )

        fun toEntity(personDto: PersonDto, userId: UUID): PersonEntity =
            PersonEntity(
                id = personDto.id,
                firstName = personDto.firstname,
                lastName = personDto.lastname,
                email = personDto.email,
                userId = userId
            )
    }
}
