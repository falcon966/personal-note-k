package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.persistence.model.PersonEntity
import de.tuantu.personalnotek.service.domain.PersonDto
import de.tuantu.personalnotek.service.domain.PhoneDto
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OverviewService(
    val personService: PersonService,
    val phoneService: PhoneService,
) {
    fun getAllPersons(): List<PersonDto> {
        val persons: List<PersonEntity> = personService.getAllPersons()
        val personToPhonesDtoMap: Map<UUID, List<PhoneDto>> =
            phoneService.getPhoneDtoForPersonIdList(persons.mapNotNull { it.id })
        return persons.mapNotNull { personEntity ->
            personEntity.id?.let {
                PersonDto.from(personEntity, personToPhonesDtoMap.getOrDefault(it, emptyList()))
            }
        }
    }

    fun getPersonById(id: UUID): PersonDto? {
        val personEntity = personService.getPersonEntityById(id)
        return personEntity?.let {
            PersonDto.from(it, phoneService.getPhoneDtoForPersonId(it.id!!))
        }
    }
}
