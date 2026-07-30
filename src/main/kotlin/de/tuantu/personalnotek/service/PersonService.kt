package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.persistence.PersonRepository
import de.tuantu.personalnotek.persistence.model.PersonEntity
import de.tuantu.personalnotek.service.domain.PersonDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class PersonService(
    val personRepository: PersonRepository,
) {
    fun getPersonEntityById(id: UUID, userId: UUID): PersonEntity =
        personRepository.findByIdAndUserId(userId, id) ?: throw
            NoSuchElementException("Person with id $id not found")

    fun getAllPersons(
        userId: UUID
    ): List<PersonEntity> = personRepository.findAllByUserId(userId)

    @Transactional
    fun upsertPerson(personDto: PersonDto, userId: UUID): PersonEntity =
        personRepository.save(PersonDto.toEntity(personDto, userId))
}
