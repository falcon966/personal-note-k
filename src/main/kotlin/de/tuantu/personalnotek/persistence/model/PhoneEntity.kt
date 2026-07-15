package de.tuantu.personalnotek.persistence.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "phone")
class PhoneEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID?,
    @Column(name = "user_id", nullable = false)
    var userId: UUID,
    @Column(name = "number", nullable = false)
    var number: String,
    @Column(name = "name", nullable = false)
    var name: String,
    @ManyToOne
    @JoinColumn(name = "person_id")
    var person: PersonEntity? = null,
)
