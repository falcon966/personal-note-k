package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.RepositoryTest
import de.tuantu.personalnotek.persistence.NoteRepository
import de.tuantu.personalnotek.persistence.PersonRepository
import de.tuantu.personalnotek.persistence.model.NoteEntity
import de.tuantu.personalnotek.persistence.model.PersonEntity
import de.tuantu.personalnotek.service.domain.NoteDto
import org.assertj.core.api.Assertions.assertThat
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.java

@RepositoryTest
class NoteServiceTest {

    @Autowired
    lateinit var noteRepository: NoteRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    lateinit var noteService: NoteService

    private val easyRandom: EasyRandom = EasyRandom()

    @BeforeEach
    fun setup(){
        noteService = NoteService(noteRepository)
    }

    @Test
    fun `findAllByPersonId - find one note`() {
        // given
        val personEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity.id = null
        val person = personRepository.save(personEntity)

        val noteEntity = easyRandom.nextObject(NoteEntity::class.java)
        noteEntity.id = null
        noteEntity.person = person
        val note = noteRepository.save(noteEntity)

        // when
        val result = noteService.findAllByPersonId(person.id!!)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].text).isEqualTo(note.text)

    }

    @Test
    fun `upsertNoteForPerson - update successful`(){
        // given
        val personEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity.id = null
        val person = personRepository.save(personEntity)

        val noteEntity = easyRandom.nextObject(NoteEntity::class.java)
        noteEntity.id = null
        noteEntity.person = person
        val note = noteRepository.save(noteEntity)

        val noteToDeleteEntity = easyRandom.nextObject(NoteEntity::class.java)
        noteToDeleteEntity.id = null
        noteToDeleteEntity.person = person
        noteRepository.save(noteEntity)

        val updatedNote = easyRandom.nextObject(NoteDto::class.java).copy(
            id = note.id,
            text = "updated text"
        )

        // when
        noteService.upsertNoteForPerson(person, listOf(updatedNote))

        // then
        val notes = noteRepository.findAllByPersonId(person.id!!)
        assertThat(notes).hasSize(1)
        assertThat(notes[0].text).isEqualTo(updatedNote.text)
    }

}