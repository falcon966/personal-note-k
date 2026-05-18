package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.RepositoryTest
import de.tuantu.personalnotek.persistence.PersonRepository
import de.tuantu.personalnotek.persistence.PhoneRepository
import de.tuantu.personalnotek.persistence.model.PersonEntity
import de.tuantu.personalnotek.persistence.model.PhoneEntity
import de.tuantu.personalnotek.service.domain.PhoneDto
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@RepositoryTest
class PhoneServiceTest {
    @Autowired
    lateinit var phoneRepository: PhoneRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    lateinit var phoneService: PhoneService

    val easyRandom: EasyRandom = EasyRandom()

    @BeforeEach
    fun beforeEach() {
        phoneService = PhoneService(phoneRepository)
    }

    @Test
    fun `find phone by id - get phoneDto`() {
        // given
        val phoneEntity: PhoneEntity = easyRandom.nextObject(PhoneEntity::class.java)
        phoneEntity.id = null
        val savedPhone = phoneRepository.save(phoneEntity)

        // when
        val phoneDto = phoneService.getPhoneById(phoneEntity.id!!)

        // then
        assertThat(phoneDto)
            .usingRecursiveComparison()
            .ignoringFields("id", "createdAt")
            .isEqualTo(PhoneDto.from(savedPhone))
    }

    @Test
    fun `find phone by id - not found - throw NoSuchElementException`() {
        val id = java.util.UUID.randomUUID()

        assertThatThrownBy {
            phoneService.getPhoneById(id)
        }.isInstanceOf(NoSuchElementException::class.java)
            .hasMessage("Phone with id $id not found")
    }

    @Test
    fun `find phones by person id - get phoneDto`() {
        // given
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity.id = null
        val savedPerson = personRepository.save(personEntity)
        val phoneEntity: PhoneEntity = easyRandom.nextObject(PhoneEntity::class.java)
        phoneEntity.id = null
        phoneEntity.person = savedPerson
        val savedPhone = phoneRepository.save(phoneEntity)

        // when
        val phones = phoneService.getPhoneDtoForPersonId(savedPerson.id!!)

        // then
        assertThat(phones).hasSize(1)
        assertThat(phones.first()).isEqualTo(
            PhoneDto.from(savedPhone),
        )
    }

    @Test
    fun `find phones for list of person ids - get phoneDto`() {
        // given
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity.id = null
        val savedPerson = personRepository.save(personEntity)
        val personEntity2: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity2.id = null
        val savedPerson2 = personRepository.save(personEntity)

        val phoneEntity: PhoneEntity = easyRandom.nextObject(PhoneEntity::class.java)
        phoneEntity.id = null
        phoneEntity.person = savedPerson
        val savedPhoneExpected = phoneRepository.save(phoneEntity)

        val phoneEntity2: PhoneEntity = easyRandom.nextObject(PhoneEntity::class.java)
        phoneEntity2.id = null
        phoneEntity2.person = savedPerson2
        phoneRepository.save(phoneEntity)

        // when
        val phones = phoneService.getPhoneDtoForPersonIdList(listOf(savedPerson.id!!))

        // then
        assertThat(phones).hasSize(1)
        assertThat(phones[savedPerson.id!!])
            .usingRecursiveComparison()
            .isEqualTo(
                listOf(PhoneDto.from(savedPhoneExpected)),
            )
    }
}
