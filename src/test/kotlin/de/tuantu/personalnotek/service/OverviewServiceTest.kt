package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.persistence.model.PersonEntity
import de.tuantu.personalnotek.service.domain.NoteDto
import de.tuantu.personalnotek.service.domain.PersonDto
import de.tuantu.personalnotek.service.domain.PhoneDto
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.util.UUID
import kotlin.jvm.java

@ExtendWith(MockKExtension::class)
class OverviewServiceTest {
    @MockK
    lateinit var personService: PersonService

    @MockK
    lateinit var phoneService: PhoneService

    @MockK
    lateinit var noteService: NoteService

    @InjectMockKs
    lateinit var overviewService: OverviewService

    val easyRandom: EasyRandom = EasyRandom()

    @Test
    fun `get all persons - get all personDtos`() {
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        val phoneDto: PhoneDto = easyRandom.nextObject(PhoneDto::class.java)
        val noteDto: NoteDto = easyRandom.nextObject(NoteDto::class.java)

        every { personService.getAllPersons(any()) } returns listOf(personEntity)
        every { phoneService.getPhoneDtoForPersonIdList(listOf(personEntity.id!!)) } returns
            mapOf(
                personEntity.id!! to
                    listOf(
                        phoneDto,
                    ),
            )
        every { noteService.findAllByPersonId(personEntity.id!!) } returns listOf(noteDto)

        val persons = overviewService.getAllPersons(personEntity.userId)

        assertThat(persons).isNotNull
        assertThat(persons).hasSize(1)
        assertThat(persons.first()).isEqualTo(
            PersonDto.from(personEntity, listOf(phoneDto), listOf(noteDto)),
        )
    }

    @Test
    fun `get person by id - get personDto`() {
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        val phoneDto: PhoneDto = easyRandom.nextObject(PhoneDto::class.java)
        val noteDto: NoteDto = easyRandom.nextObject(NoteDto::class.java)

        every { personService.getPersonEntityById(personEntity.id!!, personEntity.userId) } returns personEntity
        every { phoneService.getPhoneDtoForPersonId(personEntity.id!!) } returns listOf(phoneDto)
        every { noteService.findAllByPersonId(personEntity.id!!) } returns listOf(noteDto)

        val persons = overviewService.getPersonById(personEntity.id!!, personEntity.userId)

        assertThat(persons).isNotNull
        assertThat(persons).isEqualTo(
            PersonDto.from(personEntity, listOf(phoneDto), listOf(noteDto)),
        )
    }

    @Test
    fun `upsert person - get personDto`() {
        val personDto: PersonDto = easyRandom.nextObject(PersonDto::class.java)
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        val userId: UUID = UUID.randomUUID()

        every { personService.upsertPerson(personDto, userId) } returns personEntity
        every { phoneService.upsertPhoneForPerson(personEntity, personDto.phones) } returns listOf()
        every { noteService.upsertNoteForPerson(personEntity, personDto.notes) } returns listOf()

        val persons = overviewService.upsertPerson(personDto, userId)

        assertThat(persons).isNotNull
        assertThat(persons).isEqualTo(
            PersonDto.from(personEntity, listOf(), listOf()),
        )
    }
}
