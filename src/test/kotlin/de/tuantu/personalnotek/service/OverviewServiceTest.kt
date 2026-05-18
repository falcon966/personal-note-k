package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.persistence.model.PersonEntity
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

@ExtendWith(MockKExtension::class)
class OverviewServiceTest {
    @MockK
    lateinit var personService: PersonService

    @MockK
    lateinit var phoneService: PhoneService

    @InjectMockKs
    lateinit var overviewService: OverviewService

    val easyRandom: EasyRandom = EasyRandom()

    @Test
    fun `get all persons - get all personDtos`() {
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        val phoneDto: PhoneDto = easyRandom.nextObject(PhoneDto::class.java)

        every { personService.getAllPersons() } returns listOf(personEntity)
        every { phoneService.getPhoneDtoForPersonIdList(listOf(personEntity.id!!)) } returns
            mapOf(
                personEntity.id!! to
                    listOf(
                        phoneDto,
                    ),
            )

        val persons = overviewService.getAllPersons()

        assertThat(persons).isNotNull
        assertThat(persons).hasSize(1)
        assertThat(persons.first()).isEqualTo(
            PersonDto.from(personEntity, listOf(phoneDto)),
        )
    }

    @Test
    fun `get person by id - get personDto`() {
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        val phoneDto: PhoneDto = easyRandom.nextObject(PhoneDto::class.java)

        every { personService.getPersonEntityById(personEntity.id!!) } returns personEntity
        every { phoneService.getPhoneDtoForPersonId(personEntity.id!!) } returns listOf(phoneDto)

        val persons = overviewService.getPersonById(personEntity.id!!)

        assertThat(persons).isNotNull
        assertThat(persons).isEqualTo(
            PersonDto.from(personEntity, listOf(phoneDto)),
        )
    }
}
