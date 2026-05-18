package de.tuantu.personalnotek.persistence.model

import jakarta.persistence.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "person")
class PersonEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @Column(name = "first_name", nullable = false, length = 50)
    var firstName: String,
    @Column(name = "last_name", nullable = false, length = 50)
    var lastName: String,
    @Column
    var email: String? = null,
    @Column(name = "created_at", updatable = false)
    var createdAt: OffsetDateTime? = null,
) {
    @PrePersist
    protected fun onCreate() {
        createdAt = OffsetDateTime.now()
    }
}
