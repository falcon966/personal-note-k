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
import java.util.UUID

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
        val id = UUID.randomUUID()

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

    @Test
    fun `upsert phone for person - update phone - return phoneDto`() {
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity.id = null
        val savedPerson = personRepository.save(personEntity)
        val currentPhone: PhoneEntity = easyRandom.nextObject(PhoneEntity::class.java)
        currentPhone.id = null
        currentPhone.person = savedPerson
        val savedPhone = phoneRepository.save(currentPhone)

        val phoneDto: PhoneDto = PhoneDto.from(savedPhone).copy(name = "Updated name")

        phoneService.upsertPhoneForPerson(savedPerson, listOf(phoneDto))

        val phone = phoneRepository.findAll()
        assertThat(phone).hasSize(1)
        assertThat(phone.first())
            .usingRecursiveComparison()
            .isEqualTo(PhoneDto.toEntity(phoneDto, savedPerson))
    }

    @Test
    fun `upsert phone for person - save new phone delete deprecated ones - return phoneDto`() {
        val phoneDto: PhoneDto = easyRandom.nextObject(PhoneDto::class.java).copy(id = null)
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity.id = null
        val savedPerson = personRepository.save(personEntity)
        val phoneEntityOld: PhoneEntity = easyRandom.nextObject(PhoneEntity::class.java)
        phoneEntityOld.id = null
        phoneEntityOld.person = savedPerson
        phoneRepository.save(phoneEntityOld)

        phoneService.upsertPhoneForPerson(savedPerson, listOf(phoneDto))

        val targetPhone =
            PhoneDto.toEntity(
                phoneDto = phoneDto,
                personEntity = savedPerson,
            )
        val phone = phoneRepository.findAll()
        assertThat(phone).hasSize(1)
        assertThat(phone.first())
            .usingRecursiveComparison()
            .ignoringFields("id")
            .isEqualTo(targetPhone)
    }
}
