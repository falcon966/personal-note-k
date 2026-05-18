package de.tuantu.personalnotek.persistence

import de.tuantu.personalnotek.persistence.model.PersonEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface PersonRepository : JpaRepository<PersonEntity, UUID>
