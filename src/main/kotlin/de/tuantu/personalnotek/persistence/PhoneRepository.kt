package de.tuantu.personalnotek.persistence

import de.tuantu.personalnotek.persistence.model.PhoneEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface PhoneRepository : JpaRepository<PhoneEntity, UUID> {
    fun findByPersonId(personId: UUID): List<PhoneEntity>

    fun findByPersonIdIn(personIds: List<UUID>): List<PhoneEntity>
}
