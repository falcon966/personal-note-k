package de.tuantu.personalnotek.persistance

import de.tuantu.personalnotek.RepositoryTest
import de.tuantu.personalnotek.persistence.NoteRepository
import de.tuantu.personalnotek.persistence.PersonRepository
import de.tuantu.personalnotek.persistence.model.NoteEntity
import de.tuantu.personalnotek.persistence.model.PersonEntity
import org.assertj.core.api.Assertions.assertThat
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@RepositoryTest
class NoteRepositoryTest {

    @Autowired
    lateinit var noteRepository: NoteRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    private val easyRandom: EasyRandom = EasyRandom()

    @Test
    fun `findAllByPersonId - find one`() {
        // given
        val person = easyRandom.nextObject(PersonEntity::class.java)
        person.id = null
        val savedPerson = personRepository.save(person)

        val noteEntity = easyRandom.nextObject(NoteEntity::class.java)
        noteEntity.id = null
        noteEntity.person = savedPerson
        noteRepository.save(noteEntity)
        val noteEntity2 = easyRandom.nextObject(NoteEntity::class.java)
        noteEntity2.id = null
        noteEntity2.person = null

        noteRepository.saveAll(listOf(noteEntity, noteEntity2))

        // when
        val result = noteRepository.findAllByPersonId(savedPerson.id!!)

        // then
        assertThat(result.first())
            .usingRecursiveComparison().ignoringFields("id").isEqualTo(noteEntity)

    }

}