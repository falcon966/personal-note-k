package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.persistence.model.PersonEntity
import de.tuantu.personalnotek.service.domain.NoteDto
import de.tuantu.personalnotek.service.domain.PersonDto
import de.tuantu.personalnotek.service.domain.PhoneDto
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class OverviewService(
    val personService: PersonService,
    val phoneService: PhoneService,
    val noteService: NoteService
) {
    fun getAllPersons(userId: UUID): List<PersonDto> {
        val persons: List<PersonEntity> = personService.getAllPersons(userId)
        val personToPhonesDtoMap: Map<UUID, List<PhoneDto>> =
            phoneService.getPhoneDtoForPersonIdList(persons.mapNotNull { it.id })
        val personToNotesDtoMap: Map<UUID, List<NoteDto>> =
            persons.mapNotNull { it.id }.associateWith { noteService.findAllByPersonId(it) }
        return persons.mapNotNull { personEntity ->
            personEntity.id?.let {
                PersonDto.from(
                    personEntity,
                    personToPhonesDtoMap.getOrDefault(it, emptyList()),
                    personToNotesDtoMap.getOrDefault(it, emptyList())
                )
            }
        }
    }

    fun getPersonById(id: UUID, userId: UUID): PersonDto? {
        val personEntity = personService.getPersonEntityById(id, userId)
        val personId = personEntity.id ?: throw IllegalArgumentException("Person with id $id not found")
        return personEntity.let {
            PersonDto.from(it, phoneService.getPhoneDtoForPersonId(personId), noteService.findAllByPersonId(personId))
        }
    }

    fun upsertPerson(personDto: PersonDto, userId: UUID): PersonDto {
        val personEntity = personService.upsertPerson(personDto, userId)
        val phoneList = phoneService.upsertPhoneForPerson(personEntity, personDto.phones)
        val noteList = noteService.upsertNoteForPerson(personEntity, personDto.notes)
        return PersonDto.from(personEntity, phoneList, noteList)
    }
}
