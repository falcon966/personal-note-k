package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.persistence.NoteRepository
import de.tuantu.personalnotek.persistence.model.PersonEntity
import de.tuantu.personalnotek.service.domain.NoteDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class NoteService(
    private val repository: NoteRepository
) {

    fun findAllByPersonId(personId: UUID): List<NoteDto> = repository.findAllByPersonId(personId).map { note ->
        NoteDto.from(note)
    }

    @Transactional
    fun upsertNoteForPerson(personEntity: PersonEntity, notes: List<NoteDto>): List<NoteDto> {
        val personId = personEntity.id ?: throw IllegalArgumentException("person id cannot be null")
        deleteDeprecatedNoteForPerson(personId,notes.mapNotNull { it.id })
        return notes.map { note -> repository.save(NoteDto.toEntity(note, personEntity)).let {
            NoteDto.from(it)
        } }
    }

    private fun deleteDeprecatedNoteForPerson(personId: UUID, newNoteIds: List<UUID>) {
        val currentNotes = repository.findAllByPersonId(personId)
        val notesToDelete = currentNotes.filter { !newNoteIds.contains(it.id) }
        repository.deleteAll(notesToDelete)
    }

}