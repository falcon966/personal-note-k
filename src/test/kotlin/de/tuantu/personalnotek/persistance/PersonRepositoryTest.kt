package de.tuantu.personalnotek.persistance

import de.tuantu.personalnotek.RepositoryTest
import de.tuantu.personalnotek.persistence.PersonRepository
import de.tuantu.personalnotek.persistence.model.PersonEntity
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.jeasy.random.EasyRandom
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

@RepositoryTest
class PersonRepositoryTest {
    @Autowired
    lateinit var personRepository: PersonRepository

    private val easyRandom: EasyRandom = EasyRandom()

    @Test
    fun testFindAll() {
        // given
        val person: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        person.id = null
        val person2: PersonEntity = easyRandom.nextObject(PersonEntity::class.java)
        person2.id = null
        personRepository.saveAndFlush(person)
        personRepository.saveAndFlush(person2)

        // when
        val persons: List<PersonEntity> = personRepository.findAll()

        // then
        AssertionsForInterfaceTypes.assertThat(persons).hasSize(2)
        AssertionsForInterfaceTypes
            .assertThat(persons)
            .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id", "createdAt")
            .contains(person, person2)
    }
}
