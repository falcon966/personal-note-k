package de.tuantu.personalnotek.persistence

import de.tuantu.personalnotek.persistence.model.PersonEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface PersonRepository : JpaRepository<PersonEntity, UUID>{

    @Query("SELECT p FROM PersonEntity p WHERE p.userId = :userId")
    fun findAllByUserId(userId: UUID): List<PersonEntity>

    @Query("SELECT p FROM PersonEntity p WHERE p.userId = :userId AND p.id = :id")
    fun findByIdAndUserId(userId: UUID, id: UUID): PersonEntity?
}
