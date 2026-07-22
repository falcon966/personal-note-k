package de.tuantu.personalnotek.persistence

import de.tuantu.personalnotek.persistence.model.NoteEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface NoteRepository: JpaRepository<NoteEntity, UUID> {

    fun findAllByPersonId(personId: UUID): List<NoteEntity>

}