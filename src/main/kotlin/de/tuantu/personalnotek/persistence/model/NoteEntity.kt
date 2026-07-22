package de.tuantu.personalnotek.persistence.model

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "note")
class NoteEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = true)
    var person: PersonEntity? = null,

    @Column(name = "text", nullable = true)
    var text: String? = null,

) {}