package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.RepositoryTest
import de.tuantu.personalnotek.persistence.PersonRepository
import de.tuantu.personalnotek.persistence.model.PersonEntity
import de.tuantu.personalnotek.service.domain.PersonDto
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import java.util.*
import kotlin.test.Test

@RepositoryTest
class PersonServiceTest {
    @Autowired
    lateinit var personRepository: PersonRepository

    lateinit var personService: PersonService

    private val easyRandom: EasyRandom = EasyRandom()

    @BeforeEach
    fun beforeEach() {
        personService = PersonService(personRepository)
    }

    @Test
    fun `find person with id - get personEntity`() {
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity.id = null
        val savedPerson = personRepository.save(personEntity)

        val personDto = personService.getPersonEntityById(savedPerson.id!!, savedPerson.userId)

        assertThat(personDto)
            .isEqualTo(
                savedPerson,
            )
    }

    @Test
    fun `find person with id - not found - throw NoSuchElementException`() {
        val id = UUID.randomUUID()

        assertThatThrownBy {
            personService.getPersonEntityById(id, UUID.randomUUID())
        }.isInstanceOf(NoSuchElementException::class.java)
            .hasMessage("Person with id $id not found")
    }

    @Test
    fun `upsert person - save new person - return personEntity`() {
        val personDto: PersonDto =
            easyRandom.nextObject(PersonDto::class.java).copy(
                id = null,
                phones = emptyList(),
            )

        val userId: UUID = UUID.randomUUID()

        personService.upsertPerson(personDto, userId)

        assertThat(personRepository.findAll()).hasSize(1)

        assertThat(personRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("id", "createdAt")
            .isEqualTo(
                PersonDto.toEntity(personDto, userId),
            )
    }

    @Test
    fun `upsert person - update person - return personEntity`() {
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity.id = null
        val currentPerson = personRepository.saveAndFlush(personEntity)

        val personDto: PersonDto =
            PersonDto.from(currentPerson, emptyList()).copy(
                firstname = "Updated firstName",
                lastname = "Updated Lastname",
            )

        personService.upsertPerson(personDto, currentPerson.userId)

        assertThat(personRepository.findAll()).hasSize(1)

        assertThat(personRepository.findAll().first())
            .usingRecursiveComparison()
            .ignoringFields("createdAt")
            .isEqualTo(
                PersonDto.toEntity(personDto, currentPerson.userId),
            )
    }
}
