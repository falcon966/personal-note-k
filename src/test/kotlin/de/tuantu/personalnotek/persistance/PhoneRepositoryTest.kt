package de.tuantu.personalnotek.persistance

import de.tuantu.personalnotek.RepositoryTest
import de.tuantu.personalnotek.persistence.PersonRepository
import de.tuantu.personalnotek.persistence.PhoneRepository
import de.tuantu.personalnotek.persistence.model.PersonEntity
import de.tuantu.personalnotek.persistence.model.PhoneEntity
import org.assertj.core.api.Assertions.assertThat
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.jvm.optionals.getOrNull

@RepositoryTest
class PhoneRepositoryTest {
    @Autowired
    lateinit var phoneRepository: PhoneRepository

    @Autowired
    lateinit var personRepository: PersonRepository

    private val easyRandom: EasyRandom = EasyRandom()

    @Test
    fun `get phone by id - get phone`() {
        // given
        val phoneEntity: PhoneEntity = easyRandom.nextObject(PhoneEntity::class.java)
        phoneEntity.id = null
        val savedPhone = phoneRepository.save(phoneEntity)

        // when
        val phone = phoneRepository.findById(savedPhone.id!!).getOrNull()

        // then
        assertThat(savedPhone)
            .usingRecursiveComparison()
            .isEqualTo(phone)
    }

    @Test
    fun `get phone by person id - get phones for person`() {
        // given
        val personEntity: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        personEntity.id = null
        val savedPerson = personRepository.save(personEntity)
        val phoneEntity: PhoneEntity = easyRandom.nextObject(PhoneEntity::class.java)
        phoneEntity.id = null
        phoneEntity.person = savedPerson
        val savedPhone = phoneRepository.save(phoneEntity)

        // when
        val phones = phoneRepository.findByPersonId(savedPerson.id!!)

        // then
        assertThat(phones).hasSize(1)
        assertThat(phones.first()).isEqualTo(savedPhone)
    }
}
