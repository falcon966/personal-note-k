package de.tuantu.personalnotek.service

import de.tuantu.personalnotek.persistence.PhoneRepository
import de.tuantu.personalnotek.service.domain.PhoneDto
import org.springframework.stereotype.Service
import java.util.*

@Service
class PhoneService(
    val phoneRepository: PhoneRepository,
) {
    fun getPhoneById(id: UUID): PhoneDto? =
        phoneRepository
            .findById(id)
            .orElseThrow {
                NoSuchElementException("Phone with id $id not found")
            }.let { PhoneDto.from(it) }

    fun getPhoneDtoForPersonId(personId: UUID): List<PhoneDto> =
        phoneRepository
            .findByPersonId(
                personId,
            ).map { PhoneDto.from(it) }

    fun getPhoneDtoForPersonIdList(personIds: List<UUID>): Map<UUID, List<PhoneDto>> {
        val phoneEntities = phoneRepository.findByPersonIdIn(personIds)
        return phoneEntities
            .mapNotNull { phone -> phone.person?.id?.let { id -> id to phone } }
            .groupBy({ it.first }, { PhoneDto.from(it.second) })
    }
}
