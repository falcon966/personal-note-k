package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.persistence.PersonRepository
import de.tuantu.personalnotek.persistence.model.PersonEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class PersonService(
    val personRepository: PersonRepository,
) {
    fun getPersonEntityById(id: UUID): PersonEntity? =
        personRepository.findById(id).orElseThrow {
            NoSuchElementException("Person with id $id not found")
        }

    fun getAllPersons(): List<PersonEntity> = personRepository.findAll()
}
