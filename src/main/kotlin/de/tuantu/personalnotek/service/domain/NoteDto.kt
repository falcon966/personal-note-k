package de.tuantu.personalnotek.service.domain

import de.tuantu.personalnotek.persistence.model.NoteEntity
import de.tuantu.personalnotek.persistence.model.PersonEntity
import java.util.UUID

data class NoteDto(
    val id: UUID?,
    val text: String?
) {
    companion object {
        fun from(entity: NoteEntity): NoteDto {
            return NoteDto(
                id = entity.id,
                text = entity.text
            )
        }

        fun toEntity(dto: NoteDto, personEntity: PersonEntity): NoteEntity {
            return NoteEntity(
                id = dto.id,
                text = dto.text,
                person = personEntity,
                userId = personEntity.userId
            )
        }
    }
}
